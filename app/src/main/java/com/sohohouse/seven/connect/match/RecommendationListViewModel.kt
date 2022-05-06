package com.sohohouse.seven.connect.match

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.Filtering
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.home.suggested_people.SuggestedPeopleAdapterItem
import com.sohohouse.seven.home.suggested_people.getSuggestedPeopleAdapterItem
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecommendationListViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    private val connectionRepository: ConnectionRepository,
    private val sohoApiService: SohoApiService,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val filterManager: FilterManager,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Filtering by RecommendedPeopleFilter(filterManager, analyticsManager) {

    val suggestedUsersLiveData = MutableLiveData<List<SuggestedPeopleAdapterItem>>()
    val openProfile = LiveEvent<ProfileItem>()

    init {
        analyticsManager.logEventAction(AnalyticsManager.Action.ConnectDiscoverMember)
        filters.observeForever {
            getData()
        }
    }

    fun clearFilters() {
        clearFiltersAndRefresh()
    }

    private fun getData() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            val recommendations = sohoApiService.getRecommendations(
                industry = filterManager.get(FilterType.INDUSTRY_FILTER).map { it.id },
                city = filterManager.get(FilterType.CITY_FILTER).map { it.id },
                interests = filterManager.get(FilterType.TOPIC_FILTER).map { it.id }
            )
            when (recommendations) {
                is ApiResponse.Error -> {
                    showError(recommendations.code.toString())
                }
                is ApiResponse.Success -> {
                    val list = recommendations.response.map {
                        it.getSuggestedPeopleAdapterItem()
                    }
                    suggestedUsersLiveData.postValue(list)
                }
            }
            setIdle()
        }
    }

    fun getMemberProfile(memberID: String) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            profileRepository.getProfile(memberID)
                .ifValue {
                    val profile = ProfileItem(
                        it, MutualConnectionStatus.from(
                            it,
                            userManager.profileID,
                            connectionRepository.blockedMembers.value
                        )
                    )
                    openProfile.postValue(profile)
                    setIdle()
                }.ifError {
                    showError()
                    setIdle()
                }
        }
    }
}
