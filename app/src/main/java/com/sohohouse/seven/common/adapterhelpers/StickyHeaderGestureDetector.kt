package com.sohohouse.seven.common.adapterhelpers

import android.view.GestureDetector
import android.view.MotionEvent

class StickyHeaderGestureDetector(private val touchHelper: StickyHeaderTouchHelper) :
    GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return touchHelper.onSingleTap(event)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return true
    }
}