package com.kekouke.tfsspring.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kekouke.tfsspring.data.local.entities.StreamEntity
import com.kekouke.tfsspring.data.local.entities.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamDao {
    @Query("SELECT * FROM stream")
    fun getAllStreams(): Flow<List<StreamEntity>>

    @Query("SELECT * FROM stream WHERE subscribed == 1")
    fun getSubscribedStreams(): Flow<List<StreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreams(streams: List<StreamEntity>)

    @Delete
    fun deleteStreams(streams: List<StreamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopics(topics: List<TopicEntity>)

    @Query("SELECT * FROM topic WHERE streamId == :streamId")
    suspend fun getTopics(streamId: Int): List<TopicEntity>

    @Query("SELECT * FROM stream WHERE name == :streamName LIMIT 1")
    suspend fun findStreamByName(streamName: String): StreamEntity?
}