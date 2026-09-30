package com.example.utils

import android.content.Context
import android.os.Build
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun getCurrentFormatted(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return SimpleDateFormat(pattern, Locale.US).format(Date())
    }
}

object DeviceUtils {
    val deviceName: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"

    val androidVersion: String
        get() = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
}

object FileUtils {
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    fun doesFileExist(path: String): Boolean {
        return File(path).exists()
    }
}
