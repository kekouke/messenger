package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Page
import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.ReactionOperationType
import com.kekouke.tfsspring.domain.usecases.ChatUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChatCommandFlowHandlerTest : BaseChatCommandFlowHandlerTest() {

    @Test
    fun `WHEN command is ShowChat AND messages retrieval success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val mockMessages = getMockMessages()

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(Page(mockMessages, true)))
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns emptyFlow()
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assertEquals(ChatEvent.Result.ShowChat(mockMessages), actual.last())
        coVerify { mockUseCase.saveMessages(mockMessages, mockTopic) }
        coVerify { mockUseCase.subscribeOnChatEvents(mockTopic) }
    }

    @Test
    fun `WHEN command is ShowChat AND messages retrieval error`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val errorResult = Result.failure<Page>(Exception("test error"))

        coEvery { mockUseCase.getMessages(mockTopic) } returns flowOf(errorResult)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.size == 1)
        assert(actual.first() is ChatEvent.Result.LoadMessagesError)
    }

    @Test
    fun `WHEN command is ShowChat AND subscribeOnChatEvents emits MessageEvent with received message`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val mockPage = Page(emptyList(), true)
        val mockMessage = getMockMessage(isReceivedMessage = true)
        val pollEvent = PollEvent.MessageEvent(mockMessage)

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(mockPage))
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns flowOf(pollEvent)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assertEquals(ChatEvent.Result.UpdateChat::class, actual.last()::class)

        val expectedMessages = mockPage.content + mockMessage
        val actualMessages = (actual.last() as ChatEvent.Result.UpdateChat).messages
        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `WHEN command is ShowChat AND subscribeOnChatEvents emits MessageEvent with sent message`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val mockPage = Page(emptyList(), true)
        val mockMessage = getMockMessage(isReceivedMessage = false)
        val pollEvent = PollEvent.MessageEvent(mockMessage)

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(mockPage))
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns flowOf(pollEvent)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assertEquals(ChatEvent.Result.SendMessageSuccess::class, actual.last()::class)

        val expectedMessages = mockPage.content + mockMessage
        val actualMessages = (actual.last() as ChatEvent.Result.SendMessageSuccess).messages
        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `WHEN command is ShowChat AND subscribeOnChatEvents emits ReactionEvent to add reaction`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val mockMessage = getMockMessage(id = 10)
        val mockPage = Page(listOf(mockMessage), true)
        val reaction = Reaction("test", "")
        val pollEvent = PollEvent.ReactionEvent(
            mockMessage.id,
            reaction,
            ReactionOperationType.ADD,
            false
        )

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(mockPage))
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns flowOf(pollEvent)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assertEquals(ChatEvent.Result.UpdateChat::class, actual.last()::class)

        val expectedMessages = listOf(mockMessage.copy(reactions = listOf(reaction)))
        val actualMessages = (actual.last() as ChatEvent.Result.UpdateChat).messages
        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `WHEN command is ShowChat AND subscribeOnChatEvents emits ReactionEvent to remove reaction`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val reaction = Reaction("test", "")
        val mockMessage = getMockMessage(id = 10).copy(reactions = listOf(reaction))
        val mockPage = Page(listOf(mockMessage), true)
        val pollEvent = PollEvent.ReactionEvent(
            mockMessage.id,
            reaction,
            ReactionOperationType.REMOVE,
            false
        )

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(mockPage))
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns flowOf(pollEvent)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assertEquals(ChatEvent.Result.UpdateChat::class, actual.last()::class)

        val expectedMessages = listOf(mockMessage.copy(reactions = emptyList()))
        val actualMessages = (actual.last() as ChatEvent.Result.UpdateChat).messages
        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `WHEN command is ShowChat AND subscribeOnChatEvents failed`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.ShowChat(mockTopic)
        val mockPage = Page(emptyList(), true)
        val pollEvent = PollEvent.RegisterError(Exception())

        coEvery { mockUseCase.getMessages(any()) } returns flowOf(Result.success(mockPage))
        coEvery { mockUseCase.saveMessages(any(), any()) } returns Unit
        coEvery { mockUseCase.subscribeOnChatEvents(any()) } returns flowOf(pollEvent)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.last() is ChatEvent.Result.RegisterEventQueueError)
    }

    @Test
    fun `should not load next page if gotLastPage is true`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val spy = spyk(ChatCommandFlowHandler(mockUseCase), recordPrivateCalls = true)
        val command = ChatCommand.LoadNextPage(mockTopic)
        val propertyName = "gotLastPage"

        every { spy getProperty propertyName } returns true

        val actual = spy.handle(flowOf(command)).toList()

        assert(actual.isEmpty())
    }

    @Test
    fun `WHEN command is LoadNextPage AND gotLastPage is false AND operation was success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val spy = spyk(ChatCommandFlowHandler(mockUseCase), recordPrivateCalls = true)
        val command = ChatCommand.LoadNextPage(mockTopic)
        val propertyName = "gotLastPage"
        val successResult = Result.success(Page(getMockMessages(), true))

        every { spy getProperty propertyName } returns false
        coEvery { mockUseCase.loadNextPage(any()) } returns flowOf(successResult)

        val actual = spy.handle(flowOf(command)).toList()

        assert(actual.size == 2)
        assert(actual[0] is ChatEvent.Result.StartProcess)
        assert(actual[1] is ChatEvent.Result.UpdateChat)
    }

    @Test
    fun `WHEN command is LoadNextPage AND gotLastPage is false AND operation was not success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val spy = spyk(ChatCommandFlowHandler(mockUseCase), recordPrivateCalls = true)
        val command = ChatCommand.LoadNextPage(mockTopic)
        val propertyName = "gotLastPage"
        val errorResult = Result.failure<Page>(Exception())

        every { spy getProperty propertyName } returns false
        coEvery { mockUseCase.loadNextPage(any()) } returns flowOf(errorResult)

        val actual = spy.handle(flowOf(command)).toList()

        assert(actual.size == 2)
        assert(actual[0] is ChatEvent.Result.StartProcess)
        assert(actual[1] is ChatEvent.Result.LoadMessagesError)
    }

    @Test
    fun `WHEN command is SendMessage ANS operation was success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.SendMessage(mockTopic, "test message")

        coEvery { mockUseCase.sendMessage(any(), any(), any()) } returns Result.success(Unit)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.isEmpty())
    }

    @Test
    fun `WHEN command is SendMessage ANS operation was not success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val command = ChatCommand.SendMessage(mockTopic, "test message")
        val errorResult = Result.failure<Unit>(Exception())

        coEvery { mockUseCase.sendMessage(any(), any(), any()) } returns errorResult

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.size == 1)
        assert(actual.first() is ChatEvent.Result.SendMessageError)
    }

    @Test
    fun `WHEN command is AddReaction ANS operation was success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val messageId = 0
        val command = ChatCommand.AddReaction(messageId, getMockReaction())

        coEvery { mockUseCase.addReaction(any(), any()) } returns Result.success(true)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.isEmpty())
    }

    @Test
    fun `WHEN command is AddReaction ANS operation was not success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val messageId = 0
        val command = ChatCommand.AddReaction(messageId, getMockReaction())
        val errorResult = Result.failure<Boolean>(Exception())

        coEvery { mockUseCase.addReaction(any(), any()) } returns errorResult

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.size == 1)
        assert(actual.first() is ChatEvent.Result.ReactionError)
    }

    @Test
    fun `WHEN command is RemoveReaction ANS operation was success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val messageId = 0
        val command = ChatCommand.RemoveReaction(messageId, getMockReaction())

        coEvery { mockUseCase.removeReaction(any(), any()) } returns Result.success(true)

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.isEmpty())
    }

    @Test
    fun `WHEN command is RemoveReaction ANS operation was not success`() = runTest {
        val mockUseCase = mockk<ChatUseCase>()
        val commandFlowHandler = ChatCommandFlowHandler(mockUseCase)
        val messageId = 0
        val command = ChatCommand.RemoveReaction(messageId, getMockReaction())
        val errorResult = Result.failure<Boolean>(Exception())

        coEvery { mockUseCase.removeReaction(any(), any()) } returns errorResult

        val actual = commandFlowHandler.handle(flowOf(command)).toList()

        assert(actual.size == 1)
        assert(actual.first() is ChatEvent.Result.ReactionError)
    }
}