package com.kekouke.tfsspring.domain.usecases

import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.repository.StreamsRepository
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamsUseCase @Inject constructor(private val streamsRepository: StreamsRepository) {

    fun getCachedStreams(type: StreamsType): Flow<List<Stream>> =
        streamsRepository.getStreams(type)

    suspend fun fetchStreams(type: StreamsType) = streamsRepository.fetchStreams(type)

    fun getTopics(stream: Stream): Flow<Result<List<Topic>>> = streamsRepository.getTopics(stream)

    suspend fun createStream(streamName: String) = streamsRepository.createStream(streamName)

    suspend fun listenToStreamEvents() = streamsRepository.subscribeToStreamsEvents()
}