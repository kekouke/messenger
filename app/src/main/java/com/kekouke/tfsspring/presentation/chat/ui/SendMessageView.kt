package com.kekouke.tfsspring.presentation.chat.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.kekouke.tfsspring.databinding.ItemSendMessageBinding
import kotlin.properties.ReadOnlyProperty
import com.kekouke.tfsspring.R as TfSpringR

class SendMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : MessageView(context, attrs, defStyleAttr, defStyleRes) {

    private val binding by lazy {
        ItemSendMessageBinding.bind(this)
    }

    init {
        LayoutInflater.from(context).inflate(
            TfSpringR.layout.item_send_message,
            this,
            true
        )
    }

    override fun getMessageTextView(): ReadOnlyProperty<MessageView, TextView> {
        return ReadOnlyProperty { _, _ ->
            binding.tvMessageText
        }
    }

    override fun getEmojiContainerView(): ReadOnlyProperty<MessageView, FlexBoxLayout> {
        return ReadOnlyProperty { _, _ ->
            binding.emojiContainer
        }
    }
}