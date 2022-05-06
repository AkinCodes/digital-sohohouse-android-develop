package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.inflateLayout
import com.sohohouse.seven.databinding.ItemCheckLinkBinding
import com.sohohouse.seven.housepay.checkdetail.CheckLinkInfo

class CheckLinkViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(parent.inflateLayout(R.layout.item_check_link)) {

    private var item: CheckLinkInfo? = null

    private val binding by viewBinding(
        ItemCheckLinkBinding::bind
    )

    init {
        binding.root.setOnClickListener {
            item?.onClick
        }
    }

    fun bind(item: CheckLinkInfo) {
        this.item = item
        binding.root.text = item.text
    }

}