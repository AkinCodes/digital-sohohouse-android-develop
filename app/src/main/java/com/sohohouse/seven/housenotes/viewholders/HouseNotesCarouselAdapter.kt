package com.sohohouse.seven.housenotes.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemHouseNoteCarouselItemBinding
import com.sohohouse.seven.network.sitecore.SitecoreResourceFactory
import javax.inject.Inject


class HouseNotesCarouselAdapter
    :
    BaseRecyclerDiffAdapter<HouseNotesCarouselAdapter.HouseNoteCarouselItemViewHolder, BaseAdapterItem.HouseNoteItem.Carousel.Item>() {
    var onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit =
        { _, _, _ -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HouseNoteCarouselItemViewHolder {
        return HouseNoteCarouselItemViewHolder(
            ItemHouseNoteCarouselItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HouseNoteCarouselItemViewHolder, position: Int) {
        holder.bind(getItem(position), onHouseNoteClicked)
    }

    inner class HouseNoteCarouselItemViewHolder(private val binding: ItemHouseNoteCarouselItemBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        @Inject
        lateinit var analytics: AnalyticsManager

        init {
            appComponent.inject(this)
        }

        fun bind(
            item: BaseAdapterItem.HouseNoteItem.Carousel.Item,
            onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit?
        ) {
            with(binding) {
                title.text = item.title

                val url = item.videoImageUrl.ifEmpty { item.imageUrl }
                image.setImageFromUrl(SitecoreResourceFactory.getImageUrl(url))

                root.clicks {
                    analytics.logEventAction(AnalyticsManager.Action.HomeHouseNotesTapCarousel)
                    onHouseNoteClicked(item.id, false, adapterPosition)
                }
            }
        }
    }
}
