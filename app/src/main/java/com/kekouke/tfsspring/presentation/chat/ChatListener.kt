package com.kekouke.tfsspring.presentation.chat

import com.kekouke.tfsspring.domain.model.Reaction

interface ChatListener {
    fun onAddEmojiRequest(messageId: Int)
    fun onEmojiClick(messageId: Int, reaction: Reaction)
}