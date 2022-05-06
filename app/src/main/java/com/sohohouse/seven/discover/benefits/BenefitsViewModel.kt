package com.sohohouse.seven.discover.benefits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.DataSource.Factory
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.Config.Builder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.discover.benefits.adapter.PerksItem
import com.sohohouse.seven.network.core.request.GetPerksRequest
import com.sohohouse.seven.perks.filter.CitiesRepository
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class BenefitsViewModel @Inject constructor(
    private val dataSourceFactory: BenefitsDataSourceFactory,
    private val userManager: UserManager,
    private val featureFlags: FeatureFlags,
    private val benefitsFilterManager: BenefitsFilterManager,
    private val citiesRepository: CitiesRepository,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by dataSourceFactory,
    Errorable.ViewModel by dataSourceFactory {

    val benefits: LiveData<PagedList<PerksItem>> = LivePagedListBuilder(
        object : Factory<Int, PerksItem>() {
            override fun create(): DataSource<Int, PerksItem> = dataSourceFactory.create()
        },
        Builder().setEnablePlaceholders(false).setPageSize(GetPerksRequest.DEFAULT_PERKS_PER_PAGE)
            .build()
    ).build()

    private val _filters: MutableLiveData<List<DiffItem>> = MutableLiveData()
    val filters: LiveData<List<DiffItem>> get() = _filters

    private val onRemoveCityFilterClick = { city: String ->
        benefitsFilterManager.citiesFiltered =
            ArrayList(benefitsFilterManager.citiesFiltered).apply { remove(city) }
        invalidate()
    }

    init {
        loadCityFilters()
    }

    fun loadCityFilters() {
        if (featureFlags.benefitsFilterByCity) {
            invalidateCityFilters()
        }
    }

    fun invalidate() {
        dataSourceFactory.invalidate()
        if (featureFlags.benefitsFilterByCity) {
            invalidateCityFilters()
        }
    }

    private fun invalidateCityFilters() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            citiesRepository.getCities().fold(
                ifValue = { cities ->
                    _filters.postValue(ArrayList<DiffItem>().apply {
                        addAll(benefitsFilterManager.citiesFiltered.map { cityId ->
                            ActiveCityFilterItem(
                                cityId, cities.firstOrNull { it.id == cityId }?.name
                                    ?: "", onRemoveCityFilterClick
                            )
                        })
                    })
                },
                ifError = {
                    FirebaseCrashlytics.getInstance().log(it.toString())
                    showError()
                },
                ifEmpty = {})
            setLoadingState(LoadingState.Idle)
        }
    }

    fun logView() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.DiscoverPerks,
            AnalyticsManager.Perks.getParams(
                membershipType = userManager.membershipType,
                subscriptionType = userManager.subscriptionType.name
            )
        )
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.Perks.name)
    }

    fun trackEventPerksItem(id: String, title: String?, promoCode: String?) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.DiscoverOpenPerks,
            AnalyticsManager.Perks.getParams(
                id = id,
                title = title,
                promoCode = promoCode,
                membershipType = userManager.membershipType,
                subscriptionType = userManager.subscriptionType.name
            )
        )
    }

    fun isFilterByCityEnabled(): Boolean = featureFlags.benefitsFilterByCity
}

object FilterButtonItem : DiffItem {
    override val key: Any?
        get() = javaClass
}

data class ActiveCityFilterItem(
    val id: String,
    val name: String,
    val onRemoveClick: (city: String) -> Unit
) : DiffItem {
    override val key: Any?
        get() = name
}