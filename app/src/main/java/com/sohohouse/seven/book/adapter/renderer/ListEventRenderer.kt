package com.sohohouse.seven.book.adapter.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.adapter.model.ListEvent
import com.sohohouse.seven.book.adapter.viewholders.ListEventViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ItemBookingListItemBinding

class ListEventRenderer(
    private val onItemClicked: (event: EventItem, imageView: ImageView, position: Int) -> Unit = { _, _, _ -> }
) : Renderer<ListEvent, ListEventViewHolder> {

    override val type: Class<ListEvent> = ListEvent::class.java

    override fun createViewHolder(parent: ViewGroup): ListEventViewHolder {
        val binding =
            ItemBookingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListEventViewHolder(binding)
    }

    override fun bindViewHolder(holder: ListEventViewHolder, item: ListEvent) {
        holder.bind(item, onItemClicked)
    }

}