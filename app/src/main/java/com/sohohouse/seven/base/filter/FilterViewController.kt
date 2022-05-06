package com.sohohouse.seven.base.filter


interface FilterViewController : BaseFilterViewController {
    fun enableFilterButton(isEnabled: Boolean)
    fun onDataReady()
    fun showCategoryTab()
}