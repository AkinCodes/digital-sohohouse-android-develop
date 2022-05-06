package com.sohohouse.seven.perks.filter

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.CoroutineContextProvider
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.network.core.models.City
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class BenefitsFilterCityViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val citiesRepository: CitiesRepository,
    private val dispatchers: CoroutineContextProvider,
    private val benefitsFilterManager: BenefitsFilterManager,
    private val localVenueProvider: LocalVenueProvider
) : BaseViewModel(analyticsManager),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _items: MutableLiveData<List<DiffItem>> = MutableLiveData()
    val items: LiveData<List<DiffItem>> get() = _items

    private var allCities: ArrayList<City> = ArrayList()
    private var selectedCities: ArrayList<City> = ArrayList()
    private var expandedRegions: ArrayList<CityRegion> = ArrayList()

    private val onResetClick: () -> Unit = {
        selectedCities.clear()
        processCitiesData(allCities)
    }

    private val onCityClick: (item: CityItem) -> Unit = { item ->
        val city = allCities.first { item.id == it.id }
        if (selectedCities.contains(city)) {
            selectedCities.remove(city)
        } else {
            selectedCities.add(city)
        }
        processCitiesData(allCities)
    }

    private val onRegionClick: (item: RegionItem) -> Unit = { item ->
        val region = item.region
        if (expandedRegions.contains(region)) {
            expandedRegions.remove(region)
        } else {
            expandedRegions.add(region)
        }
        processCitiesData(allCities)
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(dispatchers.IO) {
            setLoadingState(LoadingState.Loading)
            val venue = localVenueProvider.localVenue.value
            CityRegion.forCode(venue?.region ?: "")?.let { expandedRegions = arrayListOf(it) }
            fetchCities()
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun fetchCities() {
        setLoadingState(LoadingState.Loading)
        viewModelScope.launch(dispatchers.IO) {
            citiesRepository.getCities().fold(
                ifError = {
                    showErrorView()
                },
                ifValue = { allCities ->
                    this@BenefitsFilterCityViewModel.allCities = ArrayList(allCities)
                    selectedCities =
                        ArrayList(benefitsFilterManager.citiesFiltered.map { selectedCity -> allCities.first { it.id == selectedCity } })
                    processCitiesData(allCities)
                },
                ifEmpty = {
                    showErrorView()
                }
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun processCitiesData(cities: List<City>) {

        val items = ArrayList<DiffItem>()

        items.add(
            HeaderItem(
                R.string.perks_cilter_filter_header,
                R.string.perks_cilter_filter_subheader,
                onResetClick
            )
        )

        selectedCities.iterator().forEach { selectedCity: City ->
            items.add(
                CityItem(
                    selectedCity.id,
                    selectedCity.name,
                    activated = false,
                    showRemoveBtn = true,
                    onCityClick = onCityClick
                )
            )
        }

        val regions = cities
            .asSequence()
            .map { it.region }
            .distinct()
            .map { CityRegion.forCode(it) }
            .filterNotNull()
            .sortedBy { it.ordinal }
            .toList()

        regions.forEach { region ->
            val regionName = region.label
            val isExpanded = expandedRegions.contains(region)
            val regionCities =
                cities.filter { city -> city.region.equals(region.name, ignoreCase = true) }
            val selectedCount = regionCities.intersect(selectedCities).count()
            items.add(RegionItem(region, regionName, selectedCount, isExpanded, onRegionClick))

            if (isExpanded) {
                regionCities.forEach { city ->
                    val isSelected =
                        selectedCities.any { selectedCity -> selectedCity.name == city.name }
                    items.add(
                        CityItem(
                            city.id,
                            city.name,
                            isSelected,
                            showRemoveBtn = false,
                            onCityClick = onCityClick
                        )
                    )
                }
            }
        }

        _items.postValue(items)
    }


    override fun reloadDataAfterError() {
        fetchData()
    }

    fun onConfirmClick() {
        benefitsFilterManager.citiesFiltered = selectedCities.map { it.id }
    }
}

enum class CityRegion {
    UK,
    EUROPE,
    NORTH_AMERICA,
    WORLDWIDE;

    val label: Int
        get() = when (this) {
            UK -> R.string.region_label_uk
            EUROPE -> R.string.region_label_europe
            NORTH_AMERICA -> R.string.region_label_north_america
            WORLDWIDE -> R.string.label_worldwide
        }

    companion object {
        fun forCode(code: String): CityRegion? {
            return code.asEnumOrDefault<CityRegion>()
        }
    }
}

data class HeaderItem(
    @StringRes val primaryText: Int,
    @StringRes val secondaryText: Int,
    val onResetClick: () -> Unit
) : DiffItem {
    override val key: Any?
        get() = javaClass
}

data class CityItem(
    val id: String,
    val name: String,
    val activated: Boolean,
    val showRemoveBtn: Boolean,
    val onCityClick: (item: CityItem) -> Unit
) : DiffItem {
    override val key: Any?
        get() = id
}

data class RegionItem(
    val region: CityRegion,
    @StringRes val name: Int,
    val selectedCount: Int,
    val expanded: Boolean,
    val onRegionClick: (region: RegionItem) -> Unit
) : DiffItem {
    override val key: Any?
        get() = region
}
