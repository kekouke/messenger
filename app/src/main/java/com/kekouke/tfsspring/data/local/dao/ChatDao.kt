package com.kekouke.tfsspring.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kekouke.tfsspring.data.local.entities.MessageEntity
import com.kekouke.tfsspring.data.local.entities.MessageWithReactions
import com.kekouke.tfsspring.data.local.entities.ReactionEntity

@Dao
interface ChatDao {
    @Transaction
    @Query("SELECT * FROM message WHERE streamId == :streamId AND topicName == :topicName")
    fun getCachedMessages(streamId: Int, topicName: String): List<MessageWithReactions>

    @Insert
    fun insertMessages(messages: List<MessageEntity>)

    @Insert
    fun insertReactions(reactions: List<ReactionEntity>)

    @Transaction
    @Query("DELETE FROM message WHERE streamId == :streamId AND topicName == :topicName")
    fun deleteMessages(streamId: Int, topicName: String)

    @Transaction
    fun replaceMessages(messages: List<MessageEntity>, streamId: Int, topicName: String) {
        deleteMessages(streamId, topicName)
        insertMessages(messages)
    }

    @Transaction
    fun replaceMessagesWithReactions(
        messages: List<MessageEntity>,
        reactions: List<ReactionEntity>,
        streamId: Int,
        topicName: String
    ) {
        deleteMessages(streamId, topicName)
        insertMessages(messages)
        insertReactions(reactions)
    }
}