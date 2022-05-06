package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.sohohouse.seven.R

class PageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var pageCount: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var position: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val spacing: Int

    private val rect: Rect = Rect()

    private val primaryPaint: Paint
    private val secondaryPaint: Paint

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator)
        val primaryColor = ta.getColor(R.styleable.PageIndicator_primary_color, Color.TRANSPARENT)
        primaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = primaryColor
            this.style = Paint.Style.FILL
        }
        val secondaryColor =
            ta.getColor(R.styleable.PageIndicator_secondary_color, Color.TRANSPARENT)
        secondaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = secondaryColor
            this.style = Paint.Style.FILL
        }
        spacing = ta.getDimensionPixelSize(R.styleable.PageIndicator_spacing, 0)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (pageCount == 0) return

        val width = (measuredWidth - (pageCount - 1) * spacing) / pageCount

        for (pos in 0 until pageCount) {
            val left = width * pos + spacing * pos
            val right = left + width
            val top = paddingTop
            val bottom = measuredHeight - (paddingTop + paddingBottom)
            rect.set(left, top, right, bottom)

            canvas?.drawRect(rect, if (pos > position) secondaryPaint else primaryPaint)
        }
    }

}