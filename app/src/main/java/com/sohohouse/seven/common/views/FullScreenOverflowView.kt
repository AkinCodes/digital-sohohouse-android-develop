package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ComponentOverflowViewBinding

class FullScreenOverflowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ComponentOverflowViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    fun showOverflowView(vararg buttons: Pair<String, OnClickListener>) = with(binding) {
        for (buttonPair in buttons) {
            val layoutRes = R.layout.component_overflow_button

            val buttonView =
                LayoutInflater.from(context).inflate(layoutRes, overflowContainer, false) as Button
            buttonView.text = buttonPair.first
            buttonView.clicks {
                hideOverflowView()
                buttonPair.second.onClick(buttonView)
            }
            overflowContainer.addView(buttonView)
        }
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        overflowContainer.setVisible()
        overflowContainer.startAnimation(animation)
    }

    fun isVisible(): Boolean {
        return binding.overflowContainer.visibility == View.VISIBLE
    }

    fun hideOverflowView() = with(binding) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                overflowContainer.removeAllViews()
                overflowContainer.setGone()
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        overflowContainer.startAnimation(animation)
    }
}