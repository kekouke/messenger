package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.streams.form.newstream.CreateStreamState
import ru.tinkoff.kotea.core.dsl.DslUpdate

private const val TAG = "AddNewStreamUpdate"

class CreateStreamUpdate(private val router: Router) :
    DslUpdate<CreateStreamState, CreateStreamEvent, CreateStreamCommand, CreateStreamNews>() {

    override fun NextBuilder.update(event: CreateStreamEvent) = when (event) {
        is CreateStreamEvent.UI -> handleUiEvent(event)
        is CreateStreamEvent.Result -> handleResultEvent(event)
    }

    private fun NextBuilder.handleUiEvent(event: CreateStreamEvent.UI) = when (event) {
        is CreateStreamEvent.UI.CreateStream -> handleUiCreateStream(event)
        CreateStreamEvent.UI.NavigateBack -> handleUiNavigateBack()
    }

    private fun NextBuilder.handleUiCreateStream(event: CreateStreamEvent.UI.CreateStream) {
        val streamName = event.name.trim()

        if (streamName.isEmpty()) {
            news(CreateStreamNews.EmptyStreamNameError)
        } else {
            state { copy(isLoading = true) }
            commands(CreateStreamCommand.CreateStream(streamName))
        }
    }

    private fun handleUiNavigateBack() = navigateBack()

    private fun navigateBack() = router.exit()

    private fun NextBuilder.handleResultEvent(event: CreateStreamEvent.Result) = when (event) {
        CreateStreamEvent.Result.CreateStreamSuccess -> navigateBack()
        CreateStreamEvent.Result.AlreadyExistsError -> handleResultAlreadyExistsError()
        is CreateStreamEvent.Result.NetworkError -> handleResultNetworkError(event)
        is CreateStreamEvent.Result.RefreshStreamsError -> handleResultRefreshStreamsError(event)
    }

    private fun NextBuilder.handleResultAlreadyExistsError() {
        state { copy(isLoading = false) }
        news(CreateStreamNews.AlreadyExistsError)
    }

    private fun NextBuilder.handleResultNetworkError(event: CreateStreamEvent.Result.NetworkError) {
        Log.d(TAG, event.throwable.toString())

        state { copy(isLoading = false) }
        news(CreateStreamNews.NetworkError)
    }

    private fun NextBuilder.handleResultRefreshStreamsError(
        event: CreateStreamEvent.Result.RefreshStreamsError
    ) {
        Log.d(TAG, event.throwable.toString())
        news(CreateStreamNews.RefreshStreamsError)
    }
}