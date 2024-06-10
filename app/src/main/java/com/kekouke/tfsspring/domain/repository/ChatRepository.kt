package com.kekouke.tfsspring.domain.repository

import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Page
import com.kekouke.tfsspring.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(topic: Topic): Flow<Result<Page>>
    fun loadNextPage(topic: Topic): Flow<Result<Page>>
    suspend fun saveMessages(messages: List<Message>, topic: Topic)
    suspend fun sendMessage(streamId: Int, topic: String, text: String): Result<Unit>
    suspend fun addReaction(messageId: Int, reactionName: String): Result<Boolean>
    suspend fun removeReaction(messageId: Int, reactionName: String): Result<Boolean>
    suspend fun subscribeToChatEvents(topic: Topic): Flow<PollEvent.Chat>
}