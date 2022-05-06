package com.sohohouse.seven.home.houseboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sohohouse.seven.R


class SwipeCallback<T>(
    context: Context,
    private val helper: SwipeHelper<T>,
    private val leftSwipe: (T, Int) -> Unit,
    private val rightSwipe: (T, Int) -> Unit,
    private val leftSwipeText: String,
    private val rightSwipeText: String
) : ItemTouchHelper.Callback() {

    interface SwipeHelper<T> {
        fun canSwipe(position: Int): Boolean
        fun getItem(position: Int): T?
    }

    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_add)!!
    private val background =
        ContextCompat.getDrawable(context, R.drawable.notification_swipe_background)!!
    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.faro_lucky_regular)
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            12f,
            context.resources.displayMetrics
        )
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        if (helper.canSwipe(viewHolder.adapterPosition)) {
            return makeMovementFlags(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.START or ItemTouchHelper.END
            )
        }
        return makeMovementFlags(0, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val notification = helper.getItem(viewHolder.adapterPosition) ?: return
        when (direction) {
            ItemTouchHelper.RIGHT -> rightSwipe(notification, viewHolder.adapterPosition)
            ItemTouchHelper.LEFT -> leftSwipe(notification, viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder.adapterPosition == -1) return

        val itemView = viewHolder.itemView
        val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

        if (dX > 0) {
            background.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            deleteIcon.setBounds(
                itemView.left + iconMarginVertical,
                itemView.top + iconMarginVertical,
                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                itemView.bottom - iconMarginVertical
            )
            background.draw(canvas)
        } else {
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            deleteIcon.setBounds(
                itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                itemView.top + iconMarginVertical,
                itemView.right - iconMarginVertical,
                itemView.bottom - iconMarginVertical
            )
            deleteIcon.level = 0
            background.draw(canvas)
        }

        val text = if (dX > 0) rightSwipeText else leftSwipeText
        val bounds = if (dX > 0) background.bounds else background.bounds

        canvas.save()
        if (dX > 0)
            canvas.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        else
            canvas.clipRect(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
        canvas.drawText(
            text,
            0,
            text.length,
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            paint
        )
        canvas.restore()

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}