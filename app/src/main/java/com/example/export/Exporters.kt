package com.example.export

import android.content.Context
import android.content.Intent
import com.example.database.MediaEntity
import com.example.storage.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

class ShareManager(private val fileManager: FileManager) {
    fun createShareIntent(media: MediaEntity): Intent {
        return fileManager.getShareIntent(media)
    }
}

class CsvExporter(private val context: Context) {
    suspend fun exportRecordsToCsv(records: List<MediaEntity>): File = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, "timestamp_records.csv")
        FileWriter(file).use { writer ->
            writer.append("ID,Date,Project,Inspector,Latitude,Longitude,Altitude,Address,Notes\n")
            for (r in records) {
                writer.append("${r.id},\"${r.formattedDate}\",\"${r.projectName}\",\"${r.inspectorName}\",${r.latitude},${r.longitude},${r.altitude},\"${r.address}\",\"${r.notes}\"\n")
            }
        }
        return@withContext file
    }
}
