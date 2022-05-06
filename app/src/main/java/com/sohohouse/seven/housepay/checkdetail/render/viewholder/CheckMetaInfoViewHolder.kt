package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckMetaInfoBinding

class CheckMetaInfoViewHolder(
    private val binding: ItemCheckMetaInfoBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): CheckMetaInfoViewHolder {
            return CheckMetaInfoViewHolder(
                ItemCheckMetaInfoBinding.inflate(parent.layoutInflater())
            )
        }
    }

    fun bind(value: String) {
        with(binding) {
            this.value.text = value
        }
    }

}