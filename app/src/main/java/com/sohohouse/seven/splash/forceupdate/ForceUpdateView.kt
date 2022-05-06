package com.sohohouse.seven.splash.forceupdate

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.ViewForceUpdateBinding

open class ForceUpdateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var binding: ViewForceUpdateBinding

    init {
        initLayout(context, attrs)
    }

    private fun initLayout(context: Context, attrs: AttributeSet?) {
        binding = ViewForceUpdateBinding.inflate(LayoutInflater.from(context), this)
        if (isInEditMode) return
        binding.setupAdditionalViews(attrs)
    }

    private fun ViewForceUpdateBinding.setupAdditionalViews(attrs: AttributeSet?) {
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ForceUpdateView)
            setTextToView(a, title, R.styleable.ForceUpdateView_title)
            setTextToView(a, description, R.styleable.ForceUpdateView_description)
            setTextToView(a, primaryButton, R.styleable.ForceUpdateView_primaryButton)
            if (a.hasValue(R.styleable.ForceUpdateView_logoImage)) {
                a.getDrawable(R.styleable.ForceUpdateView_logoImage)?.let {
                    logo.setImageDrawable(it)
                }
            }
            a.recycle()
        }
    }

    private fun setTextToView(typedArray: TypedArray, textView: TextView, index: Int) {
        if (typedArray.hasValue(index)) {
            val string = typedArray.getString(index)
            if (string.isNullOrEmpty()) {
                textView.visibility = View.INVISIBLE
            } else {
                textView.text = string
            }
        }
    }

    fun setOnPrimaryButtonClickListener(function: () -> Unit) {
        binding.primaryButton.clicks { function() }
    }
}