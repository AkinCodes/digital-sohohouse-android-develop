package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PeekingLinearLayoutManager : LinearLayoutManager {
    @JvmOverloads
    constructor(
        context: Context?,
        @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
        reverseLayout: Boolean = false
    ) : super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun generateDefaultLayoutParams() =
        scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
        scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
        scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) =
        layoutParams.apply {
            when (orientation) {
                RecyclerView.HORIZONTAL -> width = calculateProportionalWidth()
                RecyclerView.VERTICAL -> height = calculateProportionalHeight()
            }
        }

    fun calculateProportionalHeight() = (verticalSpace * ITEM_WIDTH_RATIO).toInt()

    fun calculateProportionalWidth() = (horizontalSpace * ITEM_WIDTH_RATIO).toInt()

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    companion object {
        const val ITEM_WIDTH_RATIO = 0.85f
    }
}