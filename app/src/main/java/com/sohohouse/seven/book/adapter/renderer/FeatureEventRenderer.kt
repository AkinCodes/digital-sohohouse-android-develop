package com.sohohouse.seven.book.adapter.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.adapter.model.FeatureEvent
import com.sohohouse.seven.book.adapter.viewholders.FeatureEventViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.FeatureEventCardBinding

class FeatureEventRenderer(
    private val onItemClicked: (event: EventItem, imageView: ImageView, position: Int) -> Unit = { _, _, _ -> }
) : Renderer<FeatureEvent, FeatureEventViewHolder> {

    override val type: Class<FeatureEvent> = FeatureEvent::class.java

    override fun createViewHolder(parent: ViewGroup): FeatureEventViewHolder {

        return FeatureEventViewHolder(
            FeatureEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindViewHolder(holder: FeatureEventViewHolder, item: FeatureEvent) {
        holder.bind(item, onItemClicked)
    }

}