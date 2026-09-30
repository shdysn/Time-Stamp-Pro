package com.example.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.camera.FlashMode
import com.example.data.model.TemplateData
import com.example.presentation.designsystem.AppColors

@Composable
fun ShutterButton(
    onClick: () -> Unit,
    isCapturing: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed || isCapturing) 0.88f else 1.0f, label = "shutter_scale")

    Box(
        modifier = modifier
            .size(76.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(4.dp, Color.White, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isCapturing,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isCapturing) AppColors.AccentGold else Color.White)
        )
    }
}

@Composable
fun FlashButton(
    flashMode: FlashMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (flashMode) {
        FlashMode.OFF -> Pair(Icons.Default.FlashOff, Color.White.copy(alpha = 0.7f))
        FlashMode.ON -> Pair(Icons.Default.FlashOn, AppColors.AccentGold)
        FlashMode.AUTO -> Pair(Icons.Default.FlashAuto, Color.White)
        FlashMode.TORCH -> Pair(Icons.Default.Highlight, AppColors.AccentGold)
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Flash: ${flashMode.iconLabel}",
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SwitchCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Switch Camera",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun GalleryShortcutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(52.dp)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = "Open Gallery",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun ZoomControlBar(
    currentZoom: Float,
    maxZoom: Float,
    onZoomSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val zoomOptions = listOf(1f, 2f, 3f, 5f).filter { it <= maxZoom }

    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            zoomOptions.forEach { zoom ->
                val isSelected = kotlin.math.abs(currentZoom - zoom) < 0.2f
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AppColors.AccentGold else Color.Transparent)
                        .clickable { onZoomSelected(zoom) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${zoom.toInt()}x",
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateQuickSelector(
    selectedTemplateId: String,
    onTemplateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TemplateData.ALL_TEMPLATES) { template ->
            val isSelected = template.id == selectedTemplateId
            val badgeColor = Color(template.primaryColorHex)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) badgeColor.copy(alpha = 0.35f)
                        else Color.Black.copy(alpha = 0.5f)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) badgeColor else Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTemplateSelected(template.id) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(badgeColor, CircleShape)
                    )
                    Text(
                        text = template.name,
                        color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
