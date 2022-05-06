package com.sohohouse.seven.book.eventdetails.viewholders

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventGuestListAdapterItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.views.eventdetaillist.GuestListAdapter
import com.sohohouse.seven.databinding.EventDetailsGuestListLayoutBinding

class GuestRecyclerviewViewHolder(
    private val binding: EventDetailsGuestListLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(guestItem: EventGuestListAdapterItem) {
        binding.guestListRecyclerview.adapter =
            GuestListAdapter(guestItem.guestNum, guestItem.deleteGuestListener)
        binding.guestListShareButton.clicks {
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT, getString(R.string.explore_events_event_share_label)
                        .replaceBraces(
                            guestItem.eventName,
                            guestItem.venueName,
                            guestItem.startDate?.getFormattedDateTime(guestItem.timeZone) ?: ""
                        )
                )
            }
            startActivity(
                it.context,
                Intent.createChooser(
                    sharingIntent,
                    getString(R.string.explore_events_event_share_title)
                ),
                null
            )
        }
    }
}