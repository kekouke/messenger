package com.kekouke.tfsspring.presentation.streams.tab.tea

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.navigation.Screens
import com.kekouke.tfsspring.presentation.streams.tab.StreamsTabState
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.Result
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI
import ru.tinkoff.kotea.core.dsl.DslUpdate

private const val TAG = "StreamsTabUpdate"

class StreamTabUpdate(
    private val router: Router,
    private val streamsType: StreamsType
) : DslUpdate<StreamsTabState, StreamsTabEvent, StreamsTabCommand, StreamsTabNews>() {

    override fun NextBuilder.update(event: StreamsTabEvent) = when (event) {
        is UI -> handleUiEvent(event)
        is Result -> handleResultEvent(event)
    }

    private fun NextBuilder.handleUiEvent(event: UI) = when (event) {
        is UI.DisplayStreams -> handleUiDisplayStreams()
        is UI.ShowTopics -> handleUiShowTopics(event)
        is UI.HideTopics -> handleUiHideTopics(event)
        is UI.Search -> handleUiSearch(event)
        is UI.NavigateToChat -> handleUiNavigateToChat(event)
    }

    private fun NextBuilder.handleUiDisplayStreams() {
        state { StreamsTabState.Loading }
        commands(
            StreamsTabCommand.DisplayStreams(streamsType),
            StreamsTabCommand.LoadStreams(streamsType)
        )
    }

    private fun NextBuilder.handleUiShowTopics(event: UI.ShowTopics) {
        if (event.stream.topics == null) {
            commands(StreamsTabCommand.LoadTopics(event.stream))
            stateContent { copy(process = true) }

            return
        }

        stateContent {
            copy(content = replaceStream(content, event.stream.copy(expanded = true)))
        }
    }

    private fun NextBuilder.handleUiHideTopics(event: UI.HideTopics) {
        stateContent {
            copy(content = replaceStream(content, event.stream.copy(expanded = false)))
        }
    }

    private fun NextBuilder.handleUiSearch(event: UI.Search) {
        commands(StreamsTabCommand.FilterStreams(event.query, streamsType))
    }

    private fun handleUiNavigateToChat(event: UI.NavigateToChat) =
        router.navigateTo(Screens.Chat(event.topic))

    private fun NextBuilder.handleResultEvent(event: Result) = when (event) {
        is Result.DisplayStreams -> handleResultDisplayStreams(event)
        is Result.ShowTopics -> handleResultShowTopics(event)
        is Result.LoadStreamsError -> handleResultLoadStreamsError(event)
        is Result.LoadTopicsError -> handleResultLoadTopicsError(event)
        is Result.DisplayFilteredStreams -> handleResultDisplayFilteredStreams(event)
        is Result.RegisterError -> handleResultRegisterError(event)
    }

    private fun NextBuilder.handleResultDisplayStreams(event: Result.DisplayStreams) {
        state { StreamsTabState.Content(event.streams) }
    }

    private fun NextBuilder.handleResultShowTopics(event: Result.ShowTopics) {
        stateContent {
            val updatedStream = event.stream.copy(expanded = true, topics = event.topics)

            copy(content = replaceStream(content, updatedStream), process = false)
        }
    }

    private fun NextBuilder.handleResultLoadStreamsError(event: Result.LoadStreamsError) {
        Log.e(TAG, event.throwable.toString())

        state {
            when (this) {
                is StreamsTabState.Content -> {
                    news(StreamsTabNews.LoadStreamsError)
                    this
                }

                else -> StreamsTabState.LoadStreamsError
            }
        }
    }

    private fun NextBuilder.handleResultLoadTopicsError(event: Result.LoadTopicsError) {
        Log.e(TAG, event.throwable.toString())

        stateContent { copy(process = false) }
        news(StreamsTabNews.LoadTopicsError)
    }

    private fun NextBuilder.handleResultDisplayFilteredStreams(event: Result.DisplayFilteredStreams) {
        stateContent { copy(content = event.streams) }
    }

    private fun NextBuilder.handleResultRegisterError(event: Result.RegisterError) {
        Log.e(TAG, event.throwable.toString())
        news(StreamsTabNews.RegisterQueueError)
    }

    private fun replaceStream(streams: List<Stream>, stream: Stream): List<Stream> {
        return streams.toMutableList().apply {
            set(indexOfFirst { it.id == stream.id }, stream)
        }
    }

    private inline fun NextBuilder.stateContent(block: StreamsTabState.Content.() -> StreamsTabState) {
        state { (this as? StreamsTabState.Content)?.block() ?: this }
    }
}