package com.sohohouse.seven.browsehouses.recycler.snaphelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.sohohouse.seven.R
import kotlin.math.roundToInt

class StartSnapHelper(private val topPadding: Float) : LinearSnapHelper() {

    private lateinit var verticalHelper: OrientationHelper
    private lateinit var horizontalHelper: OrientationHelper

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager is LinearLayoutManager) {
            return getStartView(layoutManager)
        }
        return super.findSnapView(layoutManager)
    }

    private fun getStartView(layoutManager: LinearLayoutManager): View? {
        var firstChild = layoutManager.findFirstCompletelyVisibleItemPosition()

        if (firstChild == RecyclerView.NO_POSITION) {
            firstChild = layoutManager.findFirstVisibleItemPosition()

            if (firstChild == RecyclerView.NO_POSITION) {
                return null
            } else if (firstChild != layoutManager.itemCount - 1) {
                firstChild++
            }
        }

        if (firstChild != layoutManager.itemCount - 1 && layoutManager.findViewByPosition(firstChild)?.id == R.id.region_name) {
            return layoutManager.findViewByPosition(firstChild + 1)
        }

        return layoutManager.findViewByPosition(firstChild)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {

        val out = intArrayOf(0, 0)

        when (layoutManager) {
            is LinearLayoutManager -> {
                if (layoutManager.canScrollHorizontally()) {
                    out[0] =
                        distanceToStart(targetView, getHorizontalOrientationHelper(layoutManager))
                } else {
                    out[1] =
                        distanceToStart(targetView, getVerticalOrientationHelper(layoutManager))
                }
            }
        }

        return out
    }

    private fun getHorizontalOrientationHelper(layoutManager: LinearLayoutManager): OrientationHelper {
        if (!this::horizontalHelper.isInitialized) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper
    }

    private fun getVerticalOrientationHelper(layoutManager: LinearLayoutManager): OrientationHelper {
        if (!this::verticalHelper.isInitialized) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding - topPadding.roundToInt()
    }
}