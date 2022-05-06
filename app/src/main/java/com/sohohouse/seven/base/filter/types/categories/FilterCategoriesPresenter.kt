package com.sohohouse.seven.base.filter.types.categories

import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem

class FilterCategoriesPresenter : BasePresenter<FilterCategoriesViewController>() {
    fun onDataReady(selectedItems: List<String>, allDataItems: List<CategoryDataItem>) {
        executeWhenAvailable { view, _, _ -> view.setUpLayout(selectedItems, allDataItems) }
    }
}