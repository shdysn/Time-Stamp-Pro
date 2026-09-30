package com.example.security

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.SystemClock
import android.provider.Settings

class MockGpsDetector(private val context: Context) {

    fun isLocationMock(location: Location?): Boolean {
        if (location == null) return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            location.isMock
        } else {
            @Suppress("DEPRECATION")
            location.isFromMockProvider
        }
    }

    fun isMockLocationSettingsEnabled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION
                ) != "0"
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

class TimeTamperDetector {
    fun checkTimeDrift(): Long {
        val systemTime = System.currentTimeMillis()
        val elapsedRealtime = SystemClock.elapsedRealtime()
        return kotlin.math.abs(systemTime - (SystemClock.uptimeMillis() + elapsedRealtime))
    }
}

class IntegrityChecker(private val context: Context) {
    fun isDeviceTampered(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
}
