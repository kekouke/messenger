package com.kekouke.tfsspring.presentation.chat.tea

import android.util.Log
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.presentation.chat.ChatState
import ru.tinkoff.kotea.core.dsl.DslUpdate

private const val TAG = "ChatUpdate"

class ChatUpdate(
    private val currentTopic: Topic
) : DslUpdate<ChatState, ChatEvent, ChatCommand, ChatNews>() {

    override fun NextBuilder.update(event: ChatEvent) = when (event) {
        is ChatEvent.UI -> handleUiEvent(event)
        is ChatEvent.Result -> handleResultEvent(event)
    }

    private fun NextBuilder.handleUiEvent(event: ChatEvent.UI) = when (event) {
        is ChatEvent.UI.ShowChat -> handleUiShowChat()
        ChatEvent.UI.LoadNextPage -> handleUiLoadNextPage()
        is ChatEvent.UI.SendMessage -> handleUiSendMessage(event)
        is ChatEvent.UI.ChangeReactions -> handleUiChangeReactions(event)
        is ChatEvent.UI.NavigateBack -> handleUiNavigateBack()
    }

    private fun NextBuilder.handleUiShowChat() {
        state { ChatState.Loading }
        commands(ChatCommand.ShowChat(currentTopic))
    }

    private fun NextBuilder.handleUiLoadNextPage() {
        if (contentState().process.not()) {
            commands(ChatCommand.LoadNextPage(currentTopic))
        }
    }

    private fun NextBuilder.handleUiSendMessage(event: ChatEvent.UI.SendMessage) {
        state { contentState().copy(process = true) }
        commands(ChatCommand.SendMessage(currentTopic, event.text))
    }

    private fun NextBuilder.handleUiChangeReactions(event: ChatEvent.UI.ChangeReactions) {
        state { contentState().copy(process = true) }

        if (event.reaction.selected) {
            commands(ChatCommand.AddReaction(event.messageId, event.reaction))
        } else {
            commands(ChatCommand.RemoveReaction(event.messageId, event.reaction))
        }
    }

    private fun NextBuilder.handleUiNavigateBack() = news(ChatNews.NavigateBack)

    private fun NextBuilder.handleResultEvent(event: ChatEvent.Result) = when (event) {
        is ChatEvent.Result.ShowChat -> handleResultShowChat(event)
        is ChatEvent.Result.UpdateChat -> handleResultUpdateChat(event)
        is ChatEvent.Result.LoadMessagesError -> handleResultLoadMessagesError(event)
        is ChatEvent.Result.SendMessageError -> handleResultSendMessageError(event)
        is ChatEvent.Result.SendMessageSuccess -> handleResultSendMessageSuccess(event)
        is ChatEvent.Result.ReactionError -> handleResultReactionError(event)
        is ChatEvent.Result.RegisterEventQueueError -> handleRegisterEventQueueError(event)
        ChatEvent.Result.StartProcess -> handleResultStartProcess()
    }

    private fun NextBuilder.handleResultShowChat(event: ChatEvent.Result.ShowChat) {
        state {
            ChatState.Content(
                messages = event.messages,
                needToScroll = true,
                process = false
            )
        }
    }

    private fun NextBuilder.handleResultUpdateChat(event: ChatEvent.Result.UpdateChat) {
        state {
            ChatState.Content(
                messages = event.messages,
                needToScroll = false,
                process = false
            )
        }
    }

    private fun NextBuilder.handleResultLoadMessagesError(event: ChatEvent.Result.LoadMessagesError) {
        Log.d(TAG, event.throwable.toString())

        when (state) {
            is ChatState.Content -> {
                state { contentState().copy(process = false) }
                news(ChatNews.LoadMessagesError)
            }

            else -> state { ChatState.MessagesLoadError }
        }
    }

    private fun NextBuilder.handleResultSendMessageError(event: ChatEvent.Result.SendMessageError) {
        Log.d(TAG, event.throwable.toString())

        state { contentState().copy(process = false) }
        news(ChatNews.SendMessageError)
    }

    private fun NextBuilder.handleResultSendMessageSuccess(event: ChatEvent.Result.SendMessageSuccess) {
        handleResultShowChat(ChatEvent.Result.ShowChat(event.messages))
        news(ChatNews.MessageSent)
    }

    private fun NextBuilder.handleResultReactionError(event: ChatEvent.Result.ReactionError) {
        Log.d(TAG, event.throwable.toString())

        state { contentState().copy(process = false) }
        news(ChatNews.ChangeReactionError)
    }

    private fun NextBuilder.handleRegisterEventQueueError(
        event: ChatEvent.Result.RegisterEventQueueError
    ) {
        Log.d(TAG, event.throwable.toString())

        news(ChatNews.RegisterQueueError)
    }

    private fun NextBuilder.handleResultStartProcess() {
        state { contentState().copy(process = true, needToScroll = false) }
    }

    private fun NextBuilder.contentState(): ChatState.Content = state as ChatState.Content
}