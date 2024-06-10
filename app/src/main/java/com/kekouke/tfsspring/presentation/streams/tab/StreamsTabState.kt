package com.kekouke.tfsspring.presentation.streams.tab

import com.kekouke.tfsspring.domain.model.Stream

sealed class StreamsTabState {
    data object Initial : StreamsTabState()
    data object LoadStreamsError : StreamsTabState()
    data object Loading : StreamsTabState()
    data class Content(val content: List<Stream>, val process: Boolean = false) : StreamsTabState()
}