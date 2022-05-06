package com.sohohouse.seven.guests.list

import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.databinding.ViewHolderGuestInvitationListItemBinding

class GuestInvitationViewHolder(private val binding: ViewHolderGuestInvitationListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GuestListItem.GuestInvitationItem) = with(binding) {
        guestlistImg.contentDescription = item.location
        guestlistImg.setImageUrl(item.imageUrl)
        guestlistTitle.text = item.title
        guestlistDate.text =
            item.date?.let { DateUtils.formatDateTime(itemView.context, it.time, DATE_FORMAT) }
        guestlistAddress.text = item.address
        item.status?.stringRes?.let { guestlistStatus.setText(it) }
            ?: run { guestlistStatus.text = null }
        guestlistStatus.setTextColor(
            guestlistStatus.getAttributeColor(
                item.status?.color ?: R.attr.colorInvitation
            )
        )
    }


    companion object {
        private const val DATE_FORMAT = DateUtils.FORMAT_SHOW_WEEKDAY or
                DateUtils.FORMAT_SHOW_DATE or
                DateUtils.FORMAT_NO_YEAR or
                DateUtils.FORMAT_ABBREV_WEEKDAY
    }
}

class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class NewInvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ListHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
