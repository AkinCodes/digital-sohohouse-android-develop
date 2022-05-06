package com.sohohouse.seven.book.eventdetails.viewholders

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.eventdetaillist.EventMembershipAdapterItem
import com.sohohouse.seven.databinding.EventDetailsIconAttributeNameLayoutBinding
import com.sohohouse.seven.membership.ActiveMembershipInfoActivity

class MembershipViewHolder(
    private val binding: EventDetailsIconAttributeNameLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("DefaultLocale")
    fun bind(item: EventMembershipAdapterItem) {
        binding.label.text = getString(item.labelStringRes).toLowerCase().capitalize()

        if (EventType.get(item.eventType ?: "") != EventType.FITNESS_EVENT) return

        if (item.hasLink) {
            with(binding.infoIcon) {
                visibility = View.VISIBLE
                setOnClickListener {
                    it.context.startActivity(
                        ActiveMembershipInfoActivity.getIntent(
                            it.context,
                            item.eventId, item.eventName, item.eventType
                        )
                    )
                }
            }
        }
    }
}