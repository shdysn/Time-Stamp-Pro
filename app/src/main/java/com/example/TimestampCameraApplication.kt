package com.example

import android.app.Application
import com.example.database.AppDatabase
import com.example.storage.FileManager

class TimestampCameraApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var fileManager: FileManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        fileManager = FileManager(this)
    }
}
