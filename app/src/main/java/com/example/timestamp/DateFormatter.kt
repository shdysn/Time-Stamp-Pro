package com.example.timestamp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    fun format(
        timestampMillis: Long = System.currentTimeMillis(),
        pattern: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            sdf.timeZone = timeZone
            sdf.format(Date(timestampMillis))
        } catch (e: Exception) {
            val fallback = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            fallback.format(Date(timestampMillis))
        }
    }

    fun getTimezoneOffsetString(timeZone: TimeZone = TimeZone.getDefault()): String {
        val offsetMillis = timeZone.getOffset(System.currentTimeMillis())
        val hours = offsetMillis / (1000 * 60 * 60)
        val minutes = kotlin.math.abs(offsetMillis / (1000 * 60)) % 60
        val sign = if (hours >= 0) "+" else "-"
        return String.format(Locale.US, "GMT%s%02d:%02d", sign, kotlin.math.abs(hours), minutes)
    }

    val COMMON_DATE_FORMATS = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "MM/dd/yyyy hh:mm:ss a",
        "dd/MM/yyyy HH:mm:ss",
        "yyyy.MM.dd 'at' HH:mm:ss z",
        "EEE, dd MMM yyyy HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )
}
