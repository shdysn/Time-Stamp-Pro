package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_records ORDER BY timestampMillis DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_records WHERE id = :id LIMIT 1")
    suspend fun getMediaById(id: Long): MediaEntity?

    @Query("SELECT * FROM media_records WHERE templateId = :templateId ORDER BY timestampMillis DESC")
    fun getMediaByTemplate(templateId: String): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaEntity): Long

    @Update
    suspend fun updateMedia(media: MediaEntity)

    @Delete
    suspend fun deleteMedia(media: MediaEntity)

    @Query("DELETE FROM media_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM media_records")
    fun getMediaCount(): Flow<Int>
}
