package com.kekouke.tfsspring.domain.usecases

import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Page
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: ChatRepository) {

    fun getMessages(topic: Topic): Flow<Result<Page>> = chatRepository.getMessages(topic)
    suspend fun saveMessages(messages: List<Message>, topic: Topic) =
        chatRepository.saveMessages(messages, topic)

    fun loadNextPage(topic: Topic): Flow<Result<Page>> = chatRepository.loadNextPage(topic)

    suspend fun sendMessage(streamId: Int, topic: String, text: String): Result<Unit> =
        chatRepository.sendMessage(streamId, topic, text)

    suspend fun addReaction(messageId: Int, reaction: Reaction): Result<Boolean> =
        chatRepository.addReaction(messageId, reaction.name)

    suspend fun removeReaction(messageId: Int, reaction: Reaction): Result<Boolean> =
        chatRepository.removeReaction(messageId, reaction.name)

    suspend fun subscribeOnChatEvents(topic: Topic) = chatRepository.subscribeToChatEvents(topic)
}