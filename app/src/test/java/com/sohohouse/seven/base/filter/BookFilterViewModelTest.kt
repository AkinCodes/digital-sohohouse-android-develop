package com.sohohouse.seven.base.filter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.book.filter.BookFilterViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.util.*

@ExperimentalCoroutinesApi
class BookFilterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(20)

    @MockK
    lateinit var profileRepo: ProfileRepository

    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var filterStorageManager: BookFilterManager

    @MockK
    lateinit var categoryManager: ExploreCategoryManager

    @MockK
    lateinit var venueRepo: VenueRepo

    @MockK
    lateinit var houseManager: HouseManager

    lateinit var viewModel: BookFilterViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { categoryManager.getCategories() } returns Single.just(value(emptyList()))

        viewModel = BookFilterViewModel(
            analyticsManager,
            filterStorageManager,
            categoryManager,
            testCoroutineRule.testCoroutineDispatcher,
            venueRepo,
            houseManager
        )
    }

    @Test
    fun `clear button clears the selection`() {
        every { filterStorageManager.getDefaultSelection() } returns emptyList()
        // GIVEN the viewModel has selected locations
        viewModel.filterType = FilterType.LOCATION
        viewModel.eventType = EventType.MEMBER_EVENT
        viewModel.updateLocationSelection(mutableListOf("0", "1", "2"))

        // WHEN the clear button is pressed
        viewModel.resetToDefaultSelection()

        // THEN the viewModel has nothing selected
        Assert.assertEquals(0, viewModel.draftFilter.selectedLocationList?.size)
    }

    @Test
    fun `update the selected item list updates the list`() {
        // GIVEN the viewModel has some selected locations
        viewModel.updateLocationSelection(mutableListOf("0", "1"))

        // WHEN the location selection is updated
        val updatedList = mutableListOf("2", "3", "4")
        viewModel.updateLocationSelection(updatedList)

        // THEN the viewModel has updated selected list
        Assert.assertEquals(updatedList, viewModel.draftFilter.selectedLocationList)
    }

    @Test
    fun `update the selected date updates the value`() {
        // GIVEN the viewModel has some getIntent date stored
        val calendar = Calendar.getInstance()
        calendar.set(2000, 2, 25)
        viewModel.updateDateSelection(calendar.time, true)

        // WHEN the date selection is updated
        calendar.set(2000, 2, 26)
        viewModel.updateDateSelection(calendar.time, true)

        // THEN the viewModel has updated selected date
        Assert.assertEquals(viewModel.draftFilter.selectedStartDate, calendar.time)
    }

    @Test
    fun `update the selected categories updates the value`() {
        // GIVEN the viewModel has some categories stored
        viewModel.updateCategorySelection(listOf("zoo", "sharks"))

        // WHEN the categories selection is updated
        val list = listOf("zoo", "sharks", "clause")
        viewModel.updateCategorySelection(list)

        // THEN the viewModel has updated selected categories
        Assert.assertEquals(list, viewModel.draftFilter.selectedCategoryList)
    }

    @Test
    fun `update filter type changes the filter type and updates the view`() {
        // GIVEN the viewModel has some filter type
        viewModel.filterType = FilterType.CATEGORIES

        // WHEN the filter type is updated
        viewModel.updateFilterType(FilterType.LOCATION)

        // THEN the filter type is updated and the view is updated
        Assert.assertEquals(FilterType.LOCATION, viewModel.filterType)
    }
}