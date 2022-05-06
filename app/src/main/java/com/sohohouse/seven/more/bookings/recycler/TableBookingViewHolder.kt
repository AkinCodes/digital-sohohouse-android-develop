package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getColor
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemTableBookingBinding

class TableBookingViewHolder(
    private val binding: ItemTableBookingBinding,
    private val lightTheme: Boolean = false
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TableBookingAdapterItem, onClick: (item: TableBookingAdapterItem) -> Unit) =
        with(binding) {
            root.setOnClickListener { onClick(item) }
            with(itemContent) {
                eventImage.setImageFromUrl(item.imageUrl)
                eventTitleLabel.text = item.venueName
                eventDateAndTimeLabel.text = item.dateTime
                eventLocationName.text = item.address

                if (lightTheme) {
                    eventLocationName.setTextColor(getColor(R.color.white56))
                    eventDateAndTimeLabel.setTextColor(getColor(R.color.white56))
                    eventTitleLabel.setTextColor(getColor(R.color.white))
                }
            }
        }
}