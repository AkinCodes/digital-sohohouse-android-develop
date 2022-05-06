package com.sohohouse.seven.book.eventdetails.viewholders

import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.DEFAULT_BOLD
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.CustomTFSpan
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.databinding.EventDetailsSubDescriptionLayoutBinding

const val SUB_DESCRIPTION_LAYOUT = R.layout.event_details_sub_description_layout

class SubDescriptionAttributeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = EventDetailsSubDescriptionLayoutBinding.bind(view)

    fun bind(attrStringRes: Int, value: String, lastItem: Boolean) {
        val attrString = getString(attrStringRes)
        val restString = attrString.replaceBraces(value)
        val attrTypeface: Typeface = ResourcesCompat.getFont(context, R.font.faro_lucky_regular)
            ?: DEFAULT_BOLD
        val valueTypeface: Typeface = ResourcesCompat.getFont(context, R.font.faro_lucky_regular)
            ?: DEFAULT
        val middle = restString.length - (value.length)

        val sb = SpannableStringBuilder(restString)
        sb.setSpan(CustomTFSpan(attrTypeface), 0, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(
            CustomTFSpan(valueTypeface),
            middle,
            restString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.subDescription.text = sb

        binding.subDescriptionBottomSpacing.isVisible = lastItem
    }
}