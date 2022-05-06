package com.sohohouse.seven.more.synccalendar

import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.user.UserManager
import javax.inject.Inject

class SyncCalendarPresenter @Inject constructor(
    private val userManager: UserManager, private val analyticsManager: AnalyticsManager,
    private val trackingUtil: AnalyticsManager
) : BasePresenter<SyncCalendarViewController>() {

    override fun onAttach(
        view: SyncCalendarViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.CalendarSync.name)
    }

    fun onIntentDataReceived(showToolbar: Boolean, showContinue: Boolean) {
        executeWhenAvailable { view, _, _ ->
            if (showToolbar) {
                view.showToolbar()
            } else {
                view.showTitleView()
            }
        }
        if (showContinue) {
            executeWhenAvailable { view, _, _ ->
                view.showContinueButton()
            }
        }
    }

    fun onCopyClicked() {
        analyticsManager.track(AnalyticsEvent.MemberOnBoarding.Calendar)
        executeWhenAvailable { view, _, _ ->
            view.copyTextToClipboard(CLIBOARD_LABEL, userManager.calendarSubscriptionUrl)
            view.showSnackBar()
        }
    }

    fun onContinueClicked() {
        executeWhenAvailable { view, _, _ -> view.navigateToNextOnboardingAcitivty() }
    }

    companion object {
        private const val CLIBOARD_LABEL = "Calendar Subscription Url"
    }
}