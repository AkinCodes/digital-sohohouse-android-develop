package com.sohohouse.seven.connect.message.chat.content

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.WarningViewBinding

class WarningView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttrs) {

    private val binding = WarningViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val attrs = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.WarningView,
            defStyleAttrs,
            0
        )

        val description = attrs.getString(R.styleable.WarningView_description)
        val buttonText = attrs.getString(R.styleable.WarningView_buttonText)

        binding.warningButtonConfirm.text = buttonText
        binding.warningDescription.text = description

        attrs.recycle()
    }

    fun onButtonClick(callback: (View) -> Unit) {
        binding.warningButtonConfirm.setOnClickListener(callback)
    }

    fun setDescription(text: String) {
        binding.warningDescription.text = text
    }

    fun setTitle(text: String) {
        binding.warningTitle.text = text
    }
}