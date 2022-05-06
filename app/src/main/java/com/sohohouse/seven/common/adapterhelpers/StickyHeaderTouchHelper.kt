package com.sohohouse.seven.common.adapterhelpers

import android.view.MotionEvent
import android.view.View

interface StickyHeaderTouchHelper {

    fun findHeaderViewUnder(x: Float, y: Float): View?

    fun onSingleTap(event: MotionEvent): Boolean

}