package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.presentation.chat.ChatState
import org.junit.Assert

abstract class BaseChatUpdateTest {

    protected val mockTopic get() = Topic("topicName", 0, "streamName")

    protected fun contentState(
        messages: List<Message> = emptyList(),
        needToScroll: Boolean = true,
        process: Boolean = false
    ) = ChatState.Content(
        messages,
        needToScroll,
        process
    )

    protected fun getMockReaction(selected: Boolean = false) = Reaction(
        "",
        "",
        selected = selected
    )

    protected fun getMockMessages() = listOf(
        Message(
            id = 0,
            content = "test message",
            isReceivedMessage = false,
            senderId = 0,
            timestampMilliseconds = 0
        )
    )

    protected fun assertDispatchCommand(commands: List<ChatCommand>, target: ChatCommand) {
        Assert.assertTrue(
            "List of commands should contains $target",
            commands.contains(target)
        )
    }

    protected fun assertNotDispatchCommand(commands: List<ChatCommand>, target: ChatCommand) {
        Assert.assertFalse(
            "List of commands shouldn't contains $target",
            commands.contains(target)
        )
    }

    protected fun assertSendNews(news: List<ChatNews>, target: ChatNews) {
        Assert.assertTrue("List of news should contains $target", news.contains(target))
    }
}