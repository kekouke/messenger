package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

import com.kekouke.tfsspring.domain.model.AddNewStreamResult
import com.kekouke.tfsspring.domain.usecases.StreamsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class CreateStreamCommandFlowHandler @Inject constructor(
    private val streamsUseCase: StreamsUseCase
) : CommandsFlowHandler<CreateStreamCommand, CreateStreamEvent.Result> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<CreateStreamCommand>): Flow<CreateStreamEvent.Result> {
        return commands.flatMapLatest { command ->
            when (command) {
                is CreateStreamCommand.CreateStream -> handleCreateStream(command)
            }
        }
    }

    private fun handleCreateStream(command: CreateStreamCommand.CreateStream) = flow {
        when (val result = streamsUseCase.createStream(command.streamName)) {
            AddNewStreamResult.Success -> {
                emit(CreateStreamEvent.Result.CreateStreamSuccess)
            }

            AddNewStreamResult.AlreadyExistsError -> {
                emit(CreateStreamEvent.Result.AlreadyExistsError)
            }

            is AddNewStreamResult.NetworkError -> {
                emit(CreateStreamEvent.Result.NetworkError(result.throwable))
            }

            is AddNewStreamResult.RefreshStreamsError -> {
                emit(CreateStreamEvent.Result.RefreshStreamsError(result.throwable))
            }
        }
    }
}