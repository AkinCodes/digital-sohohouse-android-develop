package com.sohohouse.seven.common.views.eventdetaillist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.databinding.GuestItemViewBinding

interface DeleteGuestListener {
    fun deleteGuest(newGuestCount: Int)
}

class GuestListAdapter(
    private val guestCount: Int,
    private val deleteGuestListener: DeleteGuestListener?
) : RecyclerView.Adapter<GuestListAdapter.GuestItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestItemViewHolder {

        return GuestItemViewHolder(
            GuestItemViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return guestCount
    }

    override fun onBindViewHolder(holder: GuestItemViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class GuestItemViewHolder(private val binding: GuestItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            guestNameText.text =
                getString(R.string.explore_events_event_your_guest_number_label).replaceBraces((position + 1).toString())
            guestDeleteButton.visibility =
                if (position == guestCount - 1 && deleteGuestListener != null) {
                    guestDeleteButton.clicks {
                        deleteGuestListener.deleteGuest(guestCount - 1)
                    }
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }
}