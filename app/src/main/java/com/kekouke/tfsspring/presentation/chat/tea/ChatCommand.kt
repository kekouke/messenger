package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.Topic

sealed interface ChatCommand {
    data class ShowChat(val topic: Topic) : ChatCommand
    data class LoadNextPage(val topic: Topic) : ChatCommand
    data class SendMessage(val topic: Topic, val text: String) : ChatCommand
    data class AddReaction(val messageId: Int, val reaction: Reaction) : ChatCommand
    data class RemoveReaction(val messageId: Int, val reaction: Reaction) : ChatCommand
}