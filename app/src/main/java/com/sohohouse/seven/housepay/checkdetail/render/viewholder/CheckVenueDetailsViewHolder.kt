package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.graphics.Paint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.inflateLayout
import com.sohohouse.seven.databinding.ItemCheckVenueDetailsBinding
import com.sohohouse.seven.housepay.checkdetail.VenueDetailsInfo

class CheckVenueDetailsViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(parent.inflateLayout(R.layout.item_check_venue_details)) {

    private val binding by viewBinding(ItemCheckVenueDetailsBinding::bind)

    private var item: VenueDetailsInfo? = null

    init {
        with(binding.housepayVenueDetailsPhone) {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                item?.let { it.onPhoneNumberClick(it.phoneNumber) }
            }
        }
    }

    fun bind(item: VenueDetailsInfo) {
        this.item = item
        binding.housepayVenueDetailsAddress.text = item.address
        binding.housepayVenueDetailsHours.text = item.hours.joinToString(separator = "\n")
        binding.housepayVenueDetailsName.text = item.name
        binding.housepayVenueDetailsPhone.text = item.phoneNumber
    }

}