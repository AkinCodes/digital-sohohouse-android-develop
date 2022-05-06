package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.inflateLayout
import com.sohohouse.seven.databinding.ItemCheckReceiptBannerBinding
import com.sohohouse.seven.housepay.checkdetail.CheckBannerInfo

class CheckReceiptBannerViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(parent.inflateLayout(R.layout.item_check_receipt_banner)) {

    private val binding by viewBinding(ItemCheckReceiptBannerBinding::bind)

    fun bind(info: CheckBannerInfo) {
        binding.receiptBannerPrimary.text = info.title
        binding.receiptBannerSecondary.text = info.subtitle
    }

}
