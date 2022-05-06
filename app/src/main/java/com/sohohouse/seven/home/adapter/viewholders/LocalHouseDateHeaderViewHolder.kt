package com.sohohouse.seven.home.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.LocalHouseDateHeaderLayoutBinding

const val LOCAL_HOUSE_DATE_HEADER_LAYOUT = R.layout.local_house_date_header_layout

class LocalHouseDateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = LocalHouseDateHeaderLayoutBinding.bind(view)

    fun bind(dateString: String) {
        binding.dateHeaderText.text = dateString
    }
}