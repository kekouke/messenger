package com.kekouke.tfsspring.presentation.streams.tab.tea

sealed interface StreamsTabNews {
    data object LoadTopicsError : StreamsTabNews
    data object LoadStreamsError : StreamsTabNews
    data object RegisterQueueError : StreamsTabNews
}