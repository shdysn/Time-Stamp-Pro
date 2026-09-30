package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.presentation.designsystem.AppColors
import com.example.presentation.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val customDarkScheme = darkColorScheme(
            primary = AppColors.AccentGold,
            onPrimary = Color.Black,
            secondary = AppColors.AccentCyan,
            background = AppColors.DarkBackground,
            surface = AppColors.DarkSurface,
            surfaceVariant = AppColors.DarkSurfaceVariant,
            onBackground = AppColors.TextPrimary,
            onSurface = AppColors.TextPrimary
        )

        setContent {
            MaterialTheme(colorScheme = customDarkScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.DarkBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
