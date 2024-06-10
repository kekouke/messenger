package com.kekouke.tfsspring.presentation.streams.tab.tea

import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType

sealed interface StreamsTabCommand {
    data class DisplayStreams(val streamsType: StreamsType) : StreamsTabCommand
    data class LoadStreams(val streamsType: StreamsType) : StreamsTabCommand
    data class LoadTopics(val stream: Stream) : StreamsTabCommand
    data class FilterStreams(val query: String, val streamsType: StreamsType) : StreamsTabCommand
}