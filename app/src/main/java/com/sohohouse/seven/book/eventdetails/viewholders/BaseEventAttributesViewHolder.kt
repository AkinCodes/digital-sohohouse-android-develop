package com.sohohouse.seven.book.eventdetails.viewholders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.sohohouse.seven.common.extensions.setVisible

interface BaseEventAttributesViewHolder {

    val label: TextView

    val description: TextView

    fun setIcon(drawable: Drawable?, tintColor: Int, padding: Int) {
        drawable?.setTint(tintColor)
        label.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        label.compoundDrawablePadding = padding
    }

    fun setLabel(stringRes: Int?) {
        if (stringRes == null) return

        label.setText(stringRes)
    }

    fun setAlternateLabelText(text: String?) {
        label.setVisible(!text.isNullOrEmpty())
        label.text = text
    }

    fun setDescription(text: String) {
        description.text = text
        description.visibility = View.VISIBLE
    }
}
