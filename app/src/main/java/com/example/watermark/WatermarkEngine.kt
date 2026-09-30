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

        // 1. Draw Custom Text Overlay if enabled (with Size, Bold, Italic, Underline, and Color)
        if (settings.isCustomTextEnabled && settings.customText.isNotBlank()) {
            val customTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = settings.customTextColorHex.toInt()
                textSize = (settings.customTextSize * 1.6f) * finalScale
                isUnderlineText = settings.isCustomTextUnderline
                val fontStyle = when {
                    settings.isCustomTextBold && settings.isCustomTextItalic -> Typeface.BOLD_ITALIC
                    settings.isCustomTextBold -> Typeface.BOLD
                    settings.isCustomTextItalic -> Typeface.ITALIC
                    else -> Typeface.NORMAL
                }
                typeface = Typeface.create(Typeface.DEFAULT, fontStyle)
                setShadowLayer(6f * finalScale, 2f * finalScale, 2f * finalScale, Color.BLACK)
            }

            val textLines = settings.customText.split("\n")
            val customLineHeight = customTextPaint.textSize * 1.25f

            val customPadding = 20f * finalScale
            var maxTextW = 0f
            for (tl in textLines) {
                maxTextW = max(maxTextW, customTextPaint.measureText(tl))
            }
            val customBoxW = (maxTextW + (customPadding * 2)).coerceAtMost(imageWidth - 40f * finalScale)
            val customBoxH = (textLines.size * customLineHeight) + (customPadding * 1.5f)

            val customMarginTop = 48f * finalScale
            val customLeft = (imageWidth - customBoxW) / 2f
            val customRect = RectF(customLeft, customMarginTop, customLeft + customBoxW, customMarginTop + customBoxH)

            val customBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                alpha = 150
                style = Paint.Style.FILL
            }
            val customCorner = 14f * finalScale
            canvas.drawRoundRect(customRect, customCorner, customCorner, customBgPaint)

            var customY = customRect.top + customPadding + (customTextPaint.textSize * 0.82f)
            for (tl in textLines) {
                val lineX = customRect.left + (customBoxW - customTextPaint.measureText(tl)) / 2f
                canvas.drawText(tl, lineX, customY, customTextPaint)
                customY += customLineHeight
            }
        }

        // 2. Draw Timestamp Stamp only if isStampVisible is true
        if (settings.isStampVisible) {
            // Assemble stamp text lines
            val lines = mutableListOf<String>()

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

            // Mock GPS Warning if detected
            if (location.isMock) {
                lines.add("⚠ WARNING: MOCK / SIMULATED GPS DETECTED")
            }

            if (lines.isNotEmpty()) {
                // Measure text dimensions
                var maxLineWidth = 0f
                for (line in lines) {
                    maxLineWidth = max(maxLineWidth, textPaint.measureText(line))
                }

                val textLineHeight = baseFontSize + lineSpacing
                val totalTextHeight = lines.size * textLineHeight

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
                for (line in lines) {
                    val paintToUse = when {
                        line.startsWith("⚠ WARNING") -> warningPaint
                        line.startsWith("LOC:") -> subTextPaint
                        else -> textPaint
                    }
                    canvas.drawText(line, bgRect.left + padding, currentY, paintToUse)
                    currentY += textLineHeight
                }
            }
        }

        return outputBitmap
    }
}
