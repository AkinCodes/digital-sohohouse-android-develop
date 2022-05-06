package com.sohohouse.seven.base.filter.types.categories

import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.common.views.categorylist.BaseCategoriesRecyclerAdapter
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.common.views.categorylist.CategorySelectedListener

class FilterCategoriesAdapter(
    selectedItems: MutableList<String>,
    allDataItems: List<CategoryDataItem>,
    private val listener: FilterListener
) : BaseCategoriesRecyclerAdapter(selectedItems, object : CategorySelectedListener {
    override fun onCategorySelected(selectedItems: List<String>) {
        listener.onCategorySelectionUpdated(selectedItems)
    }
}) {
    init {
        itemList.addAll(allDataItems)
    }
}