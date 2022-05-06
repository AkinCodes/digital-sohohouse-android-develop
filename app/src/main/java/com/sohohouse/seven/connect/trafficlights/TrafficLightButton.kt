package com.sohohouse.seven.connect.trafficlights

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.TrafficLightButtonBinding

class TrafficLightButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding = TrafficLightButtonBinding.inflate(LayoutInflater.from(context), this)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TrafficLightButton)

        binding.run {
            title.text = a.getString(R.styleable.TrafficLightButton_trafficTitle)
            subtitle.text = a.getString(R.styleable.TrafficLightButton_trafficSubtitle)
            status.backgroundTintList = ColorStateList.valueOf(
                a.getColor(R.styleable.TrafficLightButton_statusTint, Color.TRANSPARENT)
            )
            checkMark.isVisible = isSelected
        }
        a.recycle()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        binding.checkMark.isVisible = selected
    }

}