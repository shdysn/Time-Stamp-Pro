package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_records")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val originalFilePath: String? = null,
    val fileName: String,
    val timestampMillis: Long,
    val formattedDate: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val address: String,
    val compassDegrees: Float,
    val templateId: String,
    val templateName: String,
    val projectName: String,
    val inspectorName: String,
    val notes: String,
    val isMockGps: Boolean = false,
    val fileSizeBytes: Long = 0,
    val width: Int = 0,
    val height: Int = 0
)
