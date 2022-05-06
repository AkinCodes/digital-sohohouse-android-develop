package com.sohohouse.seven.housenotes.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.ItemHomeHouseNotesCarouselBinding

class HouseNoteCarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var binding = ItemHomeHouseNotesCarouselBinding.bind(view)
    private val houseNotesAdapter = HouseNotesCarouselAdapter()

    init {
        binding.houseNotesCarousel.apply {
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = houseNotesAdapter
        }
    }

    fun bind(
        item: BaseAdapterItem.HouseNoteItem.Carousel,
        onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit
    ) {
        houseNotesAdapter.let {
            it.onHouseNoteClicked = onHouseNoteClicked
            it.submitList(item.houseNotes)
        }
    }

}