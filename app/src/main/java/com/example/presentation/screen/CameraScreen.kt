package com.example.presentation.screen

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.presentation.components.FlashButton
import com.example.presentation.components.GalleryShortcutButton
import com.example.presentation.components.ShutterButton
import com.example.presentation.components.SwitchCameraButton
import com.example.presentation.components.TemplateQuickSelector
import com.example.presentation.components.TimestampOverlayView
import com.example.presentation.components.ZoomControlBar
import com.example.presentation.designsystem.AppColors
import com.example.presentation.viewmodel.CameraUiEffect
import com.example.presentation.viewmodel.CameraViewModel
import java.io.File

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onNavigateToGallery: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    var flashWhiteScreen by remember { mutableStateOf(false) }

    // Permission launcher for Camera and Location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        if (cameraGranted && previewViewRef != null) {
            viewModel.bindCamera(lifecycleOwner, previewViewRef!!)
        }
        viewModel.startSensors()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Lifecycle effect for sensors
    DisposableEffect(lifecycleOwner) {
        viewModel.startSensors()
        onDispose {
            viewModel.stopSensors()
        }
    }

    // Collect effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CameraUiEffect.PhotoSaved -> {
                    flashWhiteScreen = true
                    Toast.makeText(context, "Saved: ${effect.media.fileName}", Toast.LENGTH_SHORT).show()
                }
                is CameraUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(flashWhiteScreen) {
        if (flashWhiteScreen) {
            kotlinx.coroutines.delay(100L)
            flashWhiteScreen = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Camera Viewfinder
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    previewViewRef = this
                    viewModel.bindCamera(lifecycleOwner, this)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        previewViewRef?.let { pv ->
                            viewModel.cameraManager.focusOnPoint(offset.x, offset.y, pv)
                        }
                    }
                }
        )

        // 2. Real-time Timestamp & Geotag Overlay HUD
        TimestampOverlayView(
            settings = uiState.settings,
            location = uiState.location,
            heading = uiState.compassHeading,
            currentTimeMillis = uiState.currentTimeMillis,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Shutter flash effect
        AnimatedVisibility(
            visible = flashWhiteScreen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }

        // 4. Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 28.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash Mode
            FlashButton(
                flashMode = uiState.flashMode,
                onClick = { viewModel.cycleFlash() }
            )

            // GPS Signal & Mock indicator pill
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { onNavigateToSettings() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.location.isMock) Icons.Default.Warning else Icons.Default.GpsFixed,
                        contentDescription = "GPS Status",
                        tint = if (uiState.location.isMock) AppColors.AccentRed else AppColors.AccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (uiState.location.isMock) "MOCK GPS" else if (uiState.location.accuracy > 0) "±${uiState.location.accuracy.toInt()}m" else "GPS LOCK",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Settings Action
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // 5. Bottom Controls Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(bottom = 28.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zoom Selector
            ZoomControlBar(
                currentZoom = uiState.currentZoom,
                maxZoom = uiState.maxZoom,
                onZoomSelected = { viewModel.setZoom(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Capture Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail Shortcut
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        .clickable { onNavigateToGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.lastCapturedMedia != null) {
                        AsyncImage(
                            model = File(uiState.lastCapturedMedia!!.filePath),
                            contentDescription = "Last Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        GalleryShortcutButton(onClick = onNavigateToGallery)
                    }
                }

                // Shutter Button
                ShutterButton(
                    onClick = { viewModel.capturePhoto() },
                    isCapturing = uiState.isCapturing
                )

                // Switch Camera Lens Button
                SwitchCameraButton(
                    onClick = {
                        previewViewRef?.let { pv ->
                            viewModel.toggleCamera(lifecycleOwner, pv)
                        }
                    }
                )
            }
        }
    }
}
