package com.kekouke.tfsspring.data.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kekouke.tfsspring.data.api.EventManager
import com.kekouke.tfsspring.data.api.MY_ID
import com.kekouke.tfsspring.data.api.dto.streams.StreamDto
import com.kekouke.tfsspring.data.api.dto.streams.StreamsDtoContainer
import com.kekouke.tfsspring.data.api.response.events.EventResponseBase
import com.kekouke.tfsspring.data.api.services.StreamsApiService
import com.kekouke.tfsspring.data.api.services.TopicsApiService
import com.kekouke.tfsspring.data.api.throwExceptionOnFailedRequest
import com.kekouke.tfsspring.data.local.dao.StreamDao
import com.kekouke.tfsspring.data.local.entities.StreamEntity
import com.kekouke.tfsspring.data.local.entities.TopicEntity
import com.kekouke.tfsspring.data.mappers.toDomain
import com.kekouke.tfsspring.data.mappers.toEntity
import com.kekouke.tfsspring.domain.model.AddNewStreamResult
import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.StreamOperationType
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.repository.StreamsRepository
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import com.kekouke.tfsspring.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val streamsApi: StreamsApiService,
    private val topicsApi: TopicsApiService,
    private val streamDao: StreamDao,
    private val eventManager: EventManager
) : StreamsRepository {

    override suspend fun fetchStreams(type: StreamsType) = withContext(Dispatchers.IO) {
        runCatchingNonCancellation {
            val subscribedStreams = streamsApi.getSubscribedStreams().extract()
            val subscribedStreamsIds = subscribedStreams.map(StreamDto::id)

            val streamsForCaching = subscribedStreams.map { stream ->
                stream.toEntity(true)
            }.toMutableList()

            if (type == StreamsType.ALL_STREAMS) {
                val allStreams = streamsApi.getAllStreams().extract()

                allStreams.filter { streamDto ->
                    streamDto.id !in subscribedStreamsIds
                }.run {
                    streamsForCaching += map { stream ->
                        stream.toEntity(false)
                    }
                }
            }

            streamDao.insertStreams(streamsForCaching)
        }
    }

    private fun <T : StreamsDtoContainer> Response<T>.extract(): List<StreamDto> {
        return body()?.streams ?: throwExceptionOnFailedRequest(errorBody()!!)
    }

    override fun getStreams(type: StreamsType) = when (type) {
        StreamsType.ALL_STREAMS -> streamDao.getAllStreams()
        StreamsType.SUBSCRIBED -> streamDao.getSubscribedStreams()
    }.map { streams ->
        streams.map(StreamEntity::toDomain)
    }.flowOn(Dispatchers.IO)

    override fun getTopics(stream: Stream): Flow<Result<List<Topic>>> = flow {
        withContext(Dispatchers.IO) {
            streamDao.getTopics(stream.id)
        }.let { entities ->
            if (entities.isEmpty()) return@let

            entities.map(TopicEntity::toDomain).sortedBy(Topic::name).run {
                emit(Result.success(this))
            }
        }

        val result = runCatchingNonCancellation {
            val topics = topicsApi.getTopics(stream.id).topics

            streamDao.insertTopics(topics.map { topicDto -> topicDto.toEntity(stream) })

            topics.map { topicDto -> topicDto.toDomain(stream) }.sortedBy(Topic::name)
        }

        emit(result)
    }

    override suspend fun createStream(streamName: String): AddNewStreamResult {
        return fetchStreams(StreamsType.ALL_STREAMS).exceptionOrNull()?.let {
            AddNewStreamResult.RefreshStreamsError(it)
        } ?: withContext(Dispatchers.IO) {
            streamDao.findStreamByName(streamName)?.let {
                return@withContext AddNewStreamResult.AlreadyExistsError
            }

            runCatchingNonCancellation {
                streamsApi.createStream(getSubscriptionsParam(streamName))
            }.fold(
                onSuccess = { AddNewStreamResult.Success },
                onFailure = AddNewStreamResult::NetworkError
            )
        }
    }

    private fun getSubscriptionsParam(streamName: String) = JsonArray().apply {
        add(JsonObject().apply { addProperty(NAME_PROPERTY, streamName) })
    }.toString()

    override suspend fun subscribeToStreamsEvents() = flow<PollEvent.Streams> {
        eventManager.registerEventQueue(STREAM_EVENT_TYPES)
            .fold(onSuccess = { eventsFlow -> handleStreamEvents(eventsFlow) },
                onFailure = { exception -> throw exception })
    }

    private suspend fun handleStreamEvents(
        eventsFlow: Flow<EventResponseBase>
    ) {
        eventsFlow.collect { event ->
            when (event) {
                is EventResponseBase.StreamEvent -> handleStreamEvent(event)
                else -> error("$event was added to the event queue, but was not processed")
            }
        }
    }

    private fun handleStreamEvent(
        event: EventResponseBase.StreamEvent
    ) {
        event.streams.map { streamDto ->
            streamDto.toEntity(streamDto.creatorId == MY_ID)
        }.run {
            when (StreamOperationType.fromString(event.action)) {
                StreamOperationType.CREATE -> streamDao.insertStreams(this)
                StreamOperationType.DELETE -> streamDao.deleteStreams(this)
                StreamOperationType.UPDATE -> Unit
            }
        }
    }

    companion object {
        private const val STREAM_EVENT_TYPES = "[\"stream\"]"
        private const val NAME_PROPERTY = "name"
    }
}