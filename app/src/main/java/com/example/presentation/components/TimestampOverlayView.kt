package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AltitudeUnit
import com.example.data.model.LocationData
import com.example.data.model.StampPosition
import com.example.data.model.TemplateData
import com.example.data.model.UserSettings
import com.example.presentation.designsystem.AppColors
import com.example.timestamp.CoordinateFormatter
import com.example.timestamp.DateFormatter
import java.util.Locale

@Composable
fun TimestampOverlayView(
    settings: UserSettings,
    location: LocationData,
    heading: Float,
    currentTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    val template = TemplateData.getById(settings.selectedTemplateId)
    val stampColor = Color(settings.textColorHex)
    val bgAlpha = settings.backgroundOpacity.coerceIn(0f, 1f)
    val baseFontSize = (12f * settings.fontSize.scale).sp
    val titleFontSize = (13f * settings.fontSize.scale).sp

    val boxAlignment = when (settings.stampPosition) {
        StampPosition.TOP_LEFT -> Alignment.TopStart
        StampPosition.TOP_RIGHT -> Alignment.TopEnd
        StampPosition.BOTTOM_LEFT -> Alignment.BottomStart
        StampPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
        StampPosition.BOTTOM_BANNER -> Alignment.BottomCenter
    }

    val isBanner = settings.stampPosition == StampPosition.BOTTOM_BANNER

    Box(
        modifier = modifier.padding(if (isBanner) 0.dp else 16.dp),
        contentAlignment = boxAlignment
    ) {
        Column(
            modifier = Modifier
                .then(if (isBanner) Modifier.fillMaxWidth() else Modifier)
                .background(
                    color = Color.Black.copy(alpha = bgAlpha),
                    shape = if (isBanner) RoundedCornerShape(0.dp) else RoundedCornerShape(8.dp)
                )
                .border(
                    width = if (isBanner) 0.dp else 1.dp,
                    color = stampColor.copy(alpha = 0.6f),
                    shape = if (isBanner) RoundedCornerShape(0.dp) else RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Template Header / Badge
            if (settings.showProjectBadge) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(template.primaryColorHex),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = template.badgeTitle,
                            color = Color.White,
                            fontSize = (titleFontSize.value * 0.85f).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (location.isMock) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Mock GPS",
                                tint = AppColors.AccentRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "MOCK GPS",
                                color = AppColors.AccentRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Timestamp
            if (settings.showTimestamp) {
                val formattedDate = DateFormatter.format(currentTimeMillis, settings.dateFormat)
                val tzOffset = DateFormatter.getTimezoneOffsetString()
                Text(
                    text = "TIME: $formattedDate ($tzOffset)",
                    color = stampColor,
                    fontSize = baseFontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Coordinates
            if (settings.showCoordinates) {
                val coords = CoordinateFormatter.format(
                    location.latitude,
                    location.longitude,
                    settings.coordinateFormat
                )
                val acc = if (location.accuracy > 0) String.format(Locale.US, " (±%.1fm)", location.accuracy) else ""
                Text(
                    text = "GPS:  $coords$acc",
                    color = stampColor,
                    fontSize = baseFontSize,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Address
            if (settings.showAddress && location.address.isNotBlank() && location.address != "Detecting location...") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = stampColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = location.address,
                        color = Color(0xFFE2E8F0),
                        fontSize = (baseFontSize.value * 0.95f).sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }
            }

            // Altitude & Heading
            val hasAlt = settings.showAltitude
            val hasCompass = settings.showCompass
            if (hasAlt || hasCompass) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (hasAlt) {
                        val altText = if (settings.altitudeUnit == AltitudeUnit.METERS) {
                            "${String.format(Locale.US, "%.1f", location.altitude)}m"
                        } else {
                            "${String.format(Locale.US, "%.1f", location.altitude * 3.28084)}ft"
                        }
                        Text(
                            text = "ALT: $altText",
                            color = stampColor,
                            fontSize = baseFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (hasCompass) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Compass",
                                tint = AppColors.AccentCyan,
                                modifier = Modifier
                                    .size(14.dp)
                                    .rotate(-heading)
                            )
                            Text(
                                text = "DIR: ${CoordinateFormatter.formatBearing(heading)}",
                                color = stampColor,
                                fontSize = baseFontSize,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Project / Inspector
            val hasProj = settings.projectName.isNotBlank()
            val hasInspector = settings.inspectorName.isNotBlank()
            if (hasProj || hasInspector) {
                val projText = buildString {
                    if (hasProj) append("PROJ: ${settings.projectName}")
                    if (hasProj && hasInspector) append(" • ")
                    if (hasInspector) append("BY: ${settings.inspectorName}")
                }
                Text(
                    text = projText,
                    color = Color.White,
                    fontSize = (baseFontSize.value * 0.95f).sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Notes
            if (settings.customNotes.isNotBlank()) {
                Text(
                    text = "NOTE: ${settings.customNotes}",
                    color = Color(0xFFCBD5E1),
                    fontSize = (baseFontSize.value * 0.9f).sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
