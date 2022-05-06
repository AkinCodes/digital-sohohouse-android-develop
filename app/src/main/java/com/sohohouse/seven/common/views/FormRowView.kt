package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ItemFormRowBinding

class FormRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {

    private val binding = ItemFormRowBinding
        .inflate(LayoutInflater.from(context), this)

    var label: String? = null
        set(value) {
            field = value
            binding.labelTvUnfilled.text = value
            binding.labelTvFilled.text = value
        }

    var value: String? = null
        set(value) {
            field = value
            binding.valueTv.text = value
            showFilledState(value.isNullOrEmpty().not())
        }

    private val horizontalPadding: Int
        get() = resources.getDimensionPixelOffset(R.dimen.dp_16)

    private val verticalPadding: Int
        get() = resources.getDimensionPixelOffset(R.dimen.dp_12)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FormRowView, defStyleAttrs, 0)
        val label = ta.getString(R.styleable.FormRowView_label) ?: ""
        ta.recycle()

        this.label = label
        this.value = null

        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(
                context.resources.getDimensionPixelOffset(R.dimen.form_row_height),
                MeasureSpec.EXACTLY
            )
        )
    }

    private fun showFilledState(filled: Boolean) = with(binding) {
        labelTvUnfilled.setVisible(!filled)
        labelTvFilled.setVisible(filled)
        valueTv.setVisible(filled)
    }
}