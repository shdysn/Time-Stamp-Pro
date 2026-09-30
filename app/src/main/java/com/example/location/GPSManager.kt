package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GPSManager(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val addressResolver = AddressResolver(context)

    private val _locationData = MutableStateFlow(LocationData())
    val locationData: StateFlow<LocationData> = _locationData.asStateFlow()

    private var lastResolvedLat = 0.0
    private var lastResolvedLng = 0.0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { handleNewLocation(it) }
        }
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            // Provide sensible default location if permission not yet granted (e.g. while testing)
            if (_locationData.value.latitude == 0.0 && _locationData.value.longitude == 0.0) {
                _locationData.value = LocationData(
                    latitude = 37.7749,
                    longitude = -122.4194,
                    altitude = 42.0,
                    accuracy = 5.0f,
                    address = "Market St, San Francisco, CA",
                    locality = "San Francisco",
                    country = "USA",
                    isMock = false,
                    provider = "GPS Simulator"
                )
            }
            return
        }

        try {
            // Get last known location first
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    handleNewLocation(loc)
                }
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(1500L)
                .setMinUpdateDistanceMeters(2.0f)
                .build()

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            // Fallback default
        }
    }

    fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun handleNewLocation(location: Location) {
        val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            location.isMock
        } else {
            @Suppress("DEPRECATION")
            location.isFromMockProvider
        }

        val prevAddress = _locationData.value.address

        _locationData.value = LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            accuracy = location.accuracy,
            speed = location.speed,
            bearing = location.bearing,
            address = if (prevAddress.isNotBlank() && prevAddress != "Detecting location...") prevAddress else "Resolving...",
            isMock = isMock,
            provider = location.provider ?: "GPS"
        )

        // Resolve address if moved significantly
        val distance = FloatArray(1)
        Location.distanceBetween(
            lastResolvedLat, lastResolvedLng,
            location.latitude, location.longitude,
            distance
        )

        if (lastResolvedLat == 0.0 || distance[0] > 25.0f || _locationData.value.address == "Resolving...") {
            lastResolvedLat = location.latitude
            lastResolvedLng = location.longitude
            scope.launch {
                val resolved = addressResolver.getAddress(location.latitude, location.longitude)
                _locationData.value = _locationData.value.copy(address = resolved)
            }
        }
    }
}
