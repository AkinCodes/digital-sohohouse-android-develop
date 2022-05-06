package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ItemHouseNoteDetailsBodyImageblockBinding

class BodyImageBlockViewHolder(private val binding: ItemHouseNoteDetailsBodyImageblockBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    val set = ConstraintSet()

    fun bind(item: HouseNoteDetailIBodyImageBlockItem) {
        with(binding) {
            val ratio = "H,${item.aspectRatio}:1"
            set.clone(constraintLayout)
            set.setDimensionRatio(bodyImage.id, ratio)
            set.applyTo(constraintLayout)

            bodyImage.post {
                bodyImage.setImageFromUrl(item.url)
            }

            caption.text = item.caption
            caption.setVisible(item.caption.isNotEmpty())
        }

    }

    private val constraintLayout get() = binding.root as ConstraintLayout
}