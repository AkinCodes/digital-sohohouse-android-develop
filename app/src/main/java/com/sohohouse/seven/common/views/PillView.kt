package com.sohohouse.seven.common.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ViewPillBinding

class PillView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttrs) {

    private val horizPadding by lazy { resources.getDimensionPixelOffset(R.dimen.dp_18) }
    private val topPadding by lazy { resources.getDimensionPixelOffset(R.dimen.dp_8) }
    private val bottomPadding by lazy { resources.getDimensionPixelOffset(R.dimen.dp_8) }
    private val binding: ViewPillBinding =
        ViewPillBinding.inflate(LayoutInflater.from(context), this)

    init {
        val ta = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.PillView,
            defStyleAttrs,
            0
        )
        setIcon(ta)
        setLabel(ta)
        isActivated = ta.getBoolean(R.styleable.PillView_activated, false)
        ta.recycle()

        setPadding(horizPadding, topPadding, horizPadding, bottomPadding)
        setBackgroundResource(R.drawable.pill_bg)
    }

    private fun setLabel(typedArray: TypedArray) {
        label = typedArray.getString(R.styleable.PillView_label) ?: ""
    }

    private fun setIcon(ta: TypedArray) {
        val icon = ta.getResourceId(R.styleable.PillView_icon, 0)
        setActionButton(icon)
    }

    var label: String
        get() = binding.pillTv.text.toString()
        set(value) {
            binding.pillTv.text = value
        }

    override fun setEnabled(enabled: Boolean) {
        binding.pillTv.isEnabled = enabled
        super.setEnabled(enabled)
    }

    fun setActionButton(@DrawableRes drawable: Int) = binding.apply {
        if (drawable == 0) {
            pillActionBtn.setGone()
            pillActionBtn.setImageResource(0)
        } else {
            pillActionBtn.setVisible()
            pillActionBtn.setImageResource(drawable)
        }
    }
}