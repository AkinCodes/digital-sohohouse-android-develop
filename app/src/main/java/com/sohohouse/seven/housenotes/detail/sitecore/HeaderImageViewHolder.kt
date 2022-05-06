package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.views.SquareImageView
import com.sohohouse.seven.databinding.HouseNoteDetailHeaderImageLayoutBinding
import com.sohohouse.seven.databinding.HouseNoteDetailHeaderImageNoparentBinding

class HeaderImageViewHolder(private val binding: ViewBinding, private val merge: Boolean = false) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(item: HouseNoteDetailHeaderImageItem) {
        if (merge) {
            setImageFromUrl(
                (binding as HouseNoteDetailHeaderImageNoparentBinding).headerImage,
                item
            )
        } else {
            setImageFromUrl(
                (binding as HouseNoteDetailHeaderImageLayoutBinding).headerImage,
                item
            )
        }
    }

    private fun setImageFromUrl(image: SquareImageView, item: HouseNoteDetailHeaderImageItem) {
        image.setImageFromUrl(
            item.url,
            R.drawable.placeholder
        )
    }

}