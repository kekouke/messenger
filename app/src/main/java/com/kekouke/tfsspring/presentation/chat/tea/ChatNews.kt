package com.kekouke.tfsspring.presentation.chat.tea

sealed interface ChatNews {
    data object LoadMessagesError : ChatNews
    data object SendMessageError : ChatNews
    data object ChangeReactionError : ChatNews
    data object RegisterQueueError : ChatNews
    data object MessageSent : ChatNews
    data object NavigateBack : ChatNews
}