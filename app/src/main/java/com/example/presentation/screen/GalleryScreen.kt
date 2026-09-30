package com.example.presentation.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TemplateData
import com.example.database.MediaEntity
import com.example.presentation.designsystem.AppColors
import com.example.presentation.viewmodel.GalleryEffect
import com.example.presentation.viewmodel.GalleryViewModel
import com.example.timestamp.CoordinateFormatter
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<MediaEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler {
        if (uiState.selectedMedia != null) {
            viewModel.selectMedia(null)
        } else {
            onNavigateBack()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GalleryEffect.ShareIntent -> {
                    context.startActivity(android.content.Intent.createChooser(effect.intent, "Share Timestamp Record"))
                }
                is GalleryEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Timestamp Gallery",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${mediaList.size} Photos",
                            color = AppColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (showSearch) AppColors.AccentGold else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.DarkBackground
                )
            )
        },
        containerColor = AppColors.DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            AnimatedVisibility(visible = showSearch) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by project, address, notes...") },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Template Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAllSelected = uiState.selectedFilterTemplateId == null
                    Surface(
                        color = if (isAllSelected) AppColors.AccentGold else AppColors.DarkSurface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { viewModel.selectFilter(null) }
                    ) {
                        Text(
                            text = "All Records",
                            color = if (isAllSelected) Color.Black else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                items(TemplateData.ALL_TEMPLATES) { template ->
                    val isSelected = uiState.selectedFilterTemplateId == template.id
                    val color = Color(template.primaryColorHex)

                    Surface(
                        color = if (isSelected) color else AppColors.DarkSurface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { viewModel.selectFilter(template.id) }
                    ) {
                        Text(
                            text = template.name,
                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Photo Grid
            if (mediaList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = AppColors.TextTertiary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No stamped photos found",
                            color = AppColors.TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Capture your first stamped photo from the camera",
                            color = AppColors.TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mediaList, key = { it.id }) { media ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.DarkCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectMedia(media) }
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    AsyncImage(
                                        model = File(media.filePath),
                                        contentDescription = media.projectName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Badge overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.65f))
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = media.formattedDate,
                                            color = AppColors.AccentGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = media.projectName.ifBlank { "Timestamp Photo" },
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = media.address.ifBlank { "${media.latitude}, ${media.longitude}" },
                                        color = AppColors.TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Photo Detail Bottom Sheet
        uiState.selectedMedia?.let { media ->
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectMedia(null) },
                sheetState = sheetState,
                containerColor = AppColors.DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Stamped Image Full Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AppColors.BorderSubtle, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = File(media.filePath),
                            contentDescription = "Full Stamped Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.shareMedia(media) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.exportToDevice(media) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkCard),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export", color = Color.White)
                        }

                        IconButton(
                            onClick = { deleteCandidate = media },
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .size(42.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppColors.AccentRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Metadata Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.DarkCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "STAMP EVIDENCE METADATA",
                                color = AppColors.AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            MetadataRow("Timestamp", media.formattedDate)
                            MetadataRow("Template", media.templateName)
                            MetadataRow("Project", media.projectName.ifBlank { "N/A" })
                            MetadataRow("Inspector", media.inspectorName.ifBlank { "N/A" })
                            MetadataRow("Coordinates", "${String.format(Locale.US, "%.5f", media.latitude)}°, ${String.format(Locale.US, "%.5f", media.longitude)}°")
                            MetadataRow("Address", media.address)
                            MetadataRow("Altitude", "${String.format(Locale.US, "%.1f", media.altitude)} m")
                            MetadataRow("Compass", CoordinateFormatter.formatBearing(media.compassDegrees))
                            MetadataRow("Notes", media.notes.ifBlank { "None" })
                            MetadataRow("File Size", "${media.fileSizeBytes / 1024} KB")
                            if (media.isMockGps) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = AppColors.AccentRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mock GPS Detected", color = AppColors.AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Delete Confirmation Dialog
        deleteCandidate?.let { media ->
            AlertDialog(
                onDismissRequest = { deleteCandidate = null },
                title = { Text("Delete Stamped Photo?", color = Color.White) },
                text = { Text("Are you sure you want to delete this record? This action cannot be undone.", color = AppColors.TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteMedia(media)
                            deleteCandidate = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentRed)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteCandidate = null }) {
                        Text("Cancel", color = Color.LightGray)
                    }
                },
                containerColor = AppColors.DarkSurface
            )
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, color = AppColors.TextTertiary, fontSize = 12.sp, modifier = Modifier.width(90.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
