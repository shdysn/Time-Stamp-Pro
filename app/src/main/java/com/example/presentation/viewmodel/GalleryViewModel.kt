package com.example.presentation.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.database.MediaEntity
import com.example.storage.FileManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GalleryUiState(
    val selectedFilterTemplateId: String? = null,
    val selectedMedia: MediaEntity? = null,
    val isExporting: Boolean = false,
    val totalCount: Int = 0,
    val searchQuery: String = ""
)

sealed interface GalleryEffect {
    data class ShareIntent(val intent: Intent) : GalleryEffect
    data class ShowMessage(val message: String) : GalleryEffect
}

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaDao = AppDatabase.getDatabase(application).mediaDao()
    private val fileManager = FileManager(application)

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GalleryEffect>()
    val effect: SharedFlow<GalleryEffect> = _effect.asSharedFlow()

    val mediaList: StateFlow<List<MediaEntity>> = combine(
        mediaDao.getAllMedia(),
        _uiState
    ) { allItems, state ->
        var filtered = allItems
        state.selectedFilterTemplateId?.let { templateId ->
            filtered = filtered.filter { it.templateId == templateId }
        }
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.projectName.lowercase().contains(q) ||
                it.address.lowercase().contains(q) ||
                it.inspectorName.lowercase().contains(q) ||
                it.notes.lowercase().contains(q) ||
                it.formattedDate.lowercase().contains(q)
            }
        }
        _uiState.update { it.copy(totalCount = allItems.size) }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectFilter(templateId: String?) {
        _uiState.update { it.copy(selectedFilterTemplateId = templateId) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectMedia(media: MediaEntity?) {
        _uiState.update { it.copy(selectedMedia = media) }
    }

    fun deleteMedia(media: MediaEntity) {
        viewModelScope.launch {
            fileManager.deletePhoto(media)
            if (_uiState.value.selectedMedia?.id == media.id) {
                _uiState.update { it.copy(selectedMedia = null) }
            }
            _effect.emit(GalleryEffect.ShowMessage("Photo deleted"))
        }
    }

    fun exportToDevice(media: MediaEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val success = fileManager.exportToGallery(media)
            _uiState.update { it.copy(isExporting = false) }
            if (success) {
                _effect.emit(GalleryEffect.ShowMessage("Saved to Pictures / TimestampCameraPro"))
            } else {
                _effect.emit(GalleryEffect.ShowMessage("Export failed"))
            }
        }
    }

    fun shareMedia(media: MediaEntity) {
        viewModelScope.launch {
            val intent = fileManager.getShareIntent(media)
            _effect.emit(GalleryEffect.ShareIntent(intent))
        }
    }
}
