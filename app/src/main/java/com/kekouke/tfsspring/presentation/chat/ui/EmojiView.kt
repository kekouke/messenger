package com.kekouke.tfsspring.presentation.chat.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntRange
import com.kekouke.tfsspring.R as TfSpringR

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val horizontalPadding: Int =
        resources.getDimension(TfSpringR.dimen.emoji_view_horizontal_padding).toInt()
    private val verticalPadding: Int =
        resources.getDimension(TfSpringR.dimen.emoji_view_vertical_padding).toInt()

    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = resources.getDimension(TfSpringR.dimen.text_14sp)
    }

    init {
        isClickable = true
        setBackgroundResource(TfSpringR.drawable.bg_emoji)
    }

    private val contentBounds = Rect()

    var emoji: String = ""
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
                invalidate()
            }
        }

    @IntRange(from = 0)
    var quantity: Int = 1
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
                invalidate()
            }
        }

    private val displayedContent: String
        get() = "$emoji $quantity"

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(displayedContent, 0, displayedContent.length, contentBounds)
        val width = resolveSize(contentBounds.width() + 2 * horizontalPadding, widthMeasureSpec)
        val height = resolveSize(contentBounds.height() + 2 * verticalPadding, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val topOffset = height / 2 - contentBounds.exactCenterY()
        val leftOffset = width / 2 - contentBounds.exactCenterX()
        canvas.drawText(displayedContent, leftOffset, topOffset, textPaint)
    }

    override fun performClick(): Boolean {
        isSelected = !isSelected
        return super.performClick()
    }
}