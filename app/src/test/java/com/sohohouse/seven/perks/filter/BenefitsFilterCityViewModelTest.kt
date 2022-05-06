package com.sohohouse.seven.perks.filter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.firstValue
import com.nhaarman.mockito_kotlin.secondValue
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.R
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.captor
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.utils.TestCoroutineContextProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.City
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class BenefitsFilterCityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var citiesRepository: CitiesRepository
    private val dispatchers = TestCoroutineContextProvider()

    @RelaxedMockK
    lateinit var benefitsFilterManager: BenefitsFilterManager

    @MockK
    lateinit var houseManager: HouseManager

    @MockK
    lateinit var localVenueProvider: LocalVenueProvider

    private lateinit var viewModel: BenefitsFilterCityViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        val venue = Venue(_region = "UK")
        every { localVenueProvider.localVenue } returns MutableLiveData(venue)
        every { citiesRepository.getCities() } returns value(
            listOf(City(name = "London").apply { id = "London"; region = "UK" },
                City(name = "Paris").apply { id = "London"; region = "EUROPE" })
        )
    }

    @Test
    fun `viewModel emits expected initial items when no filters`() {
        every { benefitsFilterManager.citiesFiltered } returns emptyList()

        viewModel = BenefitsFilterCityViewModel(
            analyticsManager,
            citiesRepository,
            dispatchers,
            benefitsFilterManager,
            localVenueProvider
        )

        val observer = mock<Observer<List<DiffItem>>>().also {
            viewModel.items.observeForever(it)
        }
        val captor = captor<List<DiffItem>>()
        verify(observer).onChanged(captor.capture())
        val items = captor.firstValue
        assertTrue(items[0] is HeaderItem)
        assertTrue(items[1] is RegionItem && (items[1] as RegionItem).name == R.string.region_label_uk && (items[1] as RegionItem).expanded)
        assertTrue(items[2] is CityItem)
        assertTrue(items[3] is RegionItem && !(items[3] as RegionItem).expanded)
    }

    @Test
    fun `viewModel emits expected expected items when user selects city`() {
        every { benefitsFilterManager.citiesFiltered } returns emptyList()

        viewModel = BenefitsFilterCityViewModel(
            analyticsManager,
            citiesRepository,
            dispatchers,
            benefitsFilterManager,
            localVenueProvider
        )

        val observer = mock<Observer<List<DiffItem>>>().also {
            viewModel.items.observeForever(it)
        }

        val london = viewModel.items.value!!.get(2) as CityItem
        london.onCityClick(london)

        val captor = captor<List<DiffItem>>()

        verify(observer, times(2)).onChanged(captor.capture())
        val items = captor.secondValue
        assertTrue(items[0] is HeaderItem)
        assertTrue(items[1] is CityItem && (items[1] as CityItem).name == "London" && (items[1] as CityItem).showRemoveBtn)
        assertTrue(items[2] is RegionItem && (items[2] as RegionItem).name == R.string.region_label_uk && (items[2] as RegionItem).expanded)
        assertTrue(items[3] is CityItem)
        assertTrue(items[4] is RegionItem && !(items[4] as RegionItem).expanded)
    }

    @Test
    fun `viewModel emits expected expected items when user expands region`() {
        every { benefitsFilterManager.citiesFiltered } returns emptyList()

        viewModel = BenefitsFilterCityViewModel(
            analyticsManager,
            citiesRepository,
            dispatchers,
            benefitsFilterManager,
            localVenueProvider
        )

        val observer = mock<Observer<List<DiffItem>>>().also {
            viewModel.items.observeForever(it)
        }

        val europe = viewModel.items.value!!.get(3) as RegionItem
        europe.onRegionClick(europe)

        val captor = captor<List<DiffItem>>()

        verify(observer, times(2)).onChanged(captor.capture())
        val items = captor.secondValue
        assertTrue(items[0] is HeaderItem)
        assertTrue(items[1] is RegionItem && (items[1] as RegionItem).name == R.string.region_label_uk && (items[1] as RegionItem).expanded)
        assertTrue(items[2] is CityItem)
        assertTrue(items[3] is RegionItem && (items[3] as RegionItem).expanded)
        assertTrue(items[4] is CityItem)
    }

    @Test
    fun `viewModel emits expected initial items when filters present`() {
        every { benefitsFilterManager.citiesFiltered } returns listOf("London")

        viewModel = BenefitsFilterCityViewModel(
            analyticsManager,
            citiesRepository,
            dispatchers,
            benefitsFilterManager,
            localVenueProvider
        )

        val observer = mock<Observer<List<DiffItem>>>().also {
            viewModel.items.observeForever(it)
        }
        val captor = captor<List<DiffItem>>()
        verify(observer).onChanged(captor.capture())
        val items = captor.firstValue
        assertTrue(items[0] is HeaderItem)
        assertTrue(items[1] is CityItem && (items[1] as CityItem).name == "London" && (items[1] as CityItem).showRemoveBtn)
        assertTrue(items[2] is RegionItem && (items[2] as RegionItem).name == R.string.region_label_uk && (items[2] as RegionItem).expanded)
        assertTrue(items[3] is CityItem)
        assertTrue(items[4] is RegionItem && !(items[4] as RegionItem).expanded)
    }

}