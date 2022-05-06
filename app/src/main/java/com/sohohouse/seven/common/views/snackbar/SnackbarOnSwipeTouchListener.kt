package com.sohohouse.seven.common.views.snackbar

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class SnackbarOnSwipeTouchListener(private val gestureDetector: GestureDetector) :
    View.OnTouchListener {

    @SuppressWarnings("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}