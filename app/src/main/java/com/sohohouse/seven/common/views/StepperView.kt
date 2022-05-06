package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ComponentStepperBinding

interface JoinEventListener {
    fun joinEvent(tickets: Int)
    fun onMoreTicketsClick()
    fun onLessTicketsClick()
}

class StepperView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) :
    FrameLayout(context, attrs, R.attr.stepperViewStyle) {

    private val binding = ComponentStepperBinding
        .inflate(LayoutInflater.from(context), this)

    private var initVal: Int = 0
    private var max: Int = 0
    private var count: Int = 0
        set(value) {
            field = value
            val enableSubmitBtn = value > 0
            binding.stepperSubmitButtonLarge.isEnabled = enableSubmitBtn
            binding.stepperSubmitButtonSmall.isEnabled = enableSubmitBtn
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        outlineProvider = CustomOutline(w, h)
    }

    private inner class CustomOutline internal constructor(
        internal var width: Int,
        internal var height: Int
    ) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRect(0, 0, width, height)
        }
    }

    fun setUp(ctaRes: Int, listener: JoinEventListener) = with(binding) {
        stepperContainer.setGone()
        stepperSubmitButtonLarge.setVisible()
        stepperSubmitButtonSmall.setGone()
        stepperSubmitButtonLarge.text = context.getText(ctaRes)
        stepperSubmitButtonLarge.clicks {
            listener.joinEvent(1)
        }
        elevation = 0f
    }

    fun setUp(initVal: Int, max: Int, ctaRes: Int, listener: JoinEventListener) = with(binding){
        stepperContainer.visibility = View.VISIBLE
        stepperSubmitButtonLarge.visibility = View.GONE
        stepperSubmitButtonSmall.text = context.getText(ctaRes)
        stepperMinusButton.setOnClickListener { onMinusClick() }
        stepperPlusButton.setOnClickListener { onPlusClick() }
        this@StepperView.initVal = initVal
        this@StepperView.max = max
        this@StepperView.count = initVal
        stepperQuantity.minEms = max.toString().length
        updateQty()
        stepperSubmitButtonSmall.clicks {
            listener.joinEvent(count)
        }
        stepperText.text = resources.getString(R.string.how_many_tickets)
        elevation = resources.getDimensionPixelOffset(R.dimen.dp_8).toFloat()
    }

    private fun updateQty() {
        binding.stepperQuantity.text = count.toString()
    }

    private fun onMinusClick() {
        if (this.count > this.initVal) {
            this.count--
            updateQty()
        }
    }

    private fun onPlusClick() {
        if (this.count < this.max) {
            this.count++
            updateQty()
        }
    }
}