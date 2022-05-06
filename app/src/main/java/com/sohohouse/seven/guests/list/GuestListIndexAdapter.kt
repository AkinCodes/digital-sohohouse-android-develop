package com.sohohouse.seven.guests.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.StickyHeaderAdapter
import com.sohohouse.seven.common.viewholders.DateHeaderViewHolder
import com.sohohouse.seven.databinding.GuestListViewHolderStickyHeaderBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter

class GuestListIndexAdapter : RendererDiffAdapter(), StickyHeaderAdapter<DateHeaderViewHolder> {

    // StickyHeaderAdapter
    override fun getHeaderId(position: Int): Int {
        return (mItems[position] as GuestListItem).getHeaderId()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): DateHeaderViewHolder {
        return DateHeaderViewHolder(
            GuestListViewHolderStickyHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindHeaderViewHolder(holder: DateHeaderViewHolder, position: Int) {
        when (getHeaderId(position)) {
            DateHeaderViewHolder.HEADER_TYPE_TODAY -> holder.bind(R.string.today)
            DateHeaderViewHolder.HEADER_TYPE_THIS_WEEK -> holder.bind(R.string.this_week)
            DateHeaderViewHolder.HEADER_TYPE_NEXT_WEEK -> holder.bind(R.string.next_week)
            DateHeaderViewHolder.HEADER_TYPE_IN_FUTURE -> holder.bind(R.string.in_the_distant_future)
            else -> holder.bind(null)
        }
    }

}