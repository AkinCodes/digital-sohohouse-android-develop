package com.sohohouse.seven.book.table

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemSelectRestaurantBinding

class SelectRestaurantAdapter : RecyclerView.Adapter<SelectRestaurantAdapter.ViewHolder>() {

    private val restaurants = ArrayList<Restaurant>()
    var itemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectRestaurantBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = restaurants.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(restaurants[position], itemClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fill(items: List<Restaurant>) {
        restaurants.clear()
        restaurants.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemSelectRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var item: Restaurant? = null

        fun bind(restaurant: Restaurant, listener: ((String) -> (Unit))?) {
            item = restaurant

            with(binding) {
                name.text = restaurant.name
                address.text = restaurant.address
                date.text = restaurant.date
                image.setImageFromUrl(restaurant.imageUrl)
            }

            binding.root.setOnClickListener {
                listener?.invoke(item?.id ?: "")
            }
        }
    }
}

