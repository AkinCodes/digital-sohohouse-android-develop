package com.sohohouse.seven.profile.share

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShareProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    private val dispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    val shortProfileUrl = profileRepository.shortProfileUrl

    init {
        loadShortProfileUrl()
    }

    private fun loadShortProfileUrl() {
        if (TextUtils.isEmpty(profileRepository.shortProfileUrl.value.shortUrl)) {
            setLoadingState(LoadingState.Loading)
            viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
                profileRepository.loadShortProfileUrl(userManager.profileID)
            }.invokeOnCompletion {
                setLoadingState(LoadingState.Idle)
            }
        }
    }

    fun logAnalyticsActions(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(action)
    }

}