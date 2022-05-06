package com.sohohouse.seven.common.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

class PullToRefreshBehavior(context: Context, attrs: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    var topOffset: Int = 0
        private set

    @IdRes
    var dependencyId: Int = 0

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == dependencyId
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        topOffset = dependency.height
        child.translationY = topOffset.toFloat()
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val translationY = child.translationY.toInt()
        val newTranslationY = translationY - dy

        if (dy > 0) { // Scroll up
            if (translationY == 0) return

            if (newTranslationY < 0) {
                consumed[1] = translationY
                child.translationY = 0f
            } else if (newTranslationY > 0) {
                consumed[1] = dy
                child.translationY = newTranslationY.toFloat()
            }
        } else if (dy < 0) { // Scroll down
            if (translationY == topOffset) return

            if (!target.canScrollVertically(-1)) {
                if (newTranslationY <= topOffset) {
                    consumed[1] = dy
                    child.translationY = newTranslationY.toFloat()
                } else if (newTranslationY > topOffset) {
                    consumed[1] = translationY - topOffset
                    child.translationY = topOffset.toFloat()
                }
            }
        }
    }

}