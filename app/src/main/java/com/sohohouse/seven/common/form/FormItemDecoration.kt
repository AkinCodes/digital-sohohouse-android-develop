package com.sohohouse.seven.common.form

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor

class FormItemDecoration(context: Context, private val formAdapter: FormAdapter) :
    RecyclerView.ItemDecoration() {

    private val borderPaintAlpha = (255F * .56F).toInt()
    private val formVerticalMargin = context.resources.getDimensionPixelOffset(R.dimen.dp_16)

    private val paint = Paint().apply {
        strokeWidth = (context.resources.getDimensionPixelOffset(R.dimen.dp_1).toFloat() * 1.5f)
        color = context.getAttributeColor(R.attr.colorDrawableSecondary)
        alpha = borderPaintAlpha
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val cornerRadius = context.resources.getDimensionPixelOffset(R.dimen.dp_4)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = parent.getChildAdapterPosition(view).takeIf { it != -1 } ?: return

        when (formAdapter.getFormRowType(adapterPosition)) {
            FormRowType.TOP_ROW -> {
                outRect.set(0, formVerticalMargin, 0, 0)
            }
            FormRowType.BOTTOM_ROW -> {
                outRect.set(0, 0, 0, formVerticalMargin)
            }
            FormRowType.SINGULAR_ROW -> {
                outRect.set(0, formVerticalMargin, 0, formVerticalMargin)
            }
            else -> {
                outRect.set(0, 0, 0, 0)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount) {
            val view: View = parent.getChildAt(i)

            val adapterPosition =
                parent.getChildAdapterPosition(view).takeIf { it != -1 } ?: continue

            when (formAdapter.getFormRowType(adapterPosition)) {
                FormRowType.TOP_ROW -> drawTopRow(c, view)
                FormRowType.MIDDLE_ROW -> drawMiddleRow(c, view)
                FormRowType.BOTTOM_ROW -> drawBottomRow(c, view)
                FormRowType.SINGULAR_ROW -> drawSingularRow(c, view)
                FormRowType.NONE -> {
                }
            }
        }
    }

    private fun drawSingularRow(c: Canvas, view: View) {
        paint.alpha = (view.alpha * borderPaintAlpha).toInt()
        c.drawRoundRect(
            view.left.toFloat(),
            view.top.toFloat(),
            view.right.toFloat(),
            view.bottom.toFloat(),
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            paint
        )
    }

    private fun drawBottomRow(c: Canvas, view: View) {
        paint.alpha = (view.alpha * borderPaintAlpha).toInt()

        val path = roundedRectWithTranslation(
            view,
            roundTopLeftCorner = false,
            roundTopRightCorner = false,
            roundBottomRightCorner = true,
            roundBottomLeftCorner = true
        )
        c.drawPath(path, paint)
    }

    private fun drawMiddleRow(c: Canvas, view: View) {
        paint.alpha = (view.alpha * borderPaintAlpha).toInt()

        val path = Path().apply {
            moveTo(view.left.toFloat(), view.top.toFloat())
            lineTo(view.left.toFloat(), view.bottom.toFloat())
            moveTo(view.right.toFloat(), view.top.toFloat())
            lineTo(view.right.toFloat(), view.bottom.toFloat())
            moveTo(view.left.toFloat(), view.bottom.toFloat())
            lineTo(view.right.toFloat(), view.bottom.toFloat())
        }
        c.drawPath(path, paint)
    }

    private fun drawTopRow(c: Canvas, view: View) {
        paint.alpha = (view.alpha * borderPaintAlpha).toInt()

        val path = roundedRectWithTranslation(
            view,
            roundTopLeftCorner = true,
            roundTopRightCorner = true,
            roundBottomRightCorner = false,
            roundBottomLeftCorner = false
        )
        c.drawPath(path, paint)
    }

    private fun roundedRectWithTranslation(
        view: View,
        roundTopLeftCorner: Boolean,
        roundTopRightCorner: Boolean,
        roundBottomRightCorner: Boolean,
        roundBottomLeftCorner: Boolean
    ): Path {
        return roundedRect(
            left = view.left + view.translationX,            //TODO account for margin?
            top = view.top + view.translationY,
            right = view.right + view.translationX,
            bottom = view.bottom + view.translationY,
            rx = cornerRadius.toFloat(),
            ry = cornerRadius.toFloat(),
            roundTopLeftCorner = roundTopLeftCorner,
            roundTopRightCorner = roundTopRightCorner,
            roundBottomRightCorner = roundBottomRightCorner,
            roundBottomLeftCorner = roundBottomLeftCorner
        )
    }

    private fun roundedRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        rx: Float,
        ry: Float,
        roundTopLeftCorner: Boolean,
        roundTopRightCorner: Boolean,
        roundBottomRightCorner: Boolean,
        roundBottomLeftCorner: Boolean
    ): Path {
        val path = Path()

        val width = right - left
        val height = bottom - top
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        if (roundTopRightCorner) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (roundTopLeftCorner) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (roundBottomLeftCorner) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (roundBottomRightCorner) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -heightMinusCorners)
        path.close() //Given close, last lineto can be removed.
        return path
    }

}

enum class FormRowType {
    TOP_ROW, MIDDLE_ROW, BOTTOM_ROW, SINGULAR_ROW, NONE;

    companion object {
        fun rowTypeFor(formItemCount: Int, itemIndex: Int): FormRowType {
            if (formItemCount == 1) return FormRowType.SINGULAR_ROW

            if (formItemCount == 2) return if (itemIndex == 0) FormRowType.TOP_ROW else FormRowType.BOTTOM_ROW

            return when (itemIndex) {
                0 -> FormRowType.TOP_ROW
                formItemCount - 1 -> FormRowType.BOTTOM_ROW
                else -> FormRowType.MIDDLE_ROW
            }
        }
    }
}


interface FormAdapter {
    fun getFormRowType(adapterPosition: Int): FormRowType
}