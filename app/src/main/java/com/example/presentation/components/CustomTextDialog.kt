package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.designsystem.AppColors

val CUSTOM_TEXT_COLORS = listOf(
    0xFFFFFFFF, // White
    0xFFFFC107, // Gold
    0xFFFFEB3B, // Yellow
    0xFFFF5252, // Red
    0xFF00E676, // Green
    0xFF00E5FF, // Cyan
    0xFFFF5722, // Orange
    0xFFFF4081, // Pink
    0xFF1E293B  // Black/Slate
)

@Composable
fun CustomTextDialog(
    initialText: String,
    initialEnabled: Boolean,
    initialSize: Float,
    initialBold: Boolean,
    initialItalic: Boolean,
    initialUnderline: Boolean,
    initialColorHex: Long,
    onApply: (text: String, isEnabled: Boolean, size: Float, isBold: Boolean, isItalic: Boolean, isUnderline: Boolean, colorHex: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var isEnabled by remember { mutableStateOf(initialEnabled || initialText.isNotBlank()) }
    var textSize by remember { mutableFloatStateOf(initialSize.coerceIn(16f, 42f)) }
    var isBold by remember { mutableStateOf(initialBold) }
    var isItalic by remember { mutableStateOf(initialItalic) }
    var isUnderline by remember { mutableStateOf(initialUnderline) }
    var colorHex by remember { mutableLongStateOf(initialColorHex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = AppColors.AccentGold
                )
                Text(
                    text = "Text on Picture",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Enable/Disable Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.DarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnabled) "Display on photo: ON" else "Display on photo: OFF",
                        color = if (isEnabled) AppColors.AccentGold else Color.LightGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppColors.AccentGold,
                            checkedTrackColor = AppColors.AccentGold.copy(alpha = 0.4f)
                        )
                    )
                }

                // Text Input Field
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        if (it.isNotBlank()) isEnabled = true
                    },
                    label = { Text("Write text here...") },
                    placeholder = { Text("e.g. Site Visit #1 - Approved") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.AccentGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Live Preview Card
                Text(
                    text = "LIVE PREVIEW",
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(colorHex).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (text.isNotBlank()) text else "Preview Text Sample",
                            color = Color(colorHex),
                            fontSize = (textSize * 0.9f).sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Format Controls (Bold, Italic, Underline)
                Text(
                    text = "FONT STYLING",
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bold Toggle
                    StyleToggleButton(
                        label = "Bold",
                        icon = Icons.Default.FormatBold,
                        isSelected = isBold,
                        onClick = { isBold = !isBold },
                        modifier = Modifier.weight(1f)
                    )
                    // Italic Toggle
                    StyleToggleButton(
                        label = "Italic",
                        icon = Icons.Default.FormatItalic,
                        isSelected = isItalic,
                        onClick = { isItalic = !isItalic },
                        modifier = Modifier.weight(1f)
                    )
                    // Underline Toggle
                    StyleToggleButton(
                        label = "Underline",
                        icon = Icons.Default.FormatUnderlined,
                        isSelected = isUnderline,
                        onClick = { isUnderline = !isUnderline },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Size:",
                            color = AppColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${textSize.toInt()} sp",
                        color = AppColors.AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Slider(
                    value = textSize,
                    onValueChange = { textSize = it },
                    valueRange = 16f..42f,
                    steps = 12,
                    colors = SliderDefaults.colors(
                        thumbColor = AppColors.AccentGold,
                        activeTrackColor = AppColors.AccentGold,
                        inactiveTrackColor = Color.DarkGray
                    )
                )

                // Color Picker
                Text(
                    text = "COLOR",
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CUSTOM_TEXT_COLORS.forEach { hex ->
                        val isSelected = colorHex == hex
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { colorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (hex == 0xFFFFFFFFL || hex == 0xFFFFC107L || hex == 0xFFFFEB3BL) Color.Black else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(text, isEnabled, textSize, isBold, isItalic, isUnderline, colorHex)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentGold)
            ) {
                Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.LightGray)
            }
        },
        containerColor = AppColors.DarkSurface
    )
}

@Composable
private fun StyleToggleButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) AppColors.AccentGold.copy(alpha = 0.25f) else AppColors.DarkSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) AppColors.AccentGold else Color.Gray.copy(alpha = 0.4f)
        ),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) AppColors.AccentGold else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (isSelected) AppColors.AccentGold else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
