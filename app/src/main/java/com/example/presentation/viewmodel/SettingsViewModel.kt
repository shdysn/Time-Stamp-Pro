package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AltitudeUnit
import com.example.data.model.CoordinateFormat
import com.example.data.model.StampFontSize
import com.example.data.model.StampPosition
import com.example.data.model.UserSettings
import com.example.location.CompassManager
import com.example.location.GPSManager
import com.example.storage.FileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiagnosticsInfo(
    val hasGpsPermission: Boolean = false,
    val hasCompassSensor: Boolean = false,
    val isMockGpsDetected: Boolean = false,
    val storageUsedMb: Float = 0f,
    val osVersion: String = android.os.Build.VERSION.RELEASE,
    val deviceModel: String = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val fileManager = FileManager(application)
    private val gpsManager = GPSManager(application)
    private val compassManager = CompassManager(application)

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _diagnostics = MutableStateFlow(DiagnosticsInfo())
    val diagnostics: StateFlow<DiagnosticsInfo> = _diagnostics.asStateFlow()

    init {
        refreshDiagnostics()
    }

    fun refreshDiagnostics() {
        viewModelScope.launch {
            val bytes = fileManager.getStorageUsageBytes()
            _diagnostics.value = DiagnosticsInfo(
                hasGpsPermission = gpsManager.hasLocationPermission(),
                hasCompassSensor = compassManager.isSupported,
                isMockGpsDetected = gpsManager.locationData.value.isMock,
                storageUsedMb = (bytes / (1024f * 1024f))
            )
        }
    }

    fun updateDateFormat(format: String) {
        _settings.update { it.copy(dateFormat = format) }
    }

    fun updateCoordinateFormat(format: CoordinateFormat) {
        _settings.update { it.copy(coordinateFormat = format) }
    }

    fun updateAltitudeUnit(unit: AltitudeUnit) {
        _settings.update { it.copy(altitudeUnit = unit) }
    }

    fun updateStampPosition(pos: StampPosition) {
        _settings.update { it.copy(stampPosition = pos) }
    }

    fun updateTextColor(colorHex: Long) {
        _settings.update { it.copy(textColorHex = colorHex) }
    }

    fun updateBackgroundOpacity(opacity: Float) {
        _settings.update { it.copy(backgroundOpacity = opacity) }
    }

    fun updateFontSize(size: StampFontSize) {
        _settings.update { it.copy(fontSize = size) }
    }

    fun toggleShowAddress(show: Boolean) {
        _settings.update { it.copy(showAddress = show) }
    }

    fun toggleShowCoordinates(show: Boolean) {
        _settings.update { it.copy(showCoordinates = show) }
    }

    fun toggleShowAltitude(show: Boolean) {
        _settings.update { it.copy(showAltitude = show) }
    }

    fun toggleShowCompass(show: Boolean) {
        _settings.update { it.copy(showCompass = show) }
    }

    fun toggleShowTimestamp(show: Boolean) {
        _settings.update { it.copy(showTimestamp = show) }
    }

    fun toggleStampVisibility(show: Boolean) {
        _settings.update { it.copy(isStampVisible = show) }
    }

    fun updateCustomText(
        text: String,
        isEnabled: Boolean,
        size: Float,
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        colorHex: Long
    ) {
        _settings.update {
            it.copy(
                customText = text,
                isCustomTextEnabled = isEnabled,
                customTextSize = size,
                isCustomTextBold = isBold,
                isCustomTextItalic = isItalic,
                isCustomTextUnderline = isUnderline,
                customTextColorHex = colorHex
            )
        }
    }

    fun toggleCustomTextEnabled(enabled: Boolean) {
        _settings.update { it.copy(isCustomTextEnabled = enabled) }
    }

    fun toggleShowBadge(show: Boolean) {
        _settings.update { it.copy(showProjectBadge = show) }
    }

    fun toggleSaveOriginal(save: Boolean) {
        _settings.update { it.copy(saveOriginalCopy = save) }
    }

    fun setProjectDefaults(projectName: String, inspectorName: String, notes: String) {
        _settings.update {
            it.copy(
                projectName = projectName,
                inspectorName = inspectorName,
                customNotes = notes
            )
        }
    }
}
