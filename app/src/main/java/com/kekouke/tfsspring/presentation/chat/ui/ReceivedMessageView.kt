package com.kekouke.tfsspring.presentation.chat.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kekouke.tfsspring.databinding.ItemReceivedMessageBinding
import kotlin.properties.ReadOnlyProperty
import com.kekouke.tfsspring.R as TfSpringR

class ReceivedMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : MessageView(context, attrs, defStyleAttr, defStyleRes) {

    private val binding by lazy {
        ItemReceivedMessageBinding.bind(this)
    }

    var username: String = ""
        set(value) {
            field = value
            binding.tvUsername.text = value
        }

    fun setAvatar(uri: String) {
        Glide.with(context)
            .load(uri)
            .error(TfSpringR.drawable.placeholder_avatar)
            .placeholder(TfSpringR.drawable.placeholder_avatar)
            .into(binding.ivAvatar)
    }

    init {
        LayoutInflater.from(context).inflate(
            TfSpringR.layout.item_received_message,
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