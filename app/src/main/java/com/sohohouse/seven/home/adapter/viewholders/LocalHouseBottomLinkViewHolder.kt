package com.sohohouse.seven.home.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.LocalHouseBottomLinkLayoutBinding

const val LOCAL_HOUSE_BOTTOM_LINK_LAYOUT = R.layout.local_house_bottom_link_layout

class LocalHouseBottomLinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = LocalHouseBottomLinkLayoutBinding.bind(view)

    fun bind(perks: Int, browse: Int) = with(binding) {
        perksBtn.setText(perks)
        browseHousesBtn.setText(browse)
    }
}