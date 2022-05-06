package com.sohohouse.seven.home.houseboard.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemHoursDisplayBinding

class HoursDisplayViewHolder(private val binding: ItemHoursDisplayBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setVenueName(name: String?) {
        binding.venueName.text = name
    }

    fun setTopLabel(text: String) {
        binding.topLabel.text = text
    }

    fun setBottomLabel(text: String) {
        binding.bottomLabel.text = text
    }

    fun setButtonText(text: String) {
        binding.button.text = text
    }

    fun setOnButtonClickListener(onClickListener: View.OnClickListener) {
        binding.button.setOnClickListener(onClickListener)
    }

    fun setVenueNameClickListener(onClickListener: View.OnClickListener) {
        binding.venueName.setOnClickListener(onClickListener)
    }
}