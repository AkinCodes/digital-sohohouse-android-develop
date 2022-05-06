package com.sohohouse.seven.home.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.carousel.CarouselEventAdapter
import com.sohohouse.seven.databinding.LocalHouseHappeningNowLayoutBinding

const val LOCAL_HOUSE_HAPPENING_NOW_LAYOUT = R.layout.local_house_happening_now_layout

class HappeningNowContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var binding = LocalHouseHappeningNowLayoutBinding.bind(view)

    fun bind(
        headerText: String,
        captionText: String,
        carouselEventAdapter: CarouselEventAdapter,
        isFavorited: Boolean = false
    ) {
        with(binding) {
            happeningNowSectionLabel.text = headerText
            happeningNowSectionCaption.text = captionText

            happeningNowGallery.apply {
                adapter = carouselEventAdapter
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                isNestedScrollingEnabled = true
            }
        }
    }

}