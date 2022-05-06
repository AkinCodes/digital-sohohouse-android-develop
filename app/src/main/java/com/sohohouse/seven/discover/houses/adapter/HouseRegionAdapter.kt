package com.sohohouse.seven.discover.houses.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.getDimensionPixelSize
import com.sohohouse.seven.common.views.ItemPaddingDecoration
import com.sohohouse.seven.databinding.ViewHolderHousesRegionItemBinding

class HouseRegionAdapter(
    private val listener: (String) -> Unit,
    private val isUserInputEnabled: (Boolean) -> Unit
) : BaseRecyclerDiffAdapter<ViewHolder, BaseHouseItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            BaseHouseItem.TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_houses_header_item, parent, false)
                HouseHeaderViewHolder(itemView)
            }
            BaseHouseItem.TYPE_REGION_ITEM -> {
                val binding = ViewHolderHousesRegionItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HouseRegionViewHolder(binding)
            }
            else -> throw Exception("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is HouseRegionViewHolder -> {
                holder.bind(getItem(position) as RegionItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    inner class HouseRegionViewHolder(val binding: ViewHolderHousesRegionItemBinding) :
        ViewHolder(binding.root) {
        private val adapter = HousesAdapter(listener)

        init {
            binding.recyclerView.let { recyclerview ->
                recyclerview.adapter = adapter
                recyclerview.addItemDecoration(
                    ItemPaddingDecoration(
                        RecyclerView.HORIZONTAL,
                        itemPadding = getDimensionPixelSize(R.dimen.dp_8)
                    )
                )

                //Disable parent ViewPager swiping when houses recyclerview is scrollable
                val listener = object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        val action = e.action
                        if (recyclerview.canScrollHorizontally(1)
                            || recyclerview.canScrollHorizontally(-1)
                        ) {
                            when (action) {
                                MotionEvent.ACTION_MOVE -> {
                                    isUserInputEnabled(false)
                                }
                                else -> {
                                    isUserInputEnabled(true)
                                }
                            }
                            return false
                        } else {
                            isUserInputEnabled(true)
                            return true
                        }
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                }

                recyclerview.addOnItemTouchListener(listener)
            }
        }

        fun bind(item: RegionItem) {
            binding.title.setText(item.title)
            adapter.submitList(item.houses)
        }
    }

    inner class HouseHeaderViewHolder(itemView: View) : ViewHolder(itemView)
}
