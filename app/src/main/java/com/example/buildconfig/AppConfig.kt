package com.example.buildconfig

enum class AppBuildType {
    DEBUG,
    RELEASE,
    STAGING
}

object FeatureFlags {
    var enableMockGpsAlert: Boolean = true
    var enableHighAccuracyLocation: Boolean = true
    var enableDualSaveOriginal: Boolean = true
    var enableCompassBearing: Boolean = true
    var enableAltitudeTracking: Boolean = true
}

object AppConfig {
    const val APP_NAME = "Timestamp Camera Pro"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1
    val currentBuildType: AppBuildType = AppBuildType.DEBUG
}
