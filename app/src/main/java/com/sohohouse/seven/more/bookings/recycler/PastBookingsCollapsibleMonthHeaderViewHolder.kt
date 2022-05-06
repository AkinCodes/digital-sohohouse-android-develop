package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemPastBookingsCollabsibleMonthBinding

class PastBookingsCollapsibleMonthHeaderViewHolder(
    private val binding: ItemPastBookingsCollabsibleMonthBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: PastBookingsCollapsableMonthItem,
        onClick: (item: PastBookingsCollapsableMonthItem) -> Unit
    ) = with(binding) {
        dateSubHeader.text = item.label

        dropdownArrow.rotation = if (item.collapsed) 0f else 180f

        root.setOnClickListener { onClick(item) }
    }
}