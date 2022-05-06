package com.sohohouse.seven.common.views.snackbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.sohohouse.seven.R

class Snackbar(private val parent: ViewGroup, useAsRoot: Boolean) {

    abstract class Callback {
        open fun onDismissed(toast: Snackbar) {
        }

        open fun onShown(toast: Snackbar) {
        }
    }

    private val contentView: View =
        LayoutInflater.from(parent.context).inflate(R.layout.snackbar, parent, false)

    private val title: SnackbarTextView?

    private var duration: Int = LENGTH_SHORT

    private var callback: Callback? = null

    private var gravity: Int = Gravity.TOP

    init {
        title = contentView.findViewById(R.id.title)

        if (!useAsRoot) {
            contentView.fitsSystemWindows = true
            ViewCompat.setOnApplyWindowInsetsListener(contentView) { _, insets ->
                contentView.setPadding(
                    contentView.paddingLeft, contentView.paddingTop,
                    contentView.paddingRight, insets.systemWindowInsetBottom
                )
                insets
            }
        }
    }

    fun setTitle(text: String?) {
        title?.run {
            if (TextUtils.isEmpty(text)) {
                visibility = View.GONE
            } else {
                setText(text)
            }
        }
    }

    fun setSnackbarState(state: SnackbarState) {
        title?.setSnackbarState(state)
    }

    fun setDuration(@Duration duration: Int) {
        this.duration = duration
    }

    fun setCallback(callback: Callback?): Snackbar {
        this.callback = callback
        return this
    }

    fun enableSwipeToDismiss(enable: Boolean) {
        if (enable) {
            val gestureDetector =
                SnackbarOnGestureListener(object : SnackbarOnGestureListener.OnSwipeListener() {
                    override fun onSwipeUp() {
                        dismiss()
                    }
                })
            this.contentView.setOnTouchListener(
                SnackbarOnSwipeTouchListener(
                    GestureDetector(
                        contentView.context,
                        gestureDetector
                    )
                )
            )
        } else {
            this.contentView.setOnTouchListener(null)
        }
    }

    fun show() {
        contentView.alpha = 0f
        parent.addView(contentView)

        val layoutParams = contentView.layoutParams
        when (layoutParams) {
            is FrameLayout.LayoutParams -> layoutParams.gravity = gravity
            is CoordinatorLayout.LayoutParams -> layoutParams.gravity = gravity
            is ConstraintLayout.LayoutParams -> layoutParams.bottomToBottom = PARENT_ID
        }
        contentView.layoutParams = layoutParams

        contentView.post {
            createAnimator(true).start()
        }
    }

    fun dismiss() {
        createAnimator(false).start()
    }

    private fun createAnimator(show: Boolean): Animator {
        val alphaAnimator = ObjectAnimator.ofFloat(
            contentView, "alpha",
            if (show) 0f else 1f,
            if (show) 1f else 0f
        )
        alphaAnimator.duration = ANIMATION_DURATION

        val height = contentView.measuredHeight.toFloat()
        val translationAnimator = ObjectAnimator.ofFloat(
            contentView, "translationY",
            if (show) {
                if (gravity == Gravity.TOP) -height else height
            } else {
                0f
            },
            if (show) {
                0f
            } else {
                if (gravity == Gravity.TOP) -height else height
            }
        )

        val set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (show) {
                    if (duration != LENGTH_INDEFINITE) {
                        contentView.postDelayed(
                            { dismiss() },
                            when (duration) {
                                LENGTH_EXTRA_LONG -> EXTRA_LONG_DURATION_MS
                                LENGTH_LONG -> LONG_DURATION_MS
                                else -> SHORT_DURATION_MS
                            }
                        )
                    }
                    callback?.onShown(this@Snackbar)
                } else {
                    parent.removeView(contentView)
                    callback?.onDismissed(this@Snackbar)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        set.playTogether(alphaAnimator, translationAnimator)
        set.duration =
            contentView.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        return set
    }

    companion object {
        const val LENGTH_SHORT = 0
        const val LENGTH_LONG = 1
        const val LENGTH_EXTRA_LONG = 2
        const val LENGTH_INDEFINITE = 3

        private const val SHORT_DURATION_MS = 1500L
        private const val LONG_DURATION_MS = 2750L
        private const val EXTRA_LONG_DURATION_MS = 10000L
        private const val ANIMATION_DURATION = 250L

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(LENGTH_SHORT, LENGTH_LONG, LENGTH_EXTRA_LONG, LENGTH_INDEFINITE)
        annotation class Duration
    }

    class Builder {

        private val parent: View

        private var title: String? = null

        private var duration: Int = LENGTH_SHORT

        private var useAsRoot: Boolean = false

        private var snackbarState: SnackbarState = SnackbarState.DEFAULT

        private var gravity: Int = Gravity.TOP

        constructor(view: View, useAsRoot: Boolean = false) {
            parent = view
            this.useAsRoot = useAsRoot
        }

        constructor(activity: Activity) {
            parent = activity.window.decorView.findViewById<View>(android.R.id.content)
            this.useAsRoot = true
        }

        fun setTitle(@StringRes resId: Int): Builder {
            return setTitle(parent.context.getString(resId))
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setDuration(@Duration duration: Int): Builder {
            this.duration = duration
            return this
        }

        fun setState(state: SnackbarState): Builder {
            this.snackbarState = state
            return this
        }

        fun setGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        fun build(): Snackbar? {
            findParent(parent)?.let {
                val snackbar = Snackbar(it, useAsRoot)

                if (!TextUtils.isEmpty(title)) {
                    snackbar.setTitle(title)
                }

                snackbar.setSnackbarState(snackbarState)
                snackbar.duration = duration
                snackbar.gravity = gravity
                return snackbar
            }
            return null
        }


        private fun findParent(v: View?): ViewGroup? {
            if (useAsRoot) {
                return v as ViewGroup?
            }

            var fallback: ViewGroup? = null
            var view: View? = v

            do {
                if (view is CoordinatorLayout) {
                    return view
                } else if (view is FrameLayout) {
                    if (view.getId() == android.R.id.content) {
                        return view
                    } else {
                        fallback = view
                    }
                }

                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }

            } while (view != null)
            return fallback
        }
    }
}