package com.sohohouse.seven.common.views.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.databinding.CustomButtonWithNumberIndicatorBinding

class CustomButtonWithNumberIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val binding =
        CustomButtonWithNumberIndicatorBinding.inflate(LayoutInflater.from(context), this, true)

    var text: String = ""
        set(value) {
            field = value
            binding.text.text = value
        }

    fun setNumber(num: Int) {
        binding.itemCount.text = if (num == 0) "" else num.toString()
    }

}