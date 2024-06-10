package com.kekouke.tfsspring.presentation.chat

import com.kekouke.tfsspring.domain.model.Message

sealed class ChatState {

    data object Initial : ChatState()
    data object Loading : ChatState()

    data object MessagesLoadError : ChatState()

    data class Content(
        val messages: List<Message>,
        val needToScroll: Boolean = false,
        val process: Boolean = false
    ) : ChatState()
}