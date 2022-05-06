package com.sohohouse.seven.common.views.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.book.adapter.viewholders.CarouselEventViewHolder
import com.sohohouse.seven.book.adapter.viewholders.EventViewHolder
import com.sohohouse.seven.book.adapter.viewholders.FullBleedEventViewHolder
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.CarouselViewItemCardBinding
import com.sohohouse.seven.databinding.FullBleedEventCardBinding
import com.sohohouse.seven.network.core.models.Event

class CarouselEventAdapter(
    items: List<CarouselEventItems>,
    private val onClick: (event: Event, sharedImageView: ImageView) -> Unit
) : BaseRecyclerDiffAdapter<EventViewHolder, CarouselEventItems>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return when (viewType) {
            CarouselEventItems.ITEM_TYPE_CONTENT -> {
                val binding = CarouselViewItemCardBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CarouselEventViewHolder(binding)
            }
            CarouselEventItems.ITEM_TYPE_FULL_BLEED_CONTENT -> {
                val binding = FullBleedEventCardBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                FullBleedEventViewHolder(binding)
            }
            else -> {
                throw IllegalArgumentException("Unexpected ViewHolder : viewType = $viewType")
            }
        }
    }

    override fun getItemCount(): Int = currentItems.size

    override fun getItemViewType(position: Int): Int = currentItems[position].itemType

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = currentItems[position]
        holder.bind(item)
        holder.itemView.clicks { onClick(item.event, holder.eventImage) }
    }

    fun updateItem(event: Event) {
        currentItems.forEachIndexed { index, item ->
            if (item.event.id == event.id) {
                item.event = event
                notifyItemChanged(index)
            }
        }
    }
}
