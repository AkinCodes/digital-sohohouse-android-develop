package com.sohohouse.seven.book.table

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getFormattedDayOfWeekDayMonth
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemAlternativeRestaurantBinding
import androidx.recyclerview.widget.DiffUtil as rvDiffUtil

class AlternativeRestaurantListAdapter :
    ListAdapter<TableBookingDetails, AlternativeRestaurantListAdapter.AlternativeRestaurantViewHolder>(
        DiffUtil()
    ) {

    lateinit var clickListener: (TableBookingDetails, Boolean) -> Unit?

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlternativeRestaurantViewHolder {
        val binding = ItemAlternativeRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlternativeRestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlternativeRestaurantViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.name.text = name
                binding.address.text = address
                binding.reservationDetails.text =
                    binding.reservationDetails.context.resources.getQuantityString(
                        R.plurals.table_booking_date_and_seats,
                        persons,
                        date.getFormattedDayOfWeekDayMonth(),
                        persons
                    )
                binding.image.setImageFromUrl(imageUrl)
                holder.itemView.setOnClickListener {
                    this.let {
                        clickListener.invoke(it, false)
                    }
                }
            }
        }
    }

    class DiffUtil : rvDiffUtil.ItemCallback<TableBookingDetails>() {
        override fun areItemsTheSame(
            oldItem: TableBookingDetails,
            newItem: TableBookingDetails
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TableBookingDetails,
            newItem: TableBookingDetails
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class AlternativeRestaurantViewHolder(
        val binding: ItemAlternativeRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root)
}