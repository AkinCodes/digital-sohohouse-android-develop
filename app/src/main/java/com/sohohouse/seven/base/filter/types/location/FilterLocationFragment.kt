package com.sohohouse.seven.base.filter.types.location

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.base.filter.types.FilterUnitFragment
import com.sohohouse.seven.base.filter.types.location.FilterLocationViewModel.UiEvent
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.locationlist.BaseFilterLocationAdapter
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.databinding.FilterLocationFragmentBinding

class FilterLocationFragment : BaseMVVMFragment<FilterLocationViewModel>(), FilterUnitFragment {

    private val viewBinding: FilterLocationFragmentBinding by viewBinding(
        FilterLocationFragmentBinding::bind
    )

    override val contentLayoutId: Int
        get() = R.layout.filter_location_fragment

    override val viewModelClass: Class<FilterLocationViewModel>
        get() = FilterLocationViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventsFlow.collectLatest(viewLifecycleOwner) { event ->
            when (event) {
                is UiEvent.SetUpRecyclerView -> with(event) {
                    setUpRecyclerView(favouriteHousesData, allHousesData)
                }
                else -> {}
            }
        }
    }


    override fun getTitleRes() = R.string.explore_events_filter_header

    override fun onDataReady() {
        val favouriteHousesData = (activity as FilterListener).getFavouriteHousesData()
        val allHousesData = (activity as FilterListener).getAllHousesData()
        viewModel.onDataReady(favouriteHousesData, allHousesData)
    }

    private fun setUpRecyclerView(
        favouriteHousesData: List<LocationRecyclerChildItem>,
        allHousesData: List<LocationRecyclerParentItem>
    ) {
        val layoutManager = FlexboxLayoutManager(context)
        viewBinding.housesRecyclerView.layoutManager = layoutManager
        val adapter = ExploreFilterLocationAdapter(
            allHousesData,
            favouriteHousesData,
            activity as FilterListener
        )
        viewBinding.housesRecyclerView.adapter = adapter
    }

    override fun resetSelection() {
        val selectedData = (activity as FilterListener).getSelectedLocations()
        (viewBinding.housesRecyclerView.adapter as? BaseFilterLocationAdapter)?.resetSelection(
            selectedData
        )
    }

    companion object {
        const val TAG = "FilterLocationFragment"
    }
}