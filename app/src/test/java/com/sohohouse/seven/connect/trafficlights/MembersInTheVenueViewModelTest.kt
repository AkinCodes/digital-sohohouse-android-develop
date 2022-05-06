package com.sohohouse.seven.connect.trafficlights

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueViewModel
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.profile.Connected
import com.sohohouse.seven.profile.NotConnected
import com.sohohouse.seven.profile.RequestReceived
import com.sohohouse.seven.profile.RequestSent
import com.sohohouse.seven.profile.view.model.AcceptButton
import com.sohohouse.seven.profile.view.model.ConnectButton
import com.sohohouse.seven.profile.view.model.RequestSentButton
import com.sohohouse.seven.profile.view.model.ViewProfileButton
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MembersInTheVenueViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val localVenueProvider = mockk<LocalVenueProvider>()
    private val userManager = mockk<UserManager>()
    private val connectionsRepo = mockk<ConnectionRepository>()
    private val trafficLightsRepo = mockk<TrafficLightsRepo>()
    private val analyticsManager = mockk<AnalyticsManager>()


    val viewModel by lazy {
        MembersInTheVenueViewModel(
            userManager = userManager,
            connectionRepo = connectionsRepo,
            trafficLightsRepo = trafficLightsRepo,
            localVenueProvider = localVenueProvider,
            analyticsManager = analyticsManager,
        )
    }

    @Before
    fun setUp() {
        every { userManager.profileImageURL } returns "testURL"
        every { trafficLightsRepo.clearCache() } returns Unit
    }

    @Test
    fun `validate VenueMembers action button types`() = runBlocking {
        every { userManager.availableStatusFlow } returns connectionsOnlyFlow()
        coEvery { trafficLightsRepo.getVenueMembers(any(), any(), any()) } returns value(
            VenueMembers(
                mutableListOf(
                    FakeVenueMember(mutualConnectionStatus = NotConnected),
                    FakeVenueMember(mutualConnectionStatus = Connected),
                    FakeVenueMember(mutualConnectionStatus = RequestSent),
                    FakeVenueMember(mutualConnectionStatus = RequestReceived),
                ),
                4,
                nextPage = 1
            )
        )
        val venueMembers = viewModel.venueMembers.first()

        Assert.assertThat(
            venueMembers.map { it.button },
            Matchers.allOf(
                Matchers.containsInAnyOrder(
                    AcceptButton, ConnectButton, ViewProfileButton, RequestSentButton
                )
            )
        )
    }

    private fun connectionsOnlyFlow() = MutableStateFlow(
        UserAvailableStatus("", AvailableStatus.CONNECTIONS_ONLY)
    )

}