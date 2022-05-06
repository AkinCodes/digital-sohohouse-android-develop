package com.sohohouse.seven.main

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.LoadingView
import eightbitlab.com.blurview.BlurView

class LoadingStateAnimator(
    context: Context,
    loadingView: LoadingView,
    blurView: BlurView,
    val loading: Boolean,
    overlayColor: Int
) {

    interface AnimatorCallback {
        fun onAnimationCompleted()
        fun onAnimationUpdate(color: Int)
    }

    var overlayColor: Int = Color.BLACK

    private val blurColor: Int

    private val animator: AnimatorSet

    private var callback: AnimatorCallback? = null

    init {
        this.overlayColor = overlayColor
        blurColor = TypedValue().apply {
            context.theme.resolveAttribute(
                R.attr.colorBackgroundBlur,
                this,
                true
            )
        }.data

        animator = AnimatorSet().apply {
            duration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            playTogether(
                buildBlurViewAnimator(context, blurView, loading),
                buildLoadingViewAnimator(loadingView, loading)
            )
            addListener(object : Animator.AnimatorListener {

                private var isCancelled: Boolean = false

                override fun onAnimationStart(animatior: Animator?) {
                    loadingView.setVisible()
                }

                override fun onAnimationEnd(animatior: Animator?) {
                    if (!isCancelled) {
                        loadingView.setVisible(loading)
                        callback?.onAnimationCompleted()
                    }
                    callback = null
                }

                override fun onAnimationCancel(animatior: Animator?) {
                    isCancelled = true
                }

                override fun onAnimationRepeat(animatior: Animator?) {}

            })
        }
    }

    private fun buildBlurViewAnimator(
        context: Context,
        blurView: BlurView,
        loading: Boolean
    ): Animator {
        val layerColor = TypedValue().apply {
            context.theme.resolveAttribute(
                R.attr.colorLayer0,
                this,
                true
            )
        }.data
        return ValueAnimator.ofObject(
            ArgbEvaluator(),
            if (loading) blurColor else overlayColor,
            if (loading) layerColor else blurColor
        ).apply {
            this.addUpdateListener {
                overlayColor = it.animatedValue as Int
                blurView.setOverlayColor(overlayColor)
                callback?.onAnimationUpdate(overlayColor)
            }
        }
    }

    private fun buildLoadingViewAnimator(loadingView: LoadingView, loading: Boolean): Animator {
        return ObjectAnimator.ofFloat(
            loadingView,
            "alpha",
            loadingView.alpha,
            if (loading) 1.0f else 0.0f
        )
    }

    fun start(callback: AnimatorCallback? = null) {
        this.callback = callback
        animator.start()
    }

    fun cancel() {
        animator.cancel()
    }
}