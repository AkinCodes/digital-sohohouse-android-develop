package com.sohohouse.seven.home.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.LocalHouseAllHouseLayoutBinding

const val LOCAL_HOUSE_ALL_HOUSES_LAYOUT = R.layout.local_house_all_house_layout

class LocalHouseAllHousesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = LocalHouseAllHouseLayoutBinding.bind(view)

    fun setBookingBtnClickListener(onNext: (Any) -> Unit) {
        binding.localHouseAllHousesBg.setOnClickListener(onNext)
    }
}