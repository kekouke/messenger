package com.kekouke.tfsspring.presentation.chat.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import kotlin.math.max
import com.kekouke.tfsspring.R as TfSpringR

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val ivAddNewReaction = ImageView(context).apply {
        setImageResource(TfSpringR.drawable.ic_btn_plus)
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            resources.getDimension(TfSpringR.dimen.emoji_view_height_30dp).toInt()
        )
    }

    private val verticalSpacing =
        resources.getDimension(TfSpringR.dimen.flexbox_vertical_spacing).toInt()
    private val horizontalSpacing =
        resources.getDimension(TfSpringR.dimen.flexbox_horizontal_spacing).toInt()

    private val horizontalPadding: Int
        get() = paddingStart + paddingEnd

    private val verticalPadding: Int
        get() = paddingTop + paddingBottom

    private val gestureDetector = GestureDetector(context, MyGestureListener())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var lineCount = 1
        var maxLineWidth = 0
        var currentOccupiedLineWidth = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - horizontalPadding

        repeat(childCount + 1) { index ->
            val child = getChildAt(index) ?: ivAddNewReaction
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val childWidth = child.measuredWidth

            if (currentOccupiedLineWidth + childWidth > widthSize) {
                lineCount++
                currentOccupiedLineWidth = 0
            }
            currentOccupiedLineWidth += (childWidth + horizontalSpacing)

            maxLineWidth = max(maxLineWidth, currentOccupiedLineWidth)
        }

        val childHeight = (children.firstOrNull() ?: ivAddNewReaction).measuredHeight
        val contentHeight = lineCount * (childHeight + verticalSpacing) - verticalSpacing
        val actualHeight = contentHeight + verticalPadding

        val contentWidth = max(0, maxLineWidth - horizontalSpacing + horizontalPadding)
        val actualWidth = resolveSize(contentWidth, widthMeasureSpec)

        setMeasuredDimension(actualWidth, actualHeight)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val rect = Rect()
        ivAddNewReaction.getHitRect(rect)
        return rect.contains(ev.x.toInt(), ev.y.toInt())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLine = 0
        var currentOccupiedLineWidth = 0

        repeat(childCount + 1) { index ->
            val child = getChildAt(index) ?: ivAddNewReaction
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (currentOccupiedLineWidth + childWidth > measuredWidth) {
                currentLine++
                currentOccupiedLineWidth = 0
            }

            val left = paddingLeft + currentOccupiedLineWidth
            val top = paddingTop + currentLine * (childHeight + verticalSpacing)
            child.layout(left, top, left + childWidth, top + childHeight)

            currentOccupiedLineWidth += (childWidth + horizontalSpacing)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawChild(canvas, ivAddNewReaction, drawingTime)
    }

    fun setOnAddEmojiListener(listener: ((View) -> Unit)?) {
        ivAddNewReaction.setOnClickListener(listener)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val rect = Rect()
            ivAddNewReaction.getHitRect(rect)
            if (rect.contains(e.x.toInt(), e.y.toInt())) {
                ivAddNewReaction.performClick()
            }
            return true
        }
    }

}