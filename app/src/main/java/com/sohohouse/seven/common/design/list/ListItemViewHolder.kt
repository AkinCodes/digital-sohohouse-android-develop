package com.sohohouse.seven.common.design.list

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ComponentListItemBinding

class ListItemViewHolder<T : ListItem>(private val binding: ComponentListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T?, listener: ((T, ImageView?, Int) -> Unit)? = null) = with(binding) {
        image.setImageUrl(item?.imageUrl)
        title.text = item?.title
        subtitle.text = item?.subtitle
        label.text = item?.label
        root.setOnClickListener { item?.let { listener?.invoke(it, image, adapterPosition) } }
    }
}