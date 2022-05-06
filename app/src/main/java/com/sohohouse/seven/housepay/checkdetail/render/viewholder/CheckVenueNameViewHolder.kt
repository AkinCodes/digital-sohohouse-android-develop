package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckVenueHeaderBinding

class CheckVenueNameViewHolder(
    private val binding: ItemCheckVenueHeaderBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): CheckVenueNameViewHolder {
            return CheckVenueNameViewHolder(
                ItemCheckVenueHeaderBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(venue: String) {
        with(binding) {
            this.venueName.text = venue
        }
    }

}