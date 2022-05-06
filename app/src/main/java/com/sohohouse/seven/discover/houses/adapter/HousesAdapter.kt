package com.sohohouse.seven.discover.houses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.toggleVisibiltyIfEmpty
import com.sohohouse.seven.databinding.ViewHolderHouseItemBinding
import com.sohohouse.seven.discover.houses.adapter.HousesAdapter.HouseViewHolder


class HousesAdapter(private val listener: (String) -> Unit) :
    BaseRecyclerDiffAdapter<HouseViewHolder, HouseItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        return HouseViewHolder(
            ViewHolderHouseItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HouseViewHolder(val binding: ViewHolderHouseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener(
                    getItem(adapterPosition).slug ?: return@setOnClickListener
                )
            }
        }

        fun bind(item: HouseItem) = with(binding) {
            image.setImageUrl(item.imageUrl)
            subtitle.text = item.city
            subtitle.toggleVisibiltyIfEmpty()
            title.text = item.title
        }
    }

}
