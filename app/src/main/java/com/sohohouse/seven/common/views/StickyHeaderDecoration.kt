package com.sohohouse.seven.common.views

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.StickyHeaderAdapter


open class StickyHeaderDecoration<T : RecyclerView.ViewHolder>(
    private val adapter: StickyHeaderAdapter<T>
) : RecyclerView.ItemDecoration() {

    protected val headerCache: MutableMap<Int, RecyclerView.ViewHolder> = hashMapOf()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0

        if (position != RecyclerView.NO_POSITION && hasHeader(position) && showHeaderAboveItem(
                position
            )
        ) {
            val header = getHeader(parent, position).itemView
            headerHeight = header.height
        }

        outRect.set(0, headerHeight, 0, 0)
    }

    protected fun showHeaderAboveItem(itemAdapterPosition: Int): Boolean {
        return if (itemAdapterPosition == 0) {
            true
        } else adapter.getHeaderId(itemAdapterPosition - 1) != adapter.getHeaderId(
            itemAdapterPosition
        )
    }

    protected fun hasHeader(position: Int): Boolean {
        return adapter.getHeaderId(position) != StickyHeaderAdapter.NO_ID
    }

    protected fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        val key = adapter.getHeaderId(position)

        if (headerCache.containsKey(key)) {
            return headerCache[key]!!
        } else {
            val holder = adapter.onCreateHeaderViewHolder(parent)
            val header = holder.itemView


            adapter.onBindHeaderViewHolder(holder, position)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredWidth,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight,
                View.MeasureSpec.UNSPECIFIED
            )

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight,
                header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                header.layoutParams.height
            )

            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)

            headerCache[key] = holder

            return holder
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.childCount
        var previousHeaderId: Int = -1

        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)
            val adapterPos = parent.getChildAdapterPosition(child)

            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId = adapter.getHeaderId(adapterPos)

                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId
                    val header = getHeader(parent, adapterPos).itemView
                    canvas.save()

                    val left = child.left
                    val top = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                    canvas.translate(left.toFloat(), top.toFloat())

                    header.translationX = left.toFloat()
                    header.translationY = top.toFloat()
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderTop(
        parent: RecyclerView,
        child: View,
        header: View,
        adapterPos: Int,
        layoutPos: Int
    ): Int {
        val headerHeight = header.height
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count = parent.childCount
            val currentId = adapter.getHeaderId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId = adapter.getHeaderId(adapterPosHere)
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        val offset = next.y.toInt() - (headerHeight + getHeader(
                            parent,
                            adapterPosHere
                        ).itemView.height)
                        return if (offset < 0) {
                            offset
                        } else {
                            break
                        }
                    }
                }
            }

            top = Math.max(0, top)
        }

        return top
    }

}