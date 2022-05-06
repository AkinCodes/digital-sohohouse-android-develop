package com.sohohouse.seven.more

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModelImpl
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import javax.inject.Inject

class SettingsActivityViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    val shouldShowPrivacyTab = userManager.subscriptionType != SubscriptionType.FRIENDS

}