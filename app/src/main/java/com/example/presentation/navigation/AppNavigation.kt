package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.screen.CameraScreen
import com.example.presentation.screen.GalleryScreen
import com.example.presentation.screen.SettingsScreen
import com.example.presentation.screen.TemplateScreen
import com.example.presentation.viewmodel.CameraViewModel
import com.example.presentation.viewmodel.GalleryViewModel
import com.example.presentation.viewmodel.SettingsViewModel

object AppDestinations {
    const val CAMERA = "camera"
    const val GALLERY = "gallery"
    const val SETTINGS = "settings"
    const val TEMPLATES = "templates"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    cameraViewModel: CameraViewModel = viewModel(),
    galleryViewModel: GalleryViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val cameraUiState by cameraViewModel.uiState.collectAsState()
    val globalSettings by settingsViewModel.settings.collectAsState()

    // Sync settings between SettingsViewModel and CameraViewModel
    cameraViewModel.updateSettings(globalSettings)

    NavHost(
        navController = navController,
        startDestination = AppDestinations.CAMERA,
        modifier = modifier
    ) {
        composable(AppDestinations.CAMERA) {
            CameraScreen(
                viewModel = cameraViewModel,
                onNavigateToGallery = { navController.navigate(AppDestinations.GALLERY) },
                onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS) },
                onNavigateToTemplates = { navController.navigate(AppDestinations.TEMPLATES) }
            )
        }

        composable(AppDestinations.GALLERY) {
            GalleryScreen(
                viewModel = galleryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.TEMPLATES) {
            TemplateScreen(
                currentTemplateId = cameraUiState.settings.selectedTemplateId,
                onTemplateSelected = { selectedId ->
                    cameraViewModel.setTemplate(selectedId)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
