package com.kekouke.tfsspring.presentation.chat.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.properties.ReadOnlyProperty

abstract class MessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val tvMessageText: TextView by getMessageTextView()
    private val emojiContainer: FlexBoxLayout by getEmojiContainerView()

    init {
        layoutParams = MarginLayoutParams(
            MarginLayoutParams.MATCH_PARENT,
            MarginLayoutParams.WRAP_CONTENT
        )
    }

    var messageText: String = ""
        set(value) {
            field = value
            tvMessageText.text = value
        }

    fun addEmoji(emojiView: EmojiView) {
        if (emojiContainer.visibility == View.GONE) {
            emojiContainer.visibility = View.VISIBLE
        }
        emojiContainer.addView(emojiView)
    }

    fun removeAllEmoji() {
        emojiContainer.removeAllViews()
        emojiContainer.visibility = View.GONE
    }

    fun setOnAddEmojiListener(listener: ((View) -> Unit)?) {
        emojiContainer.setOnAddEmojiListener(listener)
    }

    protected abstract fun getMessageTextView(): ReadOnlyProperty<MessageView, TextView>
    protected abstract fun getEmojiContainerView(): ReadOnlyProperty<MessageView, FlexBoxLayout>
}