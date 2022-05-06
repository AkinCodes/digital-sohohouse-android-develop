package com.sohohouse.seven.more.privacy

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModelImpl
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

class PrivacySettingsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private lateinit var profile: Profile
    private val _isOptedIn = MutableStateFlow(false)
    val isOptedIn: Flow<Boolean> = _isOptedIn


    init {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            profileRepository.getMyProfile().ifValue {
                profile = it
                _isOptedIn.value = it.connectRecommendationOptIn?.isNotEmpty() == true
                setIdle()
            }.ifError {
                handleError(it)
                setIdle()
            }
        }
    }

    fun optIn(isChecked: Boolean) {
        if (isChecked == _isOptedIn.value || ::profile.isInitialized.not()) return

        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            profile = if (profile.connectRecommendationOptIn.isNullOrEmpty())
                profile.copy(connectRecommendationOptIn = DateTime().toString())
            else
                profile.copy(connectRecommendationOptIn = "")

            setLoading()
            profileRepository.saveProfileWithAccountUpdateV2(profile, null).ifValue {
                _isOptedIn.value = profile.connectRecommendationOptIn?.isNotEmpty() == true
                setIdle()
            }.ifError {
                _isOptedIn.value = isChecked.not()
                handleError(it)
                setIdle()
            }
        }
    }

}