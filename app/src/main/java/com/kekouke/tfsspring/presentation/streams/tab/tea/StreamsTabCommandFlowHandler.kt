package com.kekouke.tfsspring.presentation.streams.tab.tea

import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.usecases.StreamsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class StreamsTabCommandFlowHandler @Inject constructor(
    private val streamsUseCase: StreamsUseCase
) : CommandsFlowHandler<StreamsTabCommand, StreamsTabEvent.Result> {

    private var currentFilterQuery = ""
        set(value) {
            field = value.trim()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<StreamsTabCommand>): Flow<StreamsTabEvent.Result> {
        return commands.flatMapMerge { command ->
            when (command) {
                is StreamsTabCommand.DisplayStreams -> handleDisplayStreams(command)
                is StreamsTabCommand.LoadStreams -> handleLoadStreams(command)
                is StreamsTabCommand.LoadTopics -> handleLoadTopics(command)
                is StreamsTabCommand.FilterStreams -> handleFilterStreams(command)
            }
        }
    }

    private fun handleLoadStreams(command: StreamsTabCommand.LoadStreams) = flow {
        streamsUseCase.fetchStreams(command.streamsType).fold(
            onSuccess = {
                streamsUseCase.listenToStreamEvents()
                    .catch { emit(StreamsTabEvent.Result.RegisterError(it)) }
                    .collect {}
            },
            onFailure = { emit(StreamsTabEvent.Result.LoadStreamsError(it)) }
        )
    }

    private fun handleDisplayStreams(command: StreamsTabCommand.DisplayStreams) = flow {
        streamsUseCase.getCachedStreams(command.streamsType).collect { streams ->
            if (streams.isNotEmpty()) {
                emit(StreamsTabEvent.Result.DisplayStreams(filterStreamsByQuery(streams)))
            }
        }
    }

    private fun handleLoadTopics(command: StreamsTabCommand.LoadTopics) = flow {
        streamsUseCase.getTopics(command.stream).collect { result ->
            result.fold(
                onSuccess = { emit(StreamsTabEvent.Result.ShowTopics(it, command.stream)) },
                onFailure = { emit(StreamsTabEvent.Result.LoadTopicsError(it)) }
            )
        }
    }

    private fun handleFilterStreams(command: StreamsTabCommand.FilterStreams) = flow {
        if (currentFilterQuery == command.query) return@flow

        currentFilterQuery = command.query
        val cachedStreams = streamsUseCase.getCachedStreams(command.streamsType).first()
        val filteredStreams = withContext(Dispatchers.IO) {
            filterStreamsByQuery(cachedStreams)
        }

        emit(StreamsTabEvent.Result.DisplayFilteredStreams(filteredStreams))
    }

    private fun filterStreamsByQuery(streams: List<Stream>): List<Stream> {
        if (currentFilterQuery.isEmpty()) return streams

        return streams.filter { it.name.contains(currentFilterQuery, ignoreCase = true) }
    }
}