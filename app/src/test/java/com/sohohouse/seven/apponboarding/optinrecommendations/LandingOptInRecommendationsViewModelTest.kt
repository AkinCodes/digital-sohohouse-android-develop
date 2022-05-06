package com.sohohouse.seven.apponboarding.optinrecommendations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LandingOptInRecommendationsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val profileRepo = mockk<ProfileRepository> {
        every { saveProfileWithAccountUpdateV2(any(), any()) } returns value(Unit)
        every { getMyProfile() } returns value(Profile())
    }
    private lateinit var viewModel: LandingOptInRecommendationsViewModel

    @Before
    fun before() {
        viewModel = LandingOptInRecommendationsViewModel(
            mockk(relaxed = true),
            profileRepo,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun testNextAndContinueButtonsVisibilityWithSelectingPage() = runBlocking {
        val tests = listOf(
            LandingOptInPageData.UpdateYourProfile to ActionButtons.Next::class,
            LandingOptInPageData.PersonalizedRecommendations {} to ActionButtons.Next::class,
            LandingOptInPageData.ContinueWithMoreMembers {} to ActionButtons.Continue::class,
        )
        toggleOptIn(true)

        tests.forEachIndexed { i, (data, expectedType) ->
            viewModel.selectPage(data)
            Assert.assertEquals(
                "Failed at index: $i",
                viewModel.actionButtonsState.first()::class,
                expectedType
            )
        }
    }


    @Test
    fun givenSelectPageFromIndexIsTriggeredTwiceContinueButtonShouldBecomeVisible() = runBlocking {
        toggleOptIn(true)

        viewModel.selectNextPage()
        viewModel.selectNextPage()

        Assert.assertEquals(
            viewModel.actionButtonsState.first()::class,
            ActionButtons.Continue::class
        )
    }

    @Test
    fun givenOptInSwitchIsOffAndUserSelectsNextPageTwiceOptOutDialogShouldAppear() = runBlocking {
        viewModel.selectNextPage()
        viewModel.selectNextPage()

        Assert.assertNotEquals(viewModel.showOptOutDialog.first(), null)
        Assert.assertEquals(viewModel.selectedPageIndex.first(), 1)
    }

    @Test
    fun givenUserSeesOptOutDialogAndClicksPositiveButtonSelectNextPage() = runBlocking {
        viewModel.selectNextPage()
        viewModel.selectNextPage()

        val positiveAction = viewModel.showOptOutDialog.first()
        positiveAction.first()

        Assert.assertEquals(viewModel.selectedPageIndex.first(), 2)
    }

    private fun toggleOptIn(b: Boolean) {
        val personalizedRecommendations = viewModel.pages.first {
            it is LandingOptInPageData.PersonalizedRecommendations
        }
        (personalizedRecommendations as LandingOptInPageData.PersonalizedRecommendations).onOptIn(b)
    }

}