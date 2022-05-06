package com.sohohouse.seven.common.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ComponentStatusBinding
import java.util.*

class StatusView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(con, attrs, defStyleAttr) {

    private val binding = ComponentStatusBinding
        .inflate(LayoutInflater.from(con), this, true)

    fun setupLayout(status: EventStatusType, openDate: String? = null) = with(binding) {
        var resString: String = context.resources.getString(status.resString)
        if (status == EventStatusType.OPEN_SOON && openDate != null) {
            resString = resString.replaceBraces(openDate.uppercase(Locale.getDefault()))
        }
        componentEventStatusCircle.isVisible = status.showDot

        if (status == EventStatusType.OPEN_FOR_BOOKING) {
            componentEventStatusInnerCircle.setVisible()
            componentEventStatusInnerCircle.backgroundTintList =
                ColorStateList.valueOf(getAttributeColor(status.attrSecondColor))
        } else
            componentEventStatusInnerCircle.setGone()

        componentEventStatusCircle.backgroundTintList =
            ColorStateList.valueOf(getAttributeColor(status.attrColor))

        val text = "$resString "
        componentEventStatusText.text =
            text // fonts that are italicized by Android itself will be cut off in wrap_content
        componentEventStatusText.setTextColor(getAttributeColor(status.attrColor))
    }

    fun setupLayout(isOpen: Boolean, postsToday: Int) = with(binding) {
        val color =
            ContextCompat.getColor(context, if (isOpen) R.color.celery else R.color.charcoal)
        componentEventStatusCircle.backgroundTintList = ColorStateList.valueOf(color)
        var text = if (isOpen) {
            if (postsToday == 1) context.getString(R.string.connect_change_open_single_label)
                .replaceBraces(postsToday.toString())
            else context.getString(R.string.connect_change_open_label)
                .replaceBraces(postsToday.toString())
        } else {
            context.getString(R.string.connect_change_closed_label)
        }

        text = "$text "
        componentEventStatusText.text = text
        componentEventStatusText.setTextColor(color)
    }
}
