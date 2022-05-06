package com.sohohouse.seven.connect.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModelImpl
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

class RecommendationsOptInViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _saveState = MutableLiveData(false)
    val saveState: LiveData<Boolean> get() = _saveState

    private lateinit var profile: Profile

    init {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            profileRepo.getMyAccountWithProfileV2().fold(
                ifValue = { account -> account.profile?.let(::profile::set) },
                ifEmpty = {},
                ifError = { handleError(it) }
            )
        }
    }

    fun saveProfile() {
        if (!this::profile.isInitialized) return

        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            profileRepo.saveProfileWithAccountUpdateV2(
                profile.copy(connectRecommendationOptIn = DateTime.now().toString()), null
            ).fold(
                ifValue = { _saveState.postValue(true) },
                ifEmpty = {},
                ifError = { handleError(it) }
            )
        }
    }
}