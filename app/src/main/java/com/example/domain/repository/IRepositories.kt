package com.example.domain.repository

import android.graphics.Bitmap
import com.example.data.model.LocationData
import com.example.data.model.UserSettings
import com.example.database.MediaEntity
import kotlinx.coroutines.flow.Flow

interface ICameraRepository {
    suspend fun capturePhoto(): Bitmap
    fun setZoom(ratio: Float)
    fun cycleFlash()
}

interface ILocationRepository {
    val locationData: Flow<LocationData>
    fun startTracking()
    fun stopTracking()
}

interface IMediaRepository {
    fun getAllMedia(): Flow<List<MediaEntity>>
    suspend fun getMediaById(id: Long): MediaEntity?
    suspend fun saveMedia(media: MediaEntity): Long
    suspend fun deleteMedia(media: MediaEntity)
}

interface ISettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
}
