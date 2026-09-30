package com.example.domain.usecase

import android.graphics.Bitmap
import com.example.data.model.LocationData
import com.example.data.model.TemplateData
import com.example.data.model.UserSettings
import com.example.database.MediaEntity
import com.example.storage.FileManager
import com.example.watermark.WatermarkEngine

class CapturePhotoUseCase(private val cameraManager: com.example.camera.CameraManager) {
    suspend operator fun invoke(): Bitmap {
        return cameraManager.capturePhoto()
    }
}

class AddWatermarkUseCase {
    operator fun invoke(
        source: Bitmap,
        settings: UserSettings,
        location: LocationData,
        heading: Float,
        timestamp: Long = System.currentTimeMillis()
    ): Bitmap {
        return WatermarkEngine.applyWatermark(source, settings, location, heading, timestamp)
    }
}

class SaveMediaUseCase(private val fileManager: FileManager) {
    suspend operator fun invoke(
        stampedBitmap: Bitmap,
        originalBitmap: Bitmap?,
        settings: UserSettings,
        location: LocationData,
        heading: Float,
        timestamp: Long = System.currentTimeMillis()
    ): MediaEntity {
        return fileManager.saveCapturedPhoto(
            stampedBitmap,
            originalBitmap,
            settings,
            location,
            heading,
            timestamp
        )
    }
}

class LoadTemplateUseCase {
    operator fun invoke(templateId: String): TemplateData {
        return TemplateData.getById(templateId)
    }

    fun getAll(): List<TemplateData> {
        return TemplateData.ALL_TEMPLATES
    }
}

class ExportMediaUseCase(private val fileManager: FileManager) {
    suspend operator fun invoke(media: MediaEntity): Boolean {
        return fileManager.exportToGallery(media)
    }
}
