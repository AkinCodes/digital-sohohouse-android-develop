package com.sohohouse.seven.book.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.*
import com.sohohouse.seven.base.filter.types.FilterUnitFragment
import com.sohohouse.seven.book.filter.BookFilterViewModel.UiEvent
import com.sohohouse.seven.common.extensions.getSerializable
import com.sohohouse.seven.common.utils.collect
import com.sohohouse.seven.common.views.EventType
import com.uxcam.UXCam
import java.util.*

class BookFilterActivity : BaseFilterActivity(), FilterListener {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        observeViewModel()
        viewModel.updateFilterType(
            getSerializable(
                FILTER_TYPE,
                savedInstanceState
            ) as FilterType
        )
        viewModel.eventType =
            EventType.values()[savedInstanceState?.getInt(EVENT_TYPE)
                ?: intent.getIntExtra(
                    EVENT_TYPE,
                    0
                )]
        viewModel.checkCategoryTabNeeds()
        UXCam.tagScreenName(BOOK_FILTER_SCREEN)
    }

    private fun observeViewModel() {
        viewModel.eventsFlow.collect(lifecycleOwner) { event ->
            when (event) {
                is UiEvent.EnableFilterButton -> enableFilterButton(event.enable)
                is UiEvent.OnDataReady -> onDataReady()
                is UiEvent.ShowCategoryTab -> showCategoryTab()
                is UiEvent.ResetFilterSelection -> resetFilterSelection()
                is UiEvent.SwapFilterType -> swapFilterType(event.filterType)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EVENT_TYPE, viewModel.eventType.ordinal)
        super.onSaveInstanceState(outState)
    }


    override fun getFilterBtnText(): Int {
        return R.string.explore_events_filter_apply_cta
    }

    override fun getMenuLayoutId() = R.menu.filter_actionbar_menu

    override fun getFlowManagerForThis(): BaseFilterFlowManager {
        return FilterFlowManager()
    }

    override fun onSetUpLayoutComplete(fragment: Fragment) {
        supportActionBar?.let {
            it.title = getString((fragment as FilterUnitFragment).getTitleRes())
        }
    }

    override fun configureHeaderButtonsFilter() {
        super.configureHeaderButtonsFilter()
        with(binding) {
            tabs.addTab(
                tabs.newTab().setText(R.string.explore_events_filter_location_label)
                    .setTag(FilterType.LOCATION)
            )
            tabs.addTab(
                tabs.newTab().setText(R.string.explore_events_filter_date_label)
                    .setTag(FilterType.DATE)
            )
        }
    }

    // FilterViewController
    private fun enableFilterButton(isEnabled: Boolean) {
        binding.filterButton.isEnabled = isEnabled
    }

    private fun onDataReady() {
        val fragment = supportFragmentManager.findFragmentByTag(flowManager.currentFragmentTag)
        fragment?.let { (it as FilterUnitFragment).onDataReady() }
    }

    private fun showCategoryTab() {
        if (isCategoryTabPresent()) return
        binding.tabs.addTab(
            binding.tabs.newTab().setText(R.string.explore_events_filter_categories_label)
                .setTag(FilterType.CATEGORIES)
        )
    }

    private fun isCategoryTabPresent(): Boolean {
        for (i in 0 until binding.tabs.tabCount) {
            if (binding.tabs.getTabAt(i)?.tag == FilterType.CATEGORIES)
                return true
        }
        return false
    }

    private fun resetFilterSelection() {
        val fragment = supportFragmentManager.findFragmentByTag(flowManager.currentFragmentTag)
        fragment?.let { (it as FilterUnitFragment).resetSelection() }
    }


    //region FilterListener
    override fun onSelectedLocationsChanged(locationList: List<String>) {
        viewModel.updateLocationSelection(locationList)
    }

    override fun onSelectedDateChanged(date: Date?, isStart: Boolean) {
        viewModel.updateDateSelection(date, isStart)
    }

    override fun onCategorySelectionUpdated(selectedItems: List<String>) {
        viewModel.updateCategorySelection(selectedItems)
    }

    override fun getFavouriteHousesData() = viewModel.getFavouriteHouseData()
    override fun getAllHousesData() = viewModel.allHousesData

    override fun getSelectedLocations(): List<String> = viewModel.draftFilter.selectedLocationList
        ?: listOf()

    override fun getSelectedStartDate() = viewModel.draftFilter.selectedStartDate
    override fun getSelectedEndDate() = viewModel.draftFilter.selectedEndDate

    override fun getSelectedCategories() = viewModel.draftFilter.selectedCategoryList
        ?: listOf()

    override fun getAllCategories() = viewModel.getAllCategories()
    //endregion

    companion object {
        const val EVENT_TYPE = "EventType"
        const val BOOK_FILTER_SCREEN = "BOOK FILTER SCREEN"

        fun newIntent(context: Context?, filterType: FilterType, eventType: EventType) =
            Intent(context, BookFilterActivity::class.java).apply {
                putExtra(FILTER_TYPE, filterType)
                putExtra(EVENT_TYPE, eventType.ordinal)
            }
    }
}
