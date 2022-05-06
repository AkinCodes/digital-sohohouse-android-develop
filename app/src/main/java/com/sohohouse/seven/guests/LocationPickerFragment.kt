package com.sohohouse.seven.guests

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.isEmpty
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.common.house.LocationPickerType
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.locationlist.LocationCityItem
import com.sohohouse.seven.common.views.locationlist.LocationClickListener
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.databinding.FragmentLocationPickerBinding
import com.sohohouse.seven.databinding.FragmentLocationPickerListBinding

class LocationPickerFragment : BaseMVVMBottomSheet<LocationPickerViewModel>() {

    companion object {
        private const val EXTRA_SELECTED_HOUSE_ID = "EXTRA_SELECTED_HOUSE_ID"
        private const val EXTRA_IS_BOOK_A_TABLE = "EXTRA_IS_BOOK_A_TABLE"
        fun newInstance(
            selectedHouseId: String?,
            isBookingTable: Boolean = false,

            ): LocationPickerFragment {
            return LocationPickerFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_BOOK_A_TABLE, isBookingTable)
                    putString(EXTRA_SELECTED_HOUSE_ID, selectedHouseId)
                }
            }
        }
    }

    override val viewModelClass: Class<LocationPickerViewModel>
        get() = LocationPickerViewModel::class.java

    override val contentLayout: Int
        get() = R.layout.fragment_location_picker


    private val binding by viewBinding(FragmentLocationPickerBinding::bind)


    private val isBookingTable by lazy { arguments?.getBoolean(EXTRA_IS_BOOK_A_TABLE) ?: false }

    var listener: Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(
            viewModel.selection.value ?: arguments?.getString(EXTRA_SELECTED_HOUSE_ID),
            isBookingTable
        )

        observeItems()
        binding.confirmSelection.clicks { onConfirmed() }
        binding.closeBtn.clicks { dismiss() }
    }

    private fun onConfirmed() {
        viewModel.selectedLocation?.let {
            (context as? Listener)?.onLocationSelected(it)
            listener?.onLocationSelected(it)
        }
        listener = null
        dismiss()
    }

    private fun observeItems() {
        viewModel.items.observe(lifecycleOwner, Observer { data ->
            if (data == null) return@Observer
            if (binding.viewpager.adapter == null) {
                setUpViewPager(data)
            }
        })

        viewModel.selectedLocationTab.collectLatest(viewLifecycleOwner) {
            selectTab(it)
        }

        viewModel.confirmEnabled.observe(lifecycleOwner) { enabled ->
            binding.confirmSelection.isEnabled = enabled
        }
    }

    private fun setUpViewPager(data: LocationPickerViewModel.Data) {

        val types = mutableListOf<LocationPickerType>().apply {
            if (data.houses.isEmpty().not()) add(LocationPickerType.HOUSE)
            if (data.restaurants.isEmpty().not()) add(LocationPickerType.RESTAURANT)
            if (data.cities.isNotEmpty()) add(LocationPickerType.CITY)
        }

        binding.viewpager.adapter = LocationListPagerAdapter(this, types, ::onLocationClicked)

        with(binding) {
            tabs.apply { setVisible(types.size > 1) }
                .takeIf { it.visibility == View.VISIBLE }
                ?.let { tabs ->
                    TabLayoutMediator(tabs, viewpager) { tab, position ->
                        tab.setText(types[position].getLabel())
                    }.attach()
                }
        }
    }

    private fun selectTab(index: Int) {
        with(binding) {
            tabs.getTabAt(index)?.select()
        }
    }

    private fun onLocationClicked(venueId: List<String>) {
        viewModel.onSelection(venueId)
    }

    interface Listener {
        fun onLocationSelected(type: LocationType)
    }

    class LocationListFragment : BaseFragment(), LocationClickListener {

        companion object {
            const val REQ_KEY_PICK_LOCATION = "REQ_KEY_PICK_LOCATION"
            const val EXTRA_SELECTED_LOCATION = "EXTRA_SELECTED_LOCATION"
            const val PICKER_TYPE = "PICKER_TYPE"

            fun newInstance(pickerType: LocationPickerType): LocationListFragment {
                return LocationListFragment().apply {
                    arguments = bundleOf(PICKER_TYPE to pickerType)
                }
            }
        }

        private val viewModel: LocationPickerViewModel by viewModels(
            ownerProducer = { requireParentFragment() }
        )

        private val pickerType: LocationPickerType
            get() = requireArguments().getSerializable(
                PICKER_TYPE
            ) as LocationPickerType

        override val contentLayoutId get() = R.layout.fragment_location_picker_list

        private val binding by viewBinding(FragmentLocationPickerListBinding::bind)


        @Suppress("NON_EXHAUSTIVE_WHEN")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            viewModel.items.observe(viewLifecycleOwner) {
                when (pickerType) {
                    LocationPickerType.HOUSE -> populateList(it.houses, pickerType)
                    LocationPickerType.RESTAURANT -> populateList(it.restaurants, pickerType)
                    LocationPickerType.CITY -> populateListOfCity(it.cities)
                }
            }
            with(binding) {
                viewModel.selection.observe(viewLifecycleOwner) {
                    if (locationPickerRv.adapter is LocationPickerAdapter) {
                        (locationPickerRv.adapter as LocationPickerAdapter)
                            .resetSelection(listOfNotNull(it))
                    } else if (locationPickerRv.adapter is LocationCityPickerAdapter) {
                        (locationPickerRv.adapter as LocationCityPickerAdapter)
                            .markSelectedCity(it ?: "")
                    }

                }
                viewModel.tabIsSelected.collectLatest(viewLifecycleOwner) {
                    if (it) {
                        locationPickerRv.post {
                            locationPickerRv.scrollToPosition(
                                (locationPickerRv.adapter as? LocationPickerAdapter)
                                    ?.getSelectedItemPosition() ?: 0
                            )
                        }
                    }
                }
            }
        }

        private fun populateList(
            items: Triple<List<String>, List<LocationRecyclerChildItem>, List<LocationRecyclerParentItem>>,
            type: LocationPickerType
        ) = with(binding) {
            locationPickerRv.layoutManager = FlexboxLayoutManager(requireContext())
            val (_, favHouses, allHouses) = items
            locationPickerRv.adapter =
                LocationPickerAdapter(favHouses, allHouses, this@LocationListFragment, type)
        }

        private fun populateListOfCity(cityItems: List<LocationCityItem>) = with(binding) {
            locationPickerRv.layoutManager = FlexboxLayoutManager(requireContext())
            locationPickerRv.adapter =
                LocationCityPickerAdapter(cityItems, this@LocationListFragment)
        }

        override fun onLocationClicked(selectedLocations: List<String>) {

            setFragmentResult(
                REQ_KEY_PICK_LOCATION, bundleOf(
                    EXTRA_SELECTED_LOCATION to selectedLocations as ArrayList,
                    PICKER_TYPE to pickerType
                )
            )
        }
    }

    class LocationListPagerAdapter(
        frag: Fragment,
        private val pickerTypes: List<LocationPickerType>,
        private val onLocationSelected: (id: List<String>) -> Unit
    ) : FragmentStateAdapter(frag) {

        override fun getItemCount(): Int {
            return pickerTypes.size
        }

        override fun createFragment(position: Int): Fragment {
            return LocationListFragment.newInstance(pickerTypes[position])
                .withResultListener(LocationListFragment.REQ_KEY_PICK_LOCATION) { _, bundle ->
                    onLocationSelected(
                        bundle.getStringArrayList(LocationListFragment.EXTRA_SELECTED_LOCATION)
                            ?.toList() ?: emptyList()
                    )
                }
        }
    }

}

