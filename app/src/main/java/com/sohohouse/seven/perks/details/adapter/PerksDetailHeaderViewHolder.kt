package com.sohohouse.seven.perks.details.adapter

import android.text.format.DateUtils
import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.PerksDetailHeaderCardLayoutBinding
import java.util.*

class PerksDetailHeaderViewHolder(private val binding: PerksDetailHeaderCardLayoutBinding) :
    ViewHolder<PerksDetailHeader>(binding.root) {

    override fun bind(item: PerksDetailHeader) {
        val cityTagText = item.cityTag(context)
        with(binding) {
            cityTag.text = cityTagText
            cityTag.setVisible(!cityTagText.isNullOrEmpty())

            headerTitle.text = item.title

            headerLine.setVisible(item.headerLine?.isNotEmpty() == true)
            headerLine.setLinkableHtml(item.headerLine)

            item.expiresOn?.let { date ->
                if (item.datePlaceholder == -1) {
                    headerDate.text = formatExpiryDate(date)
                } else {
                    headerDate.text =
                        getString(item.datePlaceholder).replaceBraces(formatExpiryDate(date))
                }
            }
            headerDate.setVisible(item.expiresOn != null)
        }
    }

    private fun formatExpiryDate(date: Date): String {
        return DateUtils.formatDateTime(
            context, date.time,
            DateUtils.FORMAT_SHOW_YEAR or
                    DateUtils.FORMAT_ABBREV_MONTH or
                    DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_ABBREV_WEEKDAY or
                    DateUtils.FORMAT_SHOW_WEEKDAY
        )
    }

}