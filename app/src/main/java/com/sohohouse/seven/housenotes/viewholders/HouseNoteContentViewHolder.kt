package com.sohohouse.seven.housenotes.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.LocalHouseHouseNoteContentLayoutBinding
import com.sohohouse.seven.network.sitecore.SitecoreResourceFactory

const val HOUSE_NOTES_CONTENT_LAYOUT = R.layout.local_house_house_note_content_layout

class HouseNoteContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = LocalHouseHouseNoteContentLayoutBinding.bind(itemView)

    init {
        binding.houseNoteImage.clipToOutline = true
    }

    fun bind(item: BaseAdapterItem.HouseNoteItem.Content) {
        binding.houseNoteTitle.text = item.title

        val url = if (item.videoImageUrl.isNotEmpty()) item.videoImageUrl else item.imageUrl
        binding.houseNoteImage.setImageFromUrl(SitecoreResourceFactory.getImageUrl(url))
    }

    fun bind(
        item: BaseAdapterItem.HouseNoteItem.Content,
        onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit
    ) {
        bind(item)
        binding.houseNoteImage.setOnClickListener {
            onHouseNoteClicked(item.id, item.isCityGuide, adapterPosition)
        }
    }

    fun setOnClickListener(onNext: (Any) -> Unit) {
        binding.houseNoteImage.clicks(onNext)
    }
}