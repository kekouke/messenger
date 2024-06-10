package com.kekouke.tfsspring.presentation.streams.tab.tea

import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic

sealed interface StreamsTabEvent {

    sealed interface UI : StreamsTabEvent {
        data object DisplayStreams : UI
        data class ShowTopics(val stream: Stream) : UI
        data class HideTopics(val stream: Stream) : UI
        data class Search(val query: String) : UI
        data class NavigateToChat(val topic: Topic) : UI
    }

    sealed interface Result : StreamsTabEvent {
        class DisplayStreams(val streams: List<Stream>) : Result
        class DisplayFilteredStreams(val streams: List<Stream>) : Result
        data class ShowTopics(val topics: List<Topic>, val stream: Stream) : Result
        data class LoadStreamsError(val throwable: Throwable) : Result
        data class LoadTopicsError(val throwable: Throwable) : Result
        data class RegisterError(val throwable: Throwable) : Result
    }
}