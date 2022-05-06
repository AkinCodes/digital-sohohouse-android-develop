package com.sohohouse.seven.common.views.eventdetaillist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventGuestListAdapterItem
import com.sohohouse.seven.book.eventdetails.viewholders.AttributeWClickableTextViewHolder
import com.sohohouse.seven.book.eventdetails.viewholders.GuestRecyclerviewViewHolder
import com.sohohouse.seven.databinding.EventDetailsAttributeLayoutBinding
import com.sohohouse.seven.databinding.EventDetailsGuestListLayoutBinding

abstract class BaseEventDetailsAdapter(protected var data: List<BaseEventDetailsAdapterItem> = listOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EventDetailsAdapterItemType.values()[viewType]) {
            EventDetailsAdapterItemType.ATTRIBUTE -> {
                val binding = EventDetailsAttributeLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                AttributeWClickableTextViewHolder(binding)
            }
            EventDetailsAdapterItemType.GUEST_LIST -> {
                val binding = EventDetailsGuestListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                GuestRecyclerviewViewHolder(binding)
            }
            else -> throw IllegalStateException("unknown EventDetailsAdapterItemType, should have been handled by concrete adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is EventAttributeAdapterItem -> {
                val viewHolder = holder as AttributeWClickableTextViewHolder
                viewHolder.bind(item)

                //for last item, show extra bottom padding
                if (position == data.size - 1) {
                    viewHolder.setExtraBottomPadding()
                }
            }
            is EventGuestListAdapterItem -> (holder as GuestRecyclerviewViewHolder).bind(item)
            else -> throw IllegalStateException("Unknown BaseEventDetailsAdapterItem child")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].adapterItemType.ordinal
    }
}