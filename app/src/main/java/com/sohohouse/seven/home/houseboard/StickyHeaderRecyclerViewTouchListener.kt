package com.sohohouse.seven.home.houseboard

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderRecyclerViewTouchListener(private val tapDetector: GestureDetector) :
    RecyclerView.OnItemTouchListener {

    override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
    }

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
        if (tapDetector.onTouchEvent(event)) return true
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

}