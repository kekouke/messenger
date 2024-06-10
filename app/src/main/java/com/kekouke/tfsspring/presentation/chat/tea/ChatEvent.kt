package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Reaction

sealed interface ChatEvent {

    sealed interface UI : ChatEvent {
        data object ShowChat : UI
        data object LoadNextPage : UI
        data class SendMessage(val text: String) : UI
        data class ChangeReactions(val messageId: Int, val reaction: Reaction) : UI
        data object NavigateBack : UI
    }

    sealed interface Result : ChatEvent {
        data class ShowChat(val messages: List<Message>): Result
        data class UpdateChat(val messages: List<Message>) : Result
        data class LoadMessagesError(val throwable: Throwable) : Result
        data class SendMessageSuccess(val messages: List<Message>) : Result
        data class SendMessageError(val throwable: Throwable) : Result
        data class ReactionError(val throwable: Throwable) : Result
        data object StartProcess : Result
        data class RegisterEventQueueError(val throwable: Throwable) : Result
    }
}