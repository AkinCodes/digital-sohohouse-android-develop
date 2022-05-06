package com.sohohouse.seven.base.filter.types.categories

import com.sohohouse.seven.base.filter.types.FilterBaseViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem

interface FilterCategoriesViewController : ViewController, FilterBaseViewController {
    fun setUpLayout(selectedItems: List<String>, allDataItems: List<CategoryDataItem>)
}