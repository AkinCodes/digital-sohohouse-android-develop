package com.sohohouse.seven.apponboarding.optinrecommendations

import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

class LandingOptInRecommendationsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private var isOptInToggleOn = false
        set(value) {
            analyticsManager.logEventAction(
                AnalyticsManager.Action.ConnectRecommendationOptInToggle,
                bundleOf("enabled" to value)
            )
            field = value
        }
    private val nextButton = ActionButtons.Next
    private val continueButton = ActionButtons.Continue
    private var _selectedPage =
        MutableStateFlow<LandingOptInPageData>(LandingOptInPageData.UpdateYourProfile)
    private val selectedPage: LandingOptInPageData get() = _selectedPage.value
    val pages: List<LandingOptInPageData> = listOf(
        LandingOptInPageData.UpdateYourProfile,
        LandingOptInPageData.PersonalizedRecommendations(::isOptInToggleOn::set),
        LandingOptInPageData.ContinueWithMoreMembers(::finish)
    )


    val finish = LiveEvent<Unit>()
    private val _showOptOutDialog = Channel<AlertDialogActions>()
    val showOptOutDialog = _showOptOutDialog.receiveAsFlow()
    val actionButtonsState = _selectedPage.map {
        when (it) {
            is LandingOptInPageData.ContinueWithMoreMembers -> continueButton
            is LandingOptInPageData.PersonalizedRecommendations -> nextButton
            LandingOptInPageData.UpdateYourProfile -> nextButton
        }
    }
    val selectedPageIndex = _selectedPage.map { pages.indexOf(it) }

    fun selectPage(landingOptInPageData: LandingOptInPageData) {
        _selectedPage.value = landingOptInPageData
    }

    fun selectNextPage() {
        if (selectedPage is LandingOptInPageData.PersonalizedRecommendations && isOptInToggleOn.not()) {
            viewModelScope.launch {
                _showOptOutDialog.send(createAlertDialogActions())
            }
        } else if (selectedPage is LandingOptInPageData.PersonalizedRecommendations && isOptInToggleOn) {
            updateOptInProfileState()
        } else {
            nextPage()
        }
    }

    fun logOnContinue() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.ConnectRecommendationOnBoardingEditProf,
            bundleOf("current_page" to 3)
        )
    }

    private fun createAlertDialogActions() = AlertDialogActions(
        first = {
            analyticsManager.logEventAction(AnalyticsManager.Action.ConnectRecommendationOptInConfirm)
            updateOptInProfileState()
        },
        second = {
            analyticsManager.logEventAction(AnalyticsManager.Action.ConnectRecommendationOptInCancel)
            updateOptInProfileState(false)
        }
    )

    private fun updateOptInProfileState(goOptIn: Boolean = true) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()

            profileRepository.getMyProfile().ifValue { oldProfile ->
                val profile = oldProfile.apply {
                    connectRecommendationOptIn = if (goOptIn)
                        DateTime.now().toString()
                    else
                        ""
                }
                profileRepository.saveProfileWithAccountUpdateV2(profile, null)
                    .ifValue { nextPage() }
                    .ifError(::handleError)
            }.ifError(::handleError)

            setIdle()
        }
    }

    private fun nextPage() {
        val currentPage = when (selectedPage) {
            is LandingOptInPageData.ContinueWithMoreMembers -> 2
            is LandingOptInPageData.PersonalizedRecommendations -> 2
            LandingOptInPageData.UpdateYourProfile -> 1
        }
        _selectedPage.value = pages[currentPage]

        analyticsManager.logEventAction(
            AnalyticsManager.Action.ConnectConnectionOnBoardingNext,
            bundleOf("current_page" to currentPage + 1)
        )
    }

    private fun finish() {
        finish.postValue(Unit)
    }

    fun selectPageFromIndex(position: Int) {
        _selectedPage.value = pages[position]
    }

}