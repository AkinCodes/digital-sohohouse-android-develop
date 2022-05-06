package com.sohohouse.seven.base.filter

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.filter.FilterType.LOCATION
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer

abstract class BaseFilterPresenter<V : BaseFilterViewController>(
    protected val zipRequestsUtil: ZipRequestsUtil,
    private val houseManager: HouseManager,
    private val analyticsManager: AnalyticsManager,
    private val venueRepo: VenueRepo
) :
    BasePresenter<V>(), PresenterLoadable<V>, ErrorViewStatePresenter<V> {

    companion object {
        private const val TAG = "BaseFilterPresenter"
    }

    lateinit var filterType: FilterType
    lateinit var draftFilter: Filter
    lateinit var favouriteHousesData: List<LocationRecyclerChildItem>
    lateinit var allHousesData: List<LocationRecyclerParentItem>
    lateinit var allCategoryDataItems: List<CategoryDataItem>

    abstract fun saveSelectionInfo()
    abstract fun onDataFiltered()
    fun updateFilterType(filterType: FilterType) {
        this.filterType = filterType
        executeWhenAvailable { view, _, _ -> view.swapFilterType(filterType) }
    }

    protected open fun preFetchSelectedFilterInfo() {
        // do nothing
    }

    override fun onAttach(view: V, isFirstAttach: Boolean, isRecreated: Boolean) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.Filter.name)
        //Fetching category data just to show/hide Category tab
        if (!::allCategoryDataItems.isInitialized) {
            loadCategoryDataForFirstTime()
        }
    }

    @SuppressLint("CheckResult")
    fun fetchSelectedFilterInfo() {
        preFetchSelectedFilterInfo()
        if (filterType == LOCATION && !::allHousesData.isInitialized) {
            Single.just(venueRepo.venues().filterWithTopLevel())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(loadTransformer())
                .subscribe(Consumer {
                    val organizedData = houseManager.organizeHousesForLocationRecyclerView(
                        it,
                        false,
                        draftFilter.selectedLocationList,
                        includeStudios = false
                    )
                    draftFilter.selectedLocationList = organizedData.first
                    favouriteHousesData = organizedData.second
                    allHousesData = organizedData.third
                    onLocationDataReadyForFirstTime()
                })
        } else if (filterType == LOCATION) {
            syncLocationSelection()
            onLocationDataReadyWithData()
        } else {
            onCategoryDataReadyWithData()
        }
    }

    fun syncLocationSelection() {
        draftFilter.selectedLocationList?.let {
            for (item in favouriteHousesData) {
                item.selected = it.contains(item.id)
            }
        } ?: run {
            val selectedItemList = mutableListOf<String>()
            for (item in favouriteHousesData) {
                selectedItemList.add(item.id)
                item.selected = true
            }
            draftFilter.selectedLocationList = selectedItemList
        }

        draftFilter.selectedLocationList?.let {
            for (parentItem in allHousesData) {
                for (childItem in parentItem.childList) {
                    childItem.selected = it.contains(childItem.id)
                }
            }
        }
    }

    fun isAllCategoryItemsInitialized() = ::allCategoryDataItems.isInitialized

    //region Error
    override fun reloadDataAfterError() {
        fetchSelectedFilterInfo()
    }
    //endregion

    abstract fun resetToDefaultSelection()
    abstract fun onLocationDataReadyWithData()
    abstract fun onCategoryDataReadyWithData()
    abstract fun onLocationDataReadyForFirstTime()
    abstract fun loadCategoryDataForFirstTime()
}