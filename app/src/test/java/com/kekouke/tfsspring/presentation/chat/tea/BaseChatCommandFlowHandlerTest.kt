package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.Topic

abstract class BaseChatCommandFlowHandlerTest {
    protected val mockTopic get() = Topic("topicName", 0, "streamName")

    protected fun getMockMessages() = listOf(
        getMockMessage()
    )

    protected fun getMockMessage(id: Int = 0, isReceivedMessage: Boolean = false) = Message(
        id = id,
        content = "test message",
        isReceivedMessage = isReceivedMessage,
        senderId = 0,
        timestampMilliseconds = 0
    )

    protected fun getMockReaction(selected: Boolean = false) = Reaction(
        "",
        "",
        selected = selected
    )
}