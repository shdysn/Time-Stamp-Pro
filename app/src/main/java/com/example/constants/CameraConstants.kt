package com.example.constants

object CameraConstants {
    const val DEFAULT_ZOOM = 1.0f
    const val MAX_ZOOM = 8.0f
    const val JPEG_QUALITY = 92
    const val FALLBACK_WIDTH = 1280
    const val FALLBACK_HEIGHT = 960
    const val SHUTTER_ANIMATION_DURATION_MS = 100L
}

object LocationConstants {
    const val UPDATE_INTERVAL_MS = 3000L
    const val FASTEST_INTERVAL_MS = 1500L
    const val MIN_DISTANCE_METERS = 2.0f
    const val SIGNIFICANT_MOVE_METERS = 25.0f
    const val DEFAULT_LATITUDE = 37.7749
    const val DEFAULT_LONGITUDE = -122.4194
}

object StorageConstants {
    const val DIRECTORY_IMAGES = "images"
    const val DIRECTORY_BACKUP = "backups"
    const val GALLERY_ALBUM_NAME = "TimestampCameraPro"
    const val PREFIX_STAMP = "STAMP_"
    const val PREFIX_ORIGINAL = "ORIG_"
}

object TimestampConstants {
    const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
    const val TICK_INTERVAL_MS = 1000L
}
