package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.RemindMeViewBinding

interface RemindMeListener {
    fun onSetReminderButtonClicked()
    fun onDeleteReminderButtonClicked()
}

enum class RemindMeButtonStatus {
    SET_REMINDER,
    DELETE_REMINDER,
    IS_LOADING
}

class RemindMeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) :
    LinearLayout(context, attrs, R.attr.remindMeView) {

    private val binding = RemindMeViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    fun bind(status: RemindMeButtonStatus, listener: RemindMeListener) = with(binding) {
        setReminderButton.clicks {
            setButtonStatus(RemindMeButtonStatus.IS_LOADING)
            listener.onSetReminderButtonClicked()
        }
        deleteReminderButton.clicks {
            setButtonStatus(RemindMeButtonStatus.IS_LOADING)
            listener.onDeleteReminderButtonClicked()
        }
        setButtonStatus(status)
    }

    private fun setButtonStatus(status: RemindMeButtonStatus) = with(binding) {
        when (status) {
            RemindMeButtonStatus.SET_REMINDER -> {
                setReminderButton.setVisible()
                deleteReminderButton.setGone()
                progressBarContainer.setGone()
            }
            RemindMeButtonStatus.DELETE_REMINDER -> {
                setReminderButton.setGone()
                deleteReminderButton.setVisible()
                progressBarContainer.setGone()
            }
            RemindMeButtonStatus.IS_LOADING -> {
                setReminderButton.setGone()
                deleteReminderButton.setGone()
                progressBarContainer.setVisible()
            }
        }
    }

    //region Layout Stuff
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
    //endregion
}