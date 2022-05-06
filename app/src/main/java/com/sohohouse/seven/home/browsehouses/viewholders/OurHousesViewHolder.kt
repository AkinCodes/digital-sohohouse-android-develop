package com.sohohouse.seven.home.browsehouses.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.utils.ImageRandomizer
import com.sohohouse.seven.databinding.ItemHomeContentSectionHeaderBinding
import com.sohohouse.seven.databinding.ItemHomeOurHousesBinding

class OurHousesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var binding = ItemHomeOurHousesBinding.bind(itemView)
    var headerBinding = ItemHomeContentSectionHeaderBinding.bind(binding.root)

    var imageRandomizer: ImageRandomizer = ImageRandomizer().apply {
        attach(binding.ourHousesImageview)
    }

    fun bind(ourHousesItem: BaseAdapterItem.OurHousesItem) {
        with(headerBinding) {
            title.setText(ourHousesItem.title)
            subtitle.setText(ourHousesItem.subtitle)
        }

        binding.description.setText(ourHousesItem.description)

        imageRandomizer.apply {
            setImageResIds(ourHousesItem.imageResIds as MutableList<Int>)
            start()
        }
    }

    fun onDetach() {
        imageRandomizer.onDetached()
    }

}