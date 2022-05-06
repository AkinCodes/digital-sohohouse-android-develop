package com.sohohouse.seven.book.eventdetails.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.eventdetails.model.EventExternalLinkAdapterItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.EventDescriptionLinkLayoutBinding

const val EVENT_EXTERNAL_LINK_LAYOUT = R.layout.event_description_link_layout

class EventExternalLinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = EventDescriptionLinkLayoutBinding.bind(view)

    fun bind(description: String, onItemClicked: () -> Unit) {

        binding.description.text = description
        binding.CTA.clicks { onItemClicked() }
    }

    fun bind(item: EventExternalLinkAdapterItem, onItemClicked: (url: String) -> Unit) {
        binding.description.text = item.description
        binding.CTA.clicks { onItemClicked(item.url) }
    }
}