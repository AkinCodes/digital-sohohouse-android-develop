package com.sohohouse.seven.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.sohohouse.seven.common.views.ThrottledClickListener

@SuppressLint("CheckResult")
fun View.clicks(onNext: (View) -> Unit) {
    setOnClickListener {
        onNext(it)
    }
}

@Suppress("UNCHECKED_CAST")
fun <V : View> LayoutInflater.inflateAttachAndReturnSelf(layout: Int, parent: ViewGroup): V {
    return (inflate(layout, parent, true) as ViewGroup).getChildAt(parent.childCount - 1) as V
}

fun View.throttleClick(onClicked: (View) -> Unit) {
    setOnClickListener(ThrottledClickListener(onClicked))
}

fun View.expandViewHitAreaBy(
    @Px extra: Int = 0,
    parentView: ViewGroup = (parent as ViewGroup)
) {
    parentView.post {
        val childRect = Rect()
        this.getHitRect(childRect)
        childRect.left -= extra
        childRect.top -= extra
        childRect.right += extra
        childRect.bottom += extra

        parentView.touchDelegate = TouchDelegate(childRect, this)
    }
}

/*
    Delay needed for scrollToPosition to work in some cases
    See https://stackoverflow.com/questions/36426129/recyclerview-scroll-to-position-not-working-every-time
*/
fun RecyclerView.scrollToPositionFixed(position: Int) {
    postDelayed({ scrollToPosition(position) }, 200)
}

fun View.setVisible(visible: Boolean) {
    if (visible) setVisible() else setGone()
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setPaddingTop(paddingTop: Int) {
    setPadding(this.paddingLeft, paddingTop, this.paddingRight, this.paddingBottom)
}

fun View.setPaddingBottom(paddingBottom: Int) {
    setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, paddingBottom)
}

fun View.getAttributeColor(@AttrRes resId: Int): Int {
    return context.getAttributeColor(resId)
}

fun View.showAnimated() {
    if (isVisible && alpha == 1.0f) return

    animate().cancel()
    animate()
        .alpha(1.0f)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                setVisible()
            }
        })
        .start()
}

fun View.hideAnimated() {
    if (isInvisible) return

    animate().cancel()
    animate().alpha(0.0f)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                setInvisible()
            }

            override fun onAnimationStart(animation: Animator?) {
                setVisible()
            }
        })
        .start()
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun animate(
    transition: Transition = AutoTransition(),
    parent: ViewGroup,
    vararg viewIds: Int,
    actions: () -> Unit,
) {
    transition.apply {
        duration = 200
        viewIds.forEach { addTarget(it) }
    }.also {
        TransitionManager.beginDelayedTransition(parent, it)
    }
    actions()
}

val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

fun View.layoutInflater() = LayoutInflater.from(context)

fun View.setEnabledWithAlpha(enable: Boolean, changeAlpha: Boolean) {
    val alphaDisabled = 0.5F
    isEnabled = enable
    if (changeAlpha) {
        alpha = if (enable) 1F else alphaDisabled
    }
}
