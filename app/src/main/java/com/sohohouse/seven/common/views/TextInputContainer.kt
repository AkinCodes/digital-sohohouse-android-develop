package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.sohohouse.seven.R


class TextInputContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    var isChecked: Boolean = false
        set(value) {
            field = value
            refreshDrawableState()
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        refreshDivider()
        return super.onCreateDrawableState(extraSpace + 1).apply {
            if (isChecked) View.mergeDrawableStates(this, CHECKED_STATE_SET)
        }
    }

    private fun refreshDivider() {
        dividerDrawable = if (isChecked) {
            ContextCompat.getDrawable(context, R.drawable.sign_in_divider_error)
        } else {
            ContextCompat.getDrawable(context, R.drawable.sign_in_divider_normal)
        }
    }
}