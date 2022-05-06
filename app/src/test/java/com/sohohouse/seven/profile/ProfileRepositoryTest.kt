package com.sohohouse.seven.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.request.GetMyProfileRequest
import com.sohohouse.seven.network.core.request.GetProfileRequest
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class ProfileRepositoryTest {

    companion object {
        private const val MY_PROFILE_ID = "myProfID"
        private const val OTHER_USER_PROFILE_ID = "otherProfID"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var coreReqFactory: CoreRequestFactory

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var sohoApiService: SohoApiService

    private lateinit var profileRepository: ProfileRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(userManager.profileID).thenReturn(MY_PROFILE_ID)

        profileRepository = ProfileRepository(coreReqFactory, userManager, sohoApiService)
    }

    @Test
    fun `ProfileRepository fetches data`() {
        // GIVEN the profile repo has no profile saved
        val profile = ProfileTestHelper.fullMockProfile()
        Mockito.`when`(coreReqFactory.createV2(any<GetMyProfileRequest>()))
            .thenReturn(value(profile))

        // WHEN the profile repo tries to fetch my profile
        val result = profileRepository.getMyProfile()

        // THEN an api call gets made to fetch my profile
        verify(coreReqFactory, times(1)).createV2(any<GetMyProfileRequest>())
        assert(result is Either.Value && result.value == profile)
    }

    @Test
    fun `ProfileRepository saves the fetched data`() {
        val testScheduler = TestScheduler()

        // GIVEN the profile repo has some profile data
        val profile = ProfileTestHelper.fullMockProfile()
        Mockito.`when`(coreReqFactory.createV2(any<GetMyProfileRequest>()))
            .thenReturn(value(profile))

        //WHEN
        val result1 = profileRepository.getMyProfile()
        testScheduler.triggerActions()

        //THEN
        verify(coreReqFactory, times(1)).createV2(any<GetMyProfileRequest>())
    }

    @Test
    fun `ProfileRepository fetches other profile`() {
        // GIVEN the profile repo has no profile saved
        val profile = ProfileTestHelper.fullMockProfile()
        Mockito.`when`(coreReqFactory.createV2(any<GetProfileRequest>())).thenReturn(value(profile))

        // WHEN the profile repo tries to fetch my profile
        val result = profileRepository.getProfile(OTHER_USER_PROFILE_ID)

        // THEN an api call gets made to fetch my profile
        verify(coreReqFactory, times(1)).createV2(any<GetProfileRequest>())
        assert(result is Either.Value && result.value == profile)
    }

    @Test
    fun `ProfileRepository saves other profile`() {
        val testScheduler = TestScheduler()

        // GIVEN the profile repo has some profile data
        val profile = ProfileTestHelper.fullMockProfile()
        Mockito.`when`(coreReqFactory.createV2(any<GetProfileRequest>())).thenReturn(value(profile))

        //WHEN
        val result1 = profileRepository.getProfile(OTHER_USER_PROFILE_ID)

        //THEN
        verify(coreReqFactory, times(1)).createV2(any<GetProfileRequest>())
    }

}