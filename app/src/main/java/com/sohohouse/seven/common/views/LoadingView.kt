package com.sohohouse.seven.common.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setVisible

class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        val layout =
            ta.getResourceId(R.styleable.LoadingView_layout, R.layout.component_loading_view)
        ta.recycle()
        initLayout(context, layout)
    }

    private fun initLayout(context: Context, @LayoutRes layout: Int) {
        setVisible(isInEditMode)

        if (background == null) {
            val typeValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorLayer1, typeValue, true)
            setBackgroundColor(typeValue.data)
        }

        LayoutInflater.from(context).inflate(layout, this)
    }

    fun toggleSpinner(visibility: Boolean) {
        if (visibility == (this.visibility == View.VISIBLE)) return

        val animator =
            ObjectAnimator.ofFloat(this, "alpha", this.alpha, if (visibility) 1.0f else 0.0f)
        animator.duration =
            context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        animator.addListener(object : Animator.AnimatorListener {

            var isCancelled: Boolean = false

            override fun onAnimationStart(animatior: Animator?) {
                setVisible()
            }

            override fun onAnimationEnd(animatior: Animator?) {
                if (!isCancelled) setVisible(visibility)
            }

            override fun onAnimationCancel(animatior: Animator?) {
                isCancelled = true
            }

            override fun onAnimationRepeat(animatior: Animator?) {}

        })
        animator.start()
    }

}