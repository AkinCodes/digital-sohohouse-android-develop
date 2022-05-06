package com.sohohouse.seven.home.houseboard.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.PinToTopAdapterDataObserver
import com.sohohouse.seven.common.views.PeekingLinearLayoutManager
import com.sohohouse.seven.databinding.ItemUpcomingBookingsCarouselBinding
import com.sohohouse.seven.home.houseboard.items.UpcomingBookingsCarouselAdapter
import com.sohohouse.seven.home.houseboard.items.UpcomingBookingsContainerItem
import com.sohohouse.seven.home.houseboard.renderers.UpcomingBookingsListener
import java.lang.ref.WeakReference

class UpcomingBookingsContainerViewHolder(private val binding: ItemUpcomingBookingsCarouselBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.upcomingBookingsCarouselRv.apply {
            adapter = UpcomingBookingsCarouselAdapter().also {
                it.registerAdapterDataObserver(PinToTopAdapterDataObserver(WeakReference(this)))
            }
            layoutManager =
                PeekingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun bind(item: UpcomingBookingsContainerItem, listener: UpcomingBookingsListener) {
        binding.upcomingBookingsCarouselRv.apply {
            (adapter as UpcomingBookingsCarouselAdapter).apply {
                this.listener = listener
                submitList(item.items)
            }
        }
        binding.upcomingBookingsSeeAll.apply {
            if (item.showSeeAllBtn) {
                visibility = View.VISIBLE
                setOnClickListener { listener.onSeeAllClick() }
            } else {
                visibility = View.INVISIBLE
            }
        }
    }

}