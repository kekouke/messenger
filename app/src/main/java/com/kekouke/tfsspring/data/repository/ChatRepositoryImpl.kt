package com.kekouke.tfsspring.data.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kekouke.tfsspring.data.api.EventManager
import com.kekouke.tfsspring.data.api.MY_ID
import com.kekouke.tfsspring.data.api.dto.messages.MessageDto
import com.kekouke.tfsspring.data.api.response.events.EventResponseBase
import com.kekouke.tfsspring.data.api.services.ChatApiService
import com.kekouke.tfsspring.data.api.throwExceptionOnFailedRequest
import com.kekouke.tfsspring.data.local.dao.ChatDao
import com.kekouke.tfsspring.data.local.entities.MessageEntity
import com.kekouke.tfsspring.data.local.entities.MessageWithReactions
import com.kekouke.tfsspring.data.local.entities.ReactionEntity
import com.kekouke.tfsspring.data.mappers.decode
import com.kekouke.tfsspring.data.mappers.toDomain
import com.kekouke.tfsspring.data.mappers.toEntity
import com.kekouke.tfsspring.data.mappers.toEntityList
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Page
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.ReactionOperationType
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.repository.ChatRepository
import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val api: ChatApiService,
    private val eventManager: EventManager
) : ChatRepository {

    private var anchor: String = ANCHOR_NEWEST

    override fun getMessages(topic: Topic) = flow {
        val cachedMessages = getCachedMessages(topic)
        if (cachedMessages.content.isNotEmpty()) {
            emit(Result.success(cachedMessages))
        }

        val result = runCatchingNonCancellation {
            loadNewMessages(topic, MESSAGE_PREFETCH_COUNT).apply {
                saveMessages(content, topic)
            }
        }

        emit(result)
    }

    private suspend fun getCachedMessages(topic: Topic) = withContext(Dispatchers.IO) {
        val messages = chatDao.getCachedMessages(topic.streamId, topic.name)
        Page(messages.map(MessageWithReactions::toDomain), true)
    }

    private suspend fun loadNewMessages(topic: Topic, quantity: Int): Page {
        val response = api.getMessages(
            anchor,
            quantity,
            NUM_AFTER,
            getFilterString(topic),
            anchor == ANCHOR_NEWEST
        )

        val messages = response.messages.map(MessageDto::toDomain)
        response.messages.firstOrNull()?.let { messageDto ->
            anchor = messageDto.id.toString()
        }

        return Page(messages, response.foundOldest)
    }

    override suspend fun saveMessages(messages: List<Message>, topic: Topic) =
        withContext(Dispatchers.IO) {
            val (messageEntities, reactionEntities) = messages.prepareForSaving(topic)

            chatDao.replaceMessagesWithReactions(
                messageEntities, reactionEntities, topic.streamId, topic.name
            )
        }

    private fun List<Message>.prepareForSaving(
        topic: Topic
    ): Pair<List<MessageEntity>, List<ReactionEntity>> =
        takeLast(MESSAGE_PREFETCH_COUNT).run {
            val reactionEntityList = mutableListOf<ReactionEntity>()

            val messageEntityList = map { message ->
                reactionEntityList.addAll(message.reactions.toEntityList(message.id))
                message.toEntity(topic)
            }

            messageEntityList to reactionEntityList
        }

    override fun loadNextPage(topic: Topic) = flow {
        val result = runCatchingNonCancellation {
            loadNewMessages(topic, MESSAGE_LOAD_COUNT)
        }

        emit(result)
    }

    private fun getFilterString(topic: Topic) = JsonArray().apply {
        add(JsonObject().applyStreamFilter(topic.streamName))
        add(JsonObject().applyTopicFilter(topic.name))
    }.toString()

    private fun JsonObject.applyStreamFilter(streamName: String): JsonObject {
        addOperatorProperty(STREAM)
        addOperandProperty(streamName)
        return this
    }

    private fun JsonObject.applyTopicFilter(topicName: String): JsonObject {
        addOperatorProperty(TOPIC)
        addOperandProperty(topicName)
        return this
    }

    private fun JsonObject.addOperatorProperty(value: String) =
        addProperty(OPERATOR_PROPERTY, value)

    private fun JsonObject.addOperandProperty(value: String) = addProperty(OPERAND_PROPERTY, value)

    override suspend fun sendMessage(
        streamId: Int,
        topic: String,
        text: String
    ): Result<Unit> {
        return runCatchingNonCancellation {
            val response = api.sendMessage(streamId, topic, text)

            response.errorBody()?.let {
                throwExceptionOnFailedRequest(response.errorBody()!!)
            }
        }
    }

    override suspend fun addReaction(messageId: Int, reactionName: String): Result<Boolean> {
        return runCatchingNonCancellation {
            val response = api.addReaction(messageId, reactionName)

            if (response.isSuccessful.not()) {
                throwExceptionOnFailedRequest(response.errorBody()!!)
            }

            true
        }
    }

    override suspend fun removeReaction(messageId: Int, reactionName: String): Result<Boolean> {
        return runCatchingNonCancellation {
            val response = api.removeReaction(messageId, reactionName)

            if (response.isSuccessful.not()) {
                throwExceptionOnFailedRequest(response.errorBody()!!)
            }

            true
        }
    }

    override suspend fun subscribeToChatEvents(topic: Topic) = flow {
        eventManager.registerEventQueue(CHAT_EVENT_TYPES).fold(
            onSuccess = { eventsFlow -> handleChatEvents(eventsFlow, topic) },
            onFailure = { exception -> throw exception }
        )
    }

    private suspend  fun FlowCollector<PollEvent.Chat>.handleChatEvents(
        eventsFlow: Flow<EventResponseBase>,
        currentTopic: Topic
    ) {
        eventsFlow.collect { event ->
            when (event) {
                is EventResponseBase.MessageEvent -> handleMessageEvent(event, currentTopic)
                is EventResponseBase.ReactionEvent -> handleReactionEvent(event)
                else -> error("$event was added to the event queue, but was not processed")
            }
        }
    }

    private suspend fun FlowCollector<PollEvent.Chat.MessageEvent>.handleMessageEvent(
        event: EventResponseBase.MessageEvent,
        currentTopic: Topic
    ) {
        event.message.let { message ->
            if (
                currentTopic.streamId != message.streamId ||
                currentTopic.name != message.topicName
            ) return@let

            emit(PollEvent.Chat.MessageEvent(message.toDomain()))
        }
    }

    private suspend fun FlowCollector<PollEvent.Chat.ReactionEvent>.handleReactionEvent(
        event: EventResponseBase.ReactionEvent
    ) {
        if (event.reactionType != UNICODE_EMOJI) return

        val operationType = ReactionOperationType.fromString(event.action)
        val isMeReaction = event.userId == MY_ID
        val reaction = Reaction(event.emojiName, decode(event.emojiCode), selected = isMeReaction)
        val reactionEvent = PollEvent.Chat.ReactionEvent(
            event.messageId,
            reaction,
            operationType,
            isMeReaction
        )

        emit(reactionEvent)
    }

    companion object {
        private const val MESSAGE_PREFETCH_COUNT = 50
        private const val MESSAGE_LOAD_COUNT = 20
        private const val NUM_AFTER = 0
        private const val ANCHOR_NEWEST = "newest"
        private const val CHAT_EVENT_TYPES = "[\"message\", \"reaction\"]"
        private const val OPERATOR_PROPERTY = "operator"
        private const val OPERAND_PROPERTY = "operand"
        private const val STREAM = "stream"
        private const val TOPIC = "topic"
        private const val UNICODE_EMOJI = "unicode_emoji"
    }
}