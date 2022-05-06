package com.sohohouse.seven.more.membershipdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.network.core.models.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MoreMembershipDetailsViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _userMembership: MutableLiveData<Account> = MutableLiveData()
    val userMembership: LiveData<Account>
        get() = _userMembership

    private val _url: MutableLiveData<String> = MutableLiveData("")
    val url: LiveData<String>
        get() = _url

    fun trackWhenStarted(mode: Mode?) {
        if (mode == Mode.CARD_ONLY) {
            analyticsManager.logEventAction(AnalyticsManager.Action.MembershipCardShake)
        }
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.MembershipDetails.name)
    }

    fun fetchAccount() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            accountInteractor.getCompleteAccountV2().fold(
                ifError = {
                    Timber.d(it.toString())
                    showError()
                },
                ifValue = { _userMembership.postValue(it) },
                ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

}