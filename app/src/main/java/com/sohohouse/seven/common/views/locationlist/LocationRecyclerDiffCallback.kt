package com.sohohouse.seven.common.views.locationlist

import androidx.recyclerview.widget.DiffUtil


class LocationRecyclerDiffCallback(
    private var newList: List<LocationRecyclerBaseItem>,
    private var oldList: List<LocationRecyclerBaseItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].name === newList[newItemPosition].name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}