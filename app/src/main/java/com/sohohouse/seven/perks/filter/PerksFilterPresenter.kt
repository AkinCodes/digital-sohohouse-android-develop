package com.sohohouse.seven.perks.filter

import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.filter.FilterType
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterBaseItem
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterItemType
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterTextItem
import com.sohohouse.seven.perks.common.enums.HouseRegionFilter
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager
import javax.inject.Inject

class PerksFilterPresenter @Inject constructor(private val filterManager: RegionFilterManager) :
    BasePresenter<PerksFilterViewController>() {

    lateinit var filterType: FilterType

    private var allRegionDataItems: MutableList<CategoryAdapterBaseItem> = mutableListOf()

    override fun onAttach(
        view: PerksFilterViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        if (isFirstAttach) {
            prepareView()
            return
        }

        reloadData()
    }

    private fun reloadData() {
        view.onDataReady(filterManager.appliedFilter.selectedRegions, allRegionDataItems)
    }

    private fun prepareView() {

        allRegionDataItems.add(
            CategoryAdapterTextItem(
                R.string.regions_label,
                CategoryAdapterItemType.FILTER_HEADER
            )
        )

        HouseRegionFilter.values().mapTo(allRegionDataItems) {
            PerkFilterDataItem(
                it.id,
                view.context.getString(it.resourceString),
                "",
                filterManager.appliedFilter.selectedRegions.contains(it.id), true
            )
        }

        filterManager.draftFilter.selectedRegions = filterManager.appliedFilter.selectedRegions

        view.onDataReady(filterManager.appliedFilter.selectedRegions, allRegionDataItems)

    }

    fun onCategorySelected(selectedCategories: List<String>) {
        filterManager.draftFilter.selectedRegions = selectedCategories
    }

    fun onDataFiltered() {
    }

    fun saveSelectionInfo() {
        filterManager.applyDraft()
    }
}