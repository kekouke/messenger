package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.presentation.chat.ChatState
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChatUpdateTest : BaseChatUpdateTest() {

    @Test
    fun `WHEN event is ShowChat THEN set state to Loading and dispatch ShowShat command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val event = ChatEvent.UI.ShowChat

        val actual = chatUpdate.update(ChatState.Initial, event)
        val expectedState = ChatState.Loading

        assertEquals(expectedState, actual.state)
        assertDispatchCommand(actual.commands, ChatCommand.ShowChat(mockTopic))
    }

    @Test
    fun `WHEN event is LoadNextPage AND process is false THEN dispatch LoadNextPage command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState(process = false)
        val event = ChatEvent.UI.LoadNextPage

        val actual = chatUpdate.update(state, event)

        assertDispatchCommand(actual.commands, ChatCommand.LoadNextPage(mockTopic))
    }

    @Test
    fun `WHEN event is LoadNextPage AND process is true THEN doesn't dispatch LoadNextPage command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState(process = true)
        val event = ChatEvent.UI.LoadNextPage

        val actual = chatUpdate.update(state, event)

        assertNotDispatchCommand(actual.commands, ChatCommand.LoadNextPage(mockTopic))
    }


    @Test
    fun `WHEN event is SendMessage THEN set process to true and dispatch SendMessage command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState()
        val textMessage = ""
        val event = ChatEvent.UI.SendMessage(textMessage)

        val actual = chatUpdate.update(state, event)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(true, actualStateContent.process)
        assertDispatchCommand(actual.commands, ChatCommand.SendMessage(mockTopic, textMessage))
    }

    @Test
    fun `WHEN event is ChangeReactions THEN set process to true`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState()
        val event = ChatEvent.UI.ChangeReactions(0, getMockReaction())

        val actual = chatUpdate.update(state, event)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(true, actualStateContent.process)
    }

    @Test
    fun `WHEN event is ChangeReactions AND reaction is selected THEN dispatch AddReaction command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState()
        val reaction = getMockReaction(selected = true)
        val messageId = 0
        val event = ChatEvent.UI.ChangeReactions(messageId, reaction)

        val actual = chatUpdate.update(state, event)
        val expectedCommand = ChatCommand.AddReaction(messageId, reaction)

        assertDispatchCommand(actual.commands, expectedCommand)
    }

    @Test
    fun `WHEN event is ChangeReactions AND reaction isn't selected THEN dispatch RemoveReaction command`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState()
        val reaction = getMockReaction(selected = false)
        val messageId = 0
        val event = ChatEvent.UI.ChangeReactions(messageId, reaction)

        val actual = chatUpdate.update(state, event)
        val expectedCommand = ChatCommand.RemoveReaction(messageId, reaction)

        assertDispatchCommand(actual.commands, expectedCommand)
    }

    @Test
    fun `WHEN event is NavigateBack THEN send NavigateBack news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val state = contentState()
        val event = ChatEvent.UI.NavigateBack

        val actual = chatUpdate.update(state, event)

        assertSendNews(actual.news, ChatNews.NavigateBack)
    }

    @Test
    fun `WHEN result event is ShowChat THEN set content state with process is false and needToScroll is true`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val messages = getMockMessages()
        val resultEvent = ChatEvent.Result.ShowChat(messages)
        val anyState = ChatState.Initial

        val actual = chatUpdate.update(anyState, resultEvent)
        val expectedState = contentState(messages = messages, needToScroll = true, process = false)

        assertEquals(expectedState, actual.state)
    }

    @Test
    fun `WHEN result event is UpdateChat THEN set content state with process is false and needToScroll is false`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val messages = getMockMessages()
        val resultEvent = ChatEvent.Result.UpdateChat(messages)
        val anyState = ChatState.Initial

        val actual = chatUpdate.update(anyState, resultEvent)
        val expectedState = contentState(messages = messages, needToScroll = false, process = false)

        assertEquals(expectedState, actual.state)
    }

    @Test
    fun `WHEN result event is LoadMessagesError AND state is content THEN set process to false and send LoadMessagesError news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.LoadMessagesError(Exception())
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(false, actualStateContent.process)
        assertSendNews(actual.news, ChatNews.LoadMessagesError)
    }

    @Test
    fun `WHEN result event is LoadMessagesError AND state isn't content THEN set state to MessagesLoadError`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.LoadMessagesError(Exception())
        val state = ChatState.Loading

        val actual = chatUpdate.update(state, resultEvent)
        val expectedState = ChatState.MessagesLoadError

        assertEquals(expectedState, actual.state)
    }

    @Test
    fun `WHEN result event is SendMessageError THEN set process to false and send SendMessageError news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.SendMessageError(Exception())
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(false, actualStateContent.process)
        assertSendNews(actual.news, ChatNews.SendMessageError)
    }

    @Test
    fun `WHEN result event is SendMessageSuccess THEN update messages and set process to false and needToScroll to true and send MessageSent news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val messages = getMockMessages()
        val resultEvent = ChatEvent.Result.SendMessageSuccess(messages)
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(messages, actualStateContent.messages)
        assertEquals(true, actualStateContent.needToScroll)
        assertEquals(false, actualStateContent.process)
        assertSendNews(actual.news, ChatNews.MessageSent)
    }

    @Test
    fun `WHEN result event is ReactionError THEN set process to false and send ChangeReactionError news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.ReactionError(Exception())
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(false, actualStateContent.process)
        assertSendNews(actual.news, ChatNews.ChangeReactionError)
    }

    @Test
    fun `WHEN result event is RegisterEventQueueError THEN send RegisterQueueError news`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.RegisterEventQueueError(Exception())
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        assertSendNews(actual.news, ChatNews.RegisterQueueError)
    }

    @Test
    fun `WHEN result event is StartProcess THEN set process to true`() {
        val chatUpdate = ChatUpdate(mockTopic)
        val resultEvent = ChatEvent.Result.StartProcess
        val state = contentState()

        val actual = chatUpdate.update(state, resultEvent)
        val actualStateContent = actual.state as ChatState.Content

        assertEquals(true, actualStateContent.process)
    }
}