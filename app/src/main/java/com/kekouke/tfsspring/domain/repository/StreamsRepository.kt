package com.kekouke.tfsspring.domain.repository

import com.kekouke.tfsspring.domain.model.AddNewStreamResult
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import kotlinx.coroutines.flow.Flow

interface StreamsRepository {
    fun getStreams(type: StreamsType): Flow<List<Stream>>
    suspend fun fetchStreams(type: StreamsType): Result<Unit>
    fun getTopics(stream: Stream): Flow<Result<List<Topic>>>
    suspend fun createStream(streamName: String): AddNewStreamResult
    suspend fun subscribeToStreamsEvents(): Flow<PollEvent.Streams>
}