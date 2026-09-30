package com.example.storage

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.LocationData
import com.example.data.model.TemplateData
import com.example.data.model.UserSettings
import com.example.database.AppDatabase
import com.example.database.MediaEntity
import com.example.timestamp.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val context: Context) {

    private val mediaDao = AppDatabase.getDatabase(context).mediaDao()

    private val imagesDir: File
        get() {
            val dir = File(context.filesDir, "images")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    suspend fun saveCapturedPhoto(
        stampedBitmap: Bitmap,
        originalBitmap: Bitmap?,
        settings: UserSettings,
        location: LocationData,
        heading: Float,
        timestampMillis: Long = System.currentTimeMillis()
    ): MediaEntity = withContext(Dispatchers.IO) {
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(timestampMillis))
        val fileName = "STAMP_$dateStr.jpg"
        val stampedFile = File(imagesDir, fileName)

        FileOutputStream(stampedFile).use { out ->
            stampedBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        var originalPath: String? = null
        if (settings.saveOriginalCopy && originalBitmap != null) {
            val origFileName = "ORIG_$dateStr.jpg"
            val origFile = File(imagesDir, origFileName)
            FileOutputStream(origFile).use { out ->
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            originalPath = origFile.absolutePath
        }

        val template = TemplateData.getById(settings.selectedTemplateId)
        val formattedDate = DateFormatter.format(timestampMillis, settings.dateFormat)

        val entity = MediaEntity(
            filePath = stampedFile.absolutePath,
            originalFilePath = originalPath,
            fileName = fileName,
            timestampMillis = timestampMillis,
            formattedDate = formattedDate,
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            accuracy = location.accuracy,
            address = location.address,
            compassDegrees = heading,
            templateId = template.id,
            templateName = template.name,
            projectName = settings.projectName,
            inspectorName = settings.inspectorName,
            notes = settings.customNotes,
            isMockGps = location.isMock,
            fileSizeBytes = stampedFile.length(),
            width = stampedBitmap.width,
            height = stampedBitmap.height
        )

        val id = mediaDao.insertMedia(entity)
        return@withContext entity.copy(id = id)
    }

    suspend fun deletePhoto(media: MediaEntity) = withContext(Dispatchers.IO) {
        try {
            val file = File(media.filePath)
            if (file.exists()) file.delete()
            media.originalFilePath?.let {
                val orig = File(it)
                if (orig.exists()) orig.delete()
            }
            mediaDao.deleteMedia(media)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportToGallery(media: MediaEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(media.filePath)
            if (!file.exists()) return@withContext false

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, media.fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TimestampCameraPro")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext false

            resolver.openOutputStream(uri)?.use { out ->
                file.inputStream().use { input ->
                    input.copyTo(out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun getShareIntent(media: MediaEntity): Intent {
        val file = File(media.filePath)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val caption = buildString {
            append("📸 Timestamp Camera Pro Record\n")
            append("📅 ${media.formattedDate}\n")
            append("📍 ${media.address}\n")
            append("🌐 Lat: ${String.format(Locale.US, "%.5f", media.latitude)}, Lng: ${String.format(Locale.US, "%.5f", media.longitude)}\n")
            if (media.projectName.isNotBlank()) append("🏗 Project: ${media.projectName}\n")
            if (media.inspectorName.isNotBlank()) append("👤 By: ${media.inspectorName}\n")
            if (media.notes.isNotBlank()) append("📝 Notes: ${media.notes}\n")
        }

        return Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, caption)
            putExtra(Intent.EXTRA_SUBJECT, "Timestamp Photo: ${media.projectName}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    suspend fun getStorageUsageBytes(): Long = withContext(Dispatchers.IO) {
        var total = 0L
        imagesDir.listFiles()?.forEach { file ->
            total += file.length()
        }
        return@withContext total
    }
}
