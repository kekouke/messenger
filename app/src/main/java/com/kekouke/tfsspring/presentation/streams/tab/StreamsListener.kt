package com.kekouke.tfsspring.presentation.streams.tab

import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic

interface StreamsListener {
    fun onStreamClick(stream: Stream)
    fun onTopicClick(topic: Topic)
}