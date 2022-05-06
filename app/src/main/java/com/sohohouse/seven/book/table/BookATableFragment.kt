package com.sohohouse.seven.book.table

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.table.BookATableViewModel.State.*
import com.sohohouse.seven.book.table.model.SelectedLocation
import com.sohohouse.seven.book.table.timeslots.TableTimeSlotsActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.Backable
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.databinding.FragmentBookATableBinding
import com.sohohouse.seven.main.MainNavigationController
import java.util.*

class BookATableFragment : BaseMVVMFragment<BookATableViewModel>(), Loadable.View, Backable,
    TableSearchFormView.Host, TableSearchFormView.CityHost {
    override val viewModelClass: Class<BookATableViewModel>
        get() = BookATableViewModel::class.java

    private val adapter = SelectRestaurantAdapter()

    private val houseAdapter = RestaurantListAdapter()
    private val restaurantAdapter = RestaurantListAdapter()
    private val alternateRestaurantAdapter = AlternativeRestaurantListAdapter()

    private var navigatingToTimeSlots: Boolean = false
    private var isAlternateRestaurantSelected: Boolean = false

    private val binding by viewBinding(FragmentBookATableBinding::bind)

    override val contentLayoutId get() = R.layout.fragment_book_a_table

    private val tableTimeSlotsLauncher = registerForActivityResult(StartActivityForResult()) {
        val intent = it.data
        if (it.resultCode == Activity.RESULT_CANCELED && intent != null) {
            val revertToSelectRestaurantState =
                intent.getBooleanExtra(BundleKeys.IS_SELECT_RESTAURANT_STATE, false)
            if (isAlternateRestaurantSelected) viewModel.onAlternateRestaurantClick()
            else if (revertToSelectRestaurantState) viewModel.revertToSelectRestaurantState()
        } else if (it.resultCode == TableTimeSlotsActivity.RESULT_SEARCH_EDITED && intent != null) {
            val input =
                intent.getParcelableExtra<TableSearchFormView.Input>(EditTableSearchBottomSheet.EXTRA_INPUT)
            if (input != null) {
                if (input.venueIds != null)
                    viewModel.fillCityLocations(input.venueIds ?: emptyList())
                else
                    viewModel.fillLocation(input.venueID)

                viewModel.fillDate(input.year, input.month, input.day)
                viewModel.fillTime(input.hour, input.minute)
                viewModel.fillSeats(input.seats)
                viewModel.checkAvailability()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            searchForm.host = this@BookATableFragment
            searchForm.cityHost = this@BookATableFragment

            btnCheckAvailability.setOnClickListener { onCheckAvailabilityClick() }
            btnEditSearch.setOnClickListener { onEditSearchClick() }
            btnChangeSearch.setOnClickListener { onChangeSearchClick() }
            btnContactUs.setOnClickListener { onContactUsClick() }

            restaurantAdapter.bookListener = this@BookATableFragment::onRestaurantBookClick
            restaurantAdapter.clickListener = this@BookATableFragment::onRestaurantClick
            listRestaurants.adapter = restaurantAdapter
            listRestaurants.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)


            houseAdapter.bookListener = this@BookATableFragment::onRestaurantBookClick
            houseAdapter.clickListener = this@BookATableFragment::onRestaurantClick
            listHouses.adapter = houseAdapter
            listHouses.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            alternateRestaurantAdapter.clickListener =
                this@BookATableFragment::onAlternateRestaurantClick
            listAlternateRestaurants.adapter = alternateRestaurantAdapter
            listAlternateRestaurants.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


            adapter.itemClick = this@BookATableFragment::onRestaurantSelectedClick
            listRestaurant.adapter = adapter
            listRestaurant.layoutManager = LinearLayoutManager(activity)
        }

        observeLoadingState(lifecycleOwner) { onLoadingStateChange(it) }

        viewModel.stateTransitions.observe(viewLifecycleOwner) { transitionToState(it) }

        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            showLocation(it)
        }
        viewModel.selectedDate.observe(viewLifecycleOwner) { showDate(it) }
        viewModel.selectedTime.observe(viewLifecycleOwner) { showTime(it) }
        viewModel.selectedSeats.observe(viewLifecycleOwner) { showSeats(it) }

        viewModel.isUserInputCorrect.observe(
            viewLifecycleOwner
        ) { enableCheckAvailabilityButton(it) }
        viewModel.venueRestaurants.observe(viewLifecycleOwner) { showRestaurants(it) }
        viewModel.listHouses.observe(viewLifecycleOwner) { showListHouses(it) }
        viewModel.listRestaurants.observe(viewLifecycleOwner) { showListRestaurants(it) }
        viewModel.listAlternateRestaurants.observe(
            viewLifecycleOwner
        ) { showAlternateRestaurants(it) }
        viewModel.openBookingDetails.observe(viewLifecycleOwner) { navigateToTimeSlots(it) }
    }

    override fun onStop() {
        if (navigatingToTimeSlots) {
            viewModel.onNavigatedToTimeSlots()  //called in onStop to avoid UI jankiness before new activity loads
            navigatingToTimeSlots = false
        }
        super.onStop()
    }

    private fun transitionToState(transition: BookATableViewModel.TransitionToStateEvent) {
        val showNewState = when (transition.state) {
            SEARCH_FORM -> showSearchFormState
            SELECT_RESTAURANT -> showSelectRestaurantState
            NO_AVAILABILITY -> showNoAvailabilityState
            SELECT_ALTERNATE_RESTAURANT -> showAlternateRestaurantAvailabilityState
        }
        with(binding) {
            if (transition.animate) {
                animate(
                    Slide(Gravity.START),
                    constraintLayout,
                    *searchFormState.referencedIds.plus(selectRestaurantState.referencedIds)
                        .plus(noAvailibilityState.referencedIds)
                ) {
                    showNewState()
                }
            } else {
                showNewState()
            }
        }
    }

    private val showSearchFormState = {
        with(binding) {
            selectRestaurantState.setVisible(false)
            noAvailibilityState.setVisible(false)
            searchFormState.setVisible(true)
        }
        toggleAlternateRestaurantList()
        toggleRestaurantsLists(viewModel.listRestaurants.value.isNullOrEmpty())
        toggleHousesLists(viewModel.listHouses.value.isNullOrEmpty())
        scrollTop()
    }

    private val showSelectRestaurantState = {
        with(binding) {
            searchFormState.setVisible(false)
            noAvailibilityState.setVisible(false)
            toggleAlternateRestaurantList()
            selectRestaurantState.setVisible(true)
        }
    }

    private val showNoAvailabilityState = {
        with(binding) {
            searchFormState.setVisible(false)
            selectRestaurantState.setVisible(false)
            noAvailibilityState.setVisible(true)
            titleNoAvailable.text =
                getString(R.string.book_a_table_no_available_tables_match_your_search)
            titleNoAvailableDescription.text = getString(R.string.book_a_table_no_any_tables)
        }
        toggleAlternateRestaurantList()
        toggleRestaurantsLists(viewModel.listRestaurants.value.isNullOrEmpty())
        toggleHousesLists(viewModel.listHouses.value.isNullOrEmpty())
        scrollTop()
    }

    private val showAlternateRestaurantAvailabilityState = {
        with(binding) {
            searchFormState.setVisible(false)
            selectRestaurantState.setVisible(false)
            noAvailibilityState.setVisible(false)
            alternateRestaurantAvailabilityState.setVisible(true)
            toggleRestaurantsLists(true)
            toggleHousesLists(true)
            titleNoAvailable.text = getString(R.string.no_available_tables)
            titleNoAvailableDescription.text = getString(R.string.try_from_alternate_restaurants)
            scrollTop()
        }
    }

    private fun toggleAlternateRestaurantList() {
        if (binding.listAlternateRestaurants.isVisible) binding.listAlternateRestaurants.visibility =
            View.GONE
    }

    private fun onLoadingStateChange(loadingState: LoadingState) =
        with(binding) {
            searchForm.setDirectChildrenEnabled(
                loadingState != LoadingState.Loading,
                changeAlpha = true
            )
            when (loadingState) {
                LoadingState.Idle -> {
                    btnCheckAvailability.setLoading(
                        false,
                        enabled = viewModel.isUserInputCorrect.value ?: false
                    )
                }
                LoadingState.Loading -> {
                    btnCheckAvailability.setLoading(true)
                }
            }
            (requireActivity() as MainNavigationController).setLoadingState(loadingState)
        }

    override fun onBackPressed(): Boolean {
        return viewModel.backPressed()
    }

    private fun showLocation(venue: SelectedLocation) =
        with(binding) {
            searchForm.setVenue(venue.id, venue.name)
            descriptionSelectRestaurant.text =
                getString(
                    R.string.book_a_table_availability_which_restaurant,
                    venue.name
                )
        }

    private fun showDate(date: Date) {
        val (year, month, day) = date.yearMonthDay
        binding.searchForm.setDate(
            year = year,
            month = month,
            dayOfMonth = day
        )
    }

    private fun showTime(time: Date) {
        val (hour, minute) = time.hourMinute
        binding.searchForm.setTime(
            hourOfDay = hour,
            minute = minute
        )
    }

    private fun showSeats(guests: Int) {
        binding.searchForm.setSeats(guests)
    }

    private fun enableCheckAvailabilityButton(isEnable: Boolean) {
        binding.btnCheckAvailability.isEnabled = isEnable
    }

    private fun onEditSearchClick() {
        viewModel.onChangeSearchClick()
    }

    private fun onChangeSearchClick() {
        if (binding.listAlternateRestaurants.isVisible) viewModel.onAlternateRestaurantsSuggestionChangeClicked()
        viewModel.onChangeSearchClick()
    }

    private fun onRestaurantSelectedClick(id: String) {
        viewModel.checkAvailabilityForHouse(id)
    }

    private fun scrollTop() {
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun toggleRestaurantsLists(hide: Boolean) =
        with(binding) {
            titleRestaurants.setVisible(!hide && viewModel.showRestaurantCarousels)
            listRestaurants.setVisible(!hide && viewModel.showRestaurantCarousels)
        }

    private fun toggleHousesLists(hide: Boolean) =
        with(binding) {
            titleHouses.setVisible(!hide && viewModel.showRestaurantCarousels)
            listHouses.setVisible(!hide && viewModel.showRestaurantCarousels)
        }

    private fun showAlternateRestaurants(items: List<TableBookingDetails>) {
        alternateRestaurantAdapter.submitList(items)
    }

    private fun onAlternateRestaurantClick(
        tableBookingDetails: TableBookingDetails,
        isSelectRestaurantState: Boolean
    ) {
        tableTimeSlotsLauncher.launch(
            TableTimeSlotsActivity.newIntent(
                requireContext(),
                tableBookingDetails,
                isSelectRestaurantState
            )
        )
        navigatingToTimeSlots = true
        isAlternateRestaurantSelected = true
        viewModel.onAlternateRestaurantsSuggestionItemClicked(tableBookingDetails.id)
    }

    private fun showListRestaurants(items: List<Restaurant>) {
        toggleRestaurantsLists(items.isEmpty())
        restaurantAdapter.fill(items)
    }

    private fun showListHouses(items: List<Restaurant>) {
        toggleHousesLists(items.isEmpty())
        houseAdapter.fill(items)
    }

    private fun onCheckAvailabilityClick() {
        viewModel.checkAvailability()
    }

    private fun onContactUsClick() {
        openWebView(parentFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

    private fun navigateToTimeSlots(pair: Pair<TableBookingDetails, Boolean>) {
        val (details, isSelectRestaurantState) = pair
        tableTimeSlotsLauncher.launch(
            TableTimeSlotsActivity.newIntent(
                requireContext(),
                details,
                isSelectRestaurantState
            )
        )
        navigatingToTimeSlots = true
        isAlternateRestaurantSelected = false
    }

    private fun onRestaurantBookClick(item: Restaurant) {
        viewModel.fillLocation(item)
        scrollTop()
    }

    private fun onRestaurantClick(item: Restaurant) {
        WebViewBottomSheetFragment.withUrl(item.restaurantUrl).show(parentFragmentManager, "")
    }

    private fun showRestaurants(items: List<Restaurant>) {
        adapter.fill(items)
    }

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.fillDate(year, month, dayOfMonth)
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        viewModel.fillTime(hourOfDay, minute)
    }

    override fun onVenueSet(venueID: String, venueName: String) {
        viewModel.fillLocation(venueID)
    }

    override fun onCitySet(cityName: String, venueIds: List<String>) {
        viewModel.fillCityLocations(venueIds)
    }

    override fun onSeatsSet(seats: Int) {
        viewModel.fillSeats(seats)
    }

    override val _fragmentManager: FragmentManager
        get() = parentFragmentManager
}