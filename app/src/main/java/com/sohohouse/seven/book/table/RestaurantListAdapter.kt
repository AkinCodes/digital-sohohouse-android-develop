package com.sohohouse.seven.book.table

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemRestaurantBinding

class RestaurantListAdapter : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>() {

    private val items = ArrayList<Restaurant>()
    var clickListener: ((Restaurant) -> (Unit))? = null
    var bookListener: ((Restaurant) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(layout, bookListener, clickListener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fill(restaurants: List<Restaurant>) {
        items.clear()
        items.addAll(restaurants)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemRestaurantBinding,
        private val bookListener: ((Restaurant) -> (Unit))?,
        private val clickListener: ((Restaurant) -> (Unit))?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { item?.let { clickListener?.invoke(it) } }
            binding.reserveTable.setOnClickListener { item?.let { bookListener?.invoke(it) } }
        }

        private var item: Restaurant? = null

        fun bind(item: Restaurant) {
            this.item = item
            with(binding) {
                name.text = item.name
                address.text = item.address
                image.setImageFromUrl(item.imageUrl)
            }
        }
    }

}