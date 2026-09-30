package com.example.watermark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.data.model.AltitudeUnit
import com.example.data.model.LocationData
import com.example.data.model.StampPosition
import com.example.data.model.TemplateData
import com.example.data.model.UserSettings
import com.example.timestamp.CoordinateFormatter
import com.example.timestamp.DateFormatter
import java.util.Locale
import kotlin.math.max

object WatermarkEngine {

    fun applyWatermark(
        sourceBitmap: Bitmap,
        settings: UserSettings,
        location: LocationData,
        heading: Float,
        timestampMillis: Long = System.currentTimeMillis()
    ): Bitmap {
        // Ensure mutable bitmap
        val outputBitmap = if (sourceBitmap.isMutable) {
            sourceBitmap
        } else {
            sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val canvas = Canvas(outputBitmap)
        val imageWidth = outputBitmap.width.toFloat()
        val imageHeight = outputBitmap.height.toFloat()

        // Base scale based on image resolution (normalize to ~1080p width)
        val refDimension = max(imageWidth, imageHeight)
        val resScale = refDimension / 1400f
        val userScale = settings.fontSize.scale
        val finalScale = resScale * userScale

        val baseFontSize = 26f * finalScale
        val titleFontSize = 28f * finalScale
        val lineSpacing = 6f * finalScale
        val padding = 20f * finalScale

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = settings.textColorHex.toInt()
            textSize = baseFontSize
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(4f * finalScale, 2f * finalScale, 2f * finalScale, Color.BLACK)
        }

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = titleFontSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(4f * finalScale, 2f * finalScale, 2f * finalScale, Color.BLACK)
        }

        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE0E0E0.toInt()
            textSize = baseFontSize * 0.9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            setShadowLayer(3f * finalScale, 1f * finalScale, 1f * finalScale, Color.BLACK)
        }

        val warningPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFF5252.toInt()
            textSize = baseFontSize * 0.9f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        val template = TemplateData.getById(settings.selectedTemplateId)

        // Assemble stamp text lines
        val lines = mutableListOf<String>()

        // Badge / Header line
        val headerTitle = if (settings.showProjectBadge) {
            "● ${template.badgeTitle}"
        } else ""

        // Timestamp
        if (settings.showTimestamp) {
            val formattedDate = DateFormatter.format(timestampMillis, settings.dateFormat)
            val tzOffset = DateFormatter.getTimezoneOffsetString()
            lines.add("TIME: $formattedDate ($tzOffset)")
        }

        // Coordinates
        if (settings.showCoordinates) {
            val coords = CoordinateFormatter.format(
                location.latitude,
                location.longitude,
                settings.coordinateFormat
            )
            val accStr = if (location.accuracy > 0) String.format(Locale.US, " (±%.1fm)", location.accuracy) else ""
            lines.add("GPS:  $coords$accStr")
        }

        // Address
        if (settings.showAddress && location.address.isNotBlank() && location.address != "Detecting location...") {
            lines.add("LOC:  ${location.address}")
        }

        // Altitude & Compass
        val extraSensors = mutableListOf<String>()
        if (settings.showAltitude) {
            val altVal = if (settings.altitudeUnit == AltitudeUnit.METERS) {
                "${String.format(Locale.US, "%.1f", location.altitude)}m"
            } else {
                "${String.format(Locale.US, "%.1f", location.altitude * 3.28084)}ft"
            }
            extraSensors.add("ALT: $altVal")
        }
        if (settings.showCompass) {
            extraSensors.add("DIR: ${CoordinateFormatter.formatBearing(heading)}")
        }
        if (extraSensors.isNotEmpty()) {
            lines.add(extraSensors.joinToString("  |  "))
        }

        // Project / Inspector info
        val projectParts = mutableListOf<String>()
        if (settings.projectName.isNotBlank()) {
            projectParts.add("PROJ: ${settings.projectName}")
        }
        if (settings.inspectorName.isNotBlank()) {
            projectParts.add("BY: ${settings.inspectorName}")
        }
        if (projectParts.isNotEmpty()) {
            lines.add(projectParts.joinToString("  •  "))
        }

        // Notes
        if (settings.customNotes.isNotBlank()) {
            lines.add("NOTE: ${settings.customNotes}")
        }

        // Mock GPS Warning if detected
        if (location.isMock) {
            lines.add("⚠ WARNING: MOCK / SIMULATED GPS DETECTED")
        }

        if (lines.isEmpty() && headerTitle.isEmpty()) {
            return outputBitmap
        }

        // Measure text dimensions
        var maxLineWidth = 0f
        if (headerTitle.isNotEmpty()) {
            maxLineWidth = max(maxLineWidth, titlePaint.measureText(headerTitle))
        }
        for (line in lines) {
            maxLineWidth = max(maxLineWidth, textPaint.measureText(line))
        }

        val textLineHeight = baseFontSize + lineSpacing
        val totalTextHeight = (if (headerTitle.isNotEmpty()) titleFontSize + lineSpacing * 2 else 0f) +
                (lines.size * textLineHeight)

        val boxWidth = maxLineWidth + (padding * 2)
        val boxHeight = totalTextHeight + (padding * 2)

        // Calculate Box Bounds
        val margin = 28f * finalScale
        val bgRect = when (settings.stampPosition) {
            StampPosition.TOP_LEFT -> RectF(margin, margin, margin + boxWidth, margin + boxHeight)
            StampPosition.TOP_RIGHT -> RectF(imageWidth - margin - boxWidth, margin, imageWidth - margin, margin + boxHeight)
            StampPosition.BOTTOM_LEFT -> RectF(margin, imageHeight - margin - boxHeight, margin + boxWidth, imageHeight - margin)
            StampPosition.BOTTOM_RIGHT -> RectF(imageWidth - margin - boxWidth, imageHeight - margin - boxHeight, imageWidth - margin, imageHeight - margin)
            StampPosition.BOTTOM_BANNER -> RectF(0f, imageHeight - boxHeight - margin, imageWidth, imageHeight)
        }

        // Draw Background
        val alpha = (settings.backgroundOpacity * 255).toInt().coerceIn(0, 255)
        if (alpha > 0) {
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                this.alpha = alpha
                style = Paint.Style.FILL
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = settings.textColorHex.toInt()
                this.alpha = (alpha * 0.8f).toInt()
                style = Paint.Style.STROKE
                strokeWidth = 2f * finalScale
            }

            val cornerRadius = 12f * finalScale
            if (settings.stampPosition == StampPosition.BOTTOM_BANNER) {
                canvas.drawRect(bgRect, bgPaint)
                canvas.drawLine(0f, bgRect.top, imageWidth, bgRect.top, borderPaint)
            } else {
                canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint)
                canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, borderPaint)
            }
        }

        // Draw Text
        var currentY = bgRect.top + padding + baseFontSize * 0.8f

        if (headerTitle.isNotEmpty()) {
            titlePaint.color = template.primaryColorHex.toInt()
            canvas.drawText(headerTitle, bgRect.left + padding, currentY, titlePaint)
            currentY += titleFontSize + lineSpacing * 1.5f
        }

        for (line in lines) {
            val paintToUse = when {
                line.startsWith("⚠ WARNING") -> warningPaint
                line.startsWith("LOC:") || line.startsWith("NOTE:") -> subTextPaint
                else -> textPaint
            }
            canvas.drawText(line, bgRect.left + padding, currentY, paintToUse)
            currentY += textLineHeight
        }

        return outputBitmap
    }
}
