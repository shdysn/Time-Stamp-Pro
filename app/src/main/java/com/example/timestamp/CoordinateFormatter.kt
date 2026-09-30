package com.example.timestamp

import com.example.data.model.CoordinateFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

object CoordinateFormatter {

    fun format(lat: Double, lng: Double, format: CoordinateFormat): String {
        return when (format) {
            CoordinateFormat.DECIMAL -> formatDecimal(lat, lng)
            CoordinateFormat.DMS -> formatDMS(lat, lng)
            CoordinateFormat.UTM -> formatUTM(lat, lng)
        }
    }

    fun formatDecimal(lat: Double, lng: Double): String {
        val latDir = if (lat >= 0) "N" else "S"
        val lngDir = if (lng >= 0) "E" else "W"
        return String.format(
            Locale.US,
            "%.6f° %s, %.6f° %s",
            abs(lat),
            latDir,
            abs(lng),
            lngDir
        )
    }

    fun formatDMS(lat: Double, lng: Double): String {
        val latDMS = toDMS(lat, isLatitude = true)
        val lngDMS = toDMS(lng, isLatitude = false)
        return "$latDMS  $lngDMS"
    }

    private fun toDMS(value: Double, isLatitude: Boolean): String {
        val direction = if (isLatitude) {
            if (value >= 0) "N" else "S"
        } else {
            if (value >= 0) "E" else "W"
        }
        val positive = abs(value)
        val degrees = floor(positive).toInt()
        val minutesDouble = (positive - degrees) * 60
        val minutes = floor(minutesDouble).toInt()
        val seconds = (minutesDouble - minutes) * 60
        return String.format(
            Locale.US,
            "%d°%02d'%04.1f\"%s",
            degrees,
            minutes,
            seconds,
            direction
        )
    }

    fun formatUTM(lat: Double, lng: Double): String {
        // Approximate standard UTM grid calculation
        val zone = floor((lng + 180) / 6).toInt() + 1
        val hemisphere = if (lat >= 0) "N" else "S"
        val approxEasting = ((lng + 180) % 6) * 100000 + 200000
        val approxNorthing = (lat + 90) * 110000
        return String.format(
            Locale.US,
            "UTM %d%s  E:%.0fm  N:%.0fm",
            zone,
            hemisphere,
            abs(approxEasting),
            abs(approxNorthing)
        )
    }

    fun formatBearing(degrees: Float): String {
        val normalized = (degrees % 360 + 360) % 360
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                                 "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((normalized + 11.25f) / 22.5f).toInt() % 16
        val cardinal = directions[index]
        return String.format(Locale.US, "%.0f° %s", normalized, cardinal)
    }
}
