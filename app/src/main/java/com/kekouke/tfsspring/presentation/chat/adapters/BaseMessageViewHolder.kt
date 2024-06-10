package com.kekouke.tfsspring.presentation.chat.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.presentation.chat.ChatListener
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.presentation.chat.ui.EmojiView
import com.kekouke.tfsspring.presentation.chat.ui.MessageView
import com.kekouke.tfsspring.R as TfSpringR

abstract class BaseMessageViewHolder(
    private val messageView: MessageView,
    private val chatListener: ChatListener
) : RecyclerView.ViewHolder(messageView) {

    open fun bind(message: Message) {
        messageView.messageText = message.content
        messageView.removeAllEmoji()

        messageView.setOnLongClickListener {
            chatListener.onAddEmojiRequest(message.id)
            true
        }
        messageView.setOnAddEmojiListener {
            chatListener.onAddEmojiRequest(message.id)
        }

        insertReactionsFor(message)
    }

    private fun insertReactionsFor(message: Message) {
        val onEmojiCLickListener: (View, Reaction) -> Unit = { emojiView, reaction ->
            (emojiView as EmojiView).apply {
                reaction.copy(selected = isSelected).run {
                    chatListener.onEmojiClick(message.id, this)
                }
            }
        }

        message.reactions.forEach { reaction ->
            val emojiView = EmojiView(messageView.context).apply {
                emoji = reaction.code
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    resources.getDimension(TfSpringR.dimen.emoji_view_height_30dp).toInt()
                )
                isSelected = reaction.selected
                quantity = reaction.count
                setOnClickListener { view -> onEmojiCLickListener(view, reaction) }
            }

            messageView.addEmoji(emojiView)
        }
    }
}