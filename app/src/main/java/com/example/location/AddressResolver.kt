package com.example.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class AddressResolver(private val context: Context) {

    private val cache = ConcurrentHashMap<String, String>()

    suspend fun getAddress(latitude: Double, longitude: Double): String = withContext(Dispatchers.IO) {
        val cacheKey = "${String.format(Locale.US, "%.4f", latitude)},${String.format(Locale.US, "%.4f", longitude)}"
        cache[cacheKey]?.let { return@withContext it }

        if (!Geocoder.isPresent()) {
            return@withContext "Coordinates ($cacheKey)"
        }

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var resultAddress = ""
                // Synchronous fallback wrapper for coroutine
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                if (!list.isNullOrEmpty()) {
                    resultAddress = formatAddress(list[0])
                }
                if (resultAddress.isNotEmpty()) {
                    cache[cacheKey] = resultAddress
                    return@withContext resultAddress
                }
            } else {
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                if (!list.isNullOrEmpty()) {
                    val formatted = formatAddress(list[0])
                    cache[cacheKey] = formatted
                    return@withContext formatted
                }
            }
        } catch (e: Exception) {
            // Geocoder service can sometimes fail or timeout on emulators/offline
        }

        val fallback = "${String.format(Locale.US, "%.5f", latitude)}°, ${String.format(Locale.US, "%.5f", longitude)}°"
        cache[cacheKey] = fallback
        return@withContext fallback
    }

    private fun formatAddress(address: Address): String {
        val parts = mutableListOf<String>()
        val thoroughfare = address.thoroughfare
        val subThoroughfare = address.subThoroughfare
        if (!thoroughfare.isNullOrBlank()) {
            if (!subThoroughfare.isNullOrBlank()) {
                parts.add("$subThoroughfare $thoroughfare")
            } else {
                parts.add(thoroughfare)
            }
        } else if (!address.featureName.isNullOrBlank() && address.featureName != address.locality) {
            parts.add(address.featureName)
        }

        address.locality?.let { if (it.isNotBlank()) parts.add(it) }
        address.adminArea?.let { if (it.isNotBlank()) parts.add(it) }
        address.countryName?.let { if (it.isNotBlank()) parts.add(it) }

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            address.getAddressLine(0) ?: "Location verified"
        }
    }
}
