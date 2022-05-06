package com.sohohouse.seven.perks.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModelImpl
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.utils.MembershipUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.MembershipCardView.Membership
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class MembershipCardViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    private val _membership: MutableLiveData<Membership> = MutableLiveData()
    val membership: LiveData<Membership>
        get() = _membership

    fun getMembershipInfo() {
        viewModelScope.launch(viewModelContext) {
            accountInteractor.getCompleteAccountV2().fold(
                ifError = { showErrorView() },
                ifEmpty = { showErrorView() },
                ifValue = { account ->
                    _membership.postValue(
                        Membership(
                            subscriptionType = account.subscriptionType,
                            membershipDisplayName = account.membershipDisplayName,
                            membershipId = MembershipUtils.formatMembershipNumber(account.id),
                            shortCode = account.shortCode,
                            memberName = stringProvider.getString(R.string.more_membership_name_label)
                                .replaceBraces(account.firstName ?: "", account.lastName ?: ""),
                            profileImageUrl = account.imageUrl,
                            loyaltyId = account.loyaltyId,
                            isStaff = account.isStaff
                        )
                    )
                })
        }
    }
}