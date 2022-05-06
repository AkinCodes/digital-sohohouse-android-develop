package com.sohohouse.seven.common.views.snackbar

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class SnackbarOnGestureListener(private val listener: OnSwipeListener? = null) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val result = false
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        listener?.onSwipeRight()
                    } else {
                        listener?.onSwipeLeft()
                    }
                }
            } else {
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        listener?.onSwipeDown()
                    } else {
                        listener?.onSwipeUp()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return result
    }

    open class OnSwipeListener {
        open fun onSwipeLeft() {}

        open fun onSwipeRight() {}

        open fun onSwipeUp() {}

        open fun onSwipeDown() {}
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}