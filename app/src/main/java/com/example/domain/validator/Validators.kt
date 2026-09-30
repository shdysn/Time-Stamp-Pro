package com.example.domain.validator

import com.example.data.model.LocationData
import com.example.data.model.UserSettings

object LocationValidator {
    fun isValid(location: LocationData): Boolean {
        val validLat = location.latitude in -90.0..90.0
        val validLng = location.longitude in -180.0..180.0
        return validLat && validLng && (location.latitude != 0.0 || location.longitude != 0.0)
    }
}

object SettingsValidator {
    fun validate(settings: UserSettings): Boolean {
        return settings.dateFormat.isNotBlank() && settings.backgroundOpacity in 0.0f..1.0f
    }
}

object TimestampValidator {
    fun isReasonableTimestamp(millis: Long): Boolean {
        val now = System.currentTimeMillis()
        val yearMillis = 365L * 24 * 60 * 60 * 1000
        return millis in (now - 5 * yearMillis)..(now + 24 * 60 * 60 * 1000)
    }
}
