package com.sohohouse.seven.more.privacy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PrivacySettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val profileRepository = mockk<ProfileRepository>()

    private val viewModel by lazy {
        PrivacySettingsViewModel(
            mockk(relaxed = true),
            profileRepository = profileRepository,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun givenProfileHasNotBeenInitializedThenTryingToOptInDoNotCallForSaveProfile() {
        every { profileRepository.getMyProfile() } returns Either.Error(ServerError.BAD_REQUEST)

        viewModel.optIn(true)

        verify(exactly = 0) { profileRepository.saveProfileWithAccountUpdate(any(), any()) }
    }

    @Test
    fun givenOptedInIsAlreadyTrueThenTryingToOptInWithTrueValueDoNotCallForSaveProfile() {
        every { profileRepository.getMyProfile() } returns Either.Value(
            Profile(connectRecommendationOptIn = DateTime().toString())
        )

        viewModel.optIn(true)

        verify(exactly = 0) { profileRepository.saveProfileWithAccountUpdateV2(any(), any()) }
    }

    @Test
    fun givenOptedIsTrueThenTryingToOptInWithFalseValueCallForSaveProfile() = runBlocking {
        coEvery { profileRepository.getMyProfile() } returns Either.Value(
            Profile(connectRecommendationOptIn = DateTime().toString())
        )
        coEvery {
            profileRepository.saveProfileWithAccountUpdateV2(
                any(),
                any()
            )
        } returns Either.Value(Any())

        viewModel.optIn(false)

        coVerify(exactly = 1) { profileRepository.saveProfileWithAccountUpdateV2(any(), any()) }
        assertEquals(false, viewModel.isOptedIn.first())
    }

    @Test
    fun givenOptedIsFalseThenTryingToOptInWithTrueValueCallForSaveProfile() = runBlocking {
        coEvery { profileRepository.getMyProfile() } returns Either.Value(
            Profile(connectRecommendationOptIn = "")
        )
        coEvery {
            profileRepository.saveProfileWithAccountUpdateV2(
                any(),
                any()
            )
        } returns Either.Value(Any())

        viewModel.optIn(true)

        coVerify(exactly = 1) { profileRepository.saveProfileWithAccountUpdateV2(any(), any()) }
        assertEquals(true, viewModel.isOptedIn.first())
    }

    @Test
    fun givenSaveProfileReturnsErrorWhenTryingToOptingInIsOptedInShouldBeFalse() = runBlocking {
        coEvery { profileRepository.getMyProfile() } returns Either.Value(
            Profile(connectRecommendationOptIn = "")
        )
        coEvery {
            profileRepository.saveProfileWithAccountUpdateV2(
                any(),
                any()
            )
        } returns Either.Error(
            ServerError.BAD_REQUEST
        )

        viewModel.optIn(true)

        coVerify(exactly = 1) { profileRepository.saveProfileWithAccountUpdateV2(any(), any()) }
        assertEquals(false, viewModel.isOptedIn.first())
    }

}