package com.sohohouse.seven.browsehouses.recycler.snaphelper

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getSnapPosition

class SnapOnScrollListener(
    private val snapHelper: StartSnapHelper,
    private var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    private var snapIdlePosition = RecyclerView.NO_POSITION
    private var snapScrollPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        maybeNotifySnapScrollPositionChange(recyclerView)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapScrollPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = snapScrollPosition != snapPosition
        if (snapPositionChanged && snapPosition != RecyclerView.NO_POSITION) {
            onSnapPositionChangeListener?.onSnapScrollPositionChange(snapPosition)
            snapScrollPosition = snapPosition
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = snapIdlePosition != snapPosition
        if (snapPositionChanged && snapPosition != RecyclerView.NO_POSITION) {
            onSnapPositionChangeListener?.onSnapIdlePositionChange(snapPosition)
            snapIdlePosition = snapPosition
        }
    }

}
