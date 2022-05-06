package com.sohohouse.seven.browsehouses

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.browsehouses.recycler.BrowseAllHouseViewHolder
import com.sohohouse.seven.browsehouses.recycler.BrowseAllHousesRegionViewHolder
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.BrowseHousesHouseItemBinding
import com.sohohouse.seven.databinding.BrowseHousesRegionItemBinding
import com.sohohouse.seven.home.browsehouses.BrowseHousesViewSizeListener

class BrowseAllHouseAdapter(private val viewSizeListener: BrowseHousesViewSizeListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var attachedRecyclerView: RecyclerView? = null
    private var initializedArrow: Boolean = false
    private val dataItems: ArrayList<BaseAdapterItem.BrowseHousesItem> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<BaseAdapterItem.BrowseHousesItem>) {
        dataItems.clear()
        dataItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (BaseAdapterItem.BrowseHousesItemType.values()[viewType]) {
            BaseAdapterItem.BrowseHousesItemType.REGION_HEADER -> BrowseAllHousesRegionViewHolder(
                BrowseHousesRegionItemBinding.inflate(inflater, parent, false)
            )
            BaseAdapterItem.BrowseHousesItemType.CONTENT -> BrowseAllHouseViewHolder(
                BrowseHousesHouseItemBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataItems[position]

        if (position == 0 && item is BaseAdapterItem.BrowseHousesItem.RegionHeader) {
            getBackgroundForPosition(1)
        }

        when (BaseAdapterItem.BrowseHousesItemType.values()[holder.itemViewType]) {
            BaseAdapterItem.BrowseHousesItemType.REGION_HEADER -> (holder as BrowseAllHousesRegionViewHolder).bind(
                item as BaseAdapterItem.BrowseHousesItem.RegionHeader
            )
            BaseAdapterItem.BrowseHousesItemType.CONTENT -> {
                (holder as BrowseAllHouseViewHolder).bind(item as BaseAdapterItem.BrowseHousesItem.Content) { venue ->
                    viewSizeListener.onHomeClicked(venue, position)
                }
                if (position == 1 && !initializedArrow) {
                    holder.iconVisibility(true)
                    initializedArrow = true
                } else {
                    holder.iconVisibility(false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].type.ordinal
    }

    fun getBackgroundForPosition(position: Int) {
        val item = dataItems[Math.min(position, dataItems.size - 1)]
        if (item is BaseAdapterItem.BrowseHousesItem.Content) {
            viewSizeListener.setBackgroundImage(item.house.house.get(item.house.document)?.houseImageSet?.xlargePng)
        }
    }

    fun iconVisibility(position: Int, isVisible: Boolean) {
        when (dataItems[Math.min(position, dataItems.size - 1)]) {
            is BaseAdapterItem.BrowseHousesItem.Content -> {
                attachedRecyclerView?.findViewHolderForAdapterPosition(
                    Math.min(
                        position,
                        dataItems.size - 1
                    )
                )?.let {
                    when (it) {
                        is BrowseAllHouseViewHolder -> {
                            it.iconVisibility(isVisible)
                        }
                        else -> {}
                    }
                }
            }
            else -> {}
        }
    }
}