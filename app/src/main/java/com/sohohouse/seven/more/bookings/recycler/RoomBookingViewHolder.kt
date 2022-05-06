package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getColor
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemBookingListItemBinding
import com.sohohouse.seven.databinding.ListViewEventCardSmallBinding

class RoomBookingViewHolder(
    private val vBinding: ViewBinding,
    private val lightTheme: Boolean = false
) : RecyclerView.ViewHolder(
    vBinding.root
) {

    val binding = when (vBinding) {
        is ListViewEventCardSmallBinding -> vBinding
        is ItemBookingListItemBinding -> vBinding.itemContent
        else -> null
    }

    fun bind(item: RoomBookingAdapterItem, onClick: (item: RoomBookingAdapterItem) -> Unit) =
        binding?.apply {

            root.setOnClickListener { onClick(item) }

            eventLocationName.text = item.roomName
            eventTitleLabel.text = item.hotelName

            eventDateAndTimeLabel.text = item.dateAndTime
            eventBookingStatus.text = item.statusLabel
            eventImage.setImageFromUrl(item.imageUrl)

            if (lightTheme) {
                eventLocationName.setTextColor(getColor(R.color.white56))
                eventDateAndTimeLabel.setTextColor(getColor(R.color.white56))
                eventTitleLabel.setTextColor(getColor(R.color.white))
            }
        }

}