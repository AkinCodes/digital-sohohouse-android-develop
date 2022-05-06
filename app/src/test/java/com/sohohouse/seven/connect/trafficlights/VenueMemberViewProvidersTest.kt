package com.sohohouse.seven.connect.trafficlights

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.controlpanel.VenueMemberViewProvider
import com.sohohouse.seven.connect.trafficlights.controlpanel.VenueMemberViewProviders
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class VenueMemberViewProvidersTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val venueMembers: List<VenueMember>
        get() {
            return List(10) {
                if (it in 8..9) {
                    FakeVenueMember(isConnection = true)
                } else {
                    FakeVenueMember(isConnection = false)
                }
            }
        }

    @Test
    fun `returns 2 notBlurred 3 blurred and 5 showMore when ConnectionsOnly`() {
        val connectMemberViewProviders = VenueMemberViewProviders(
            venueMembers = venueMembers,
            availableStatus = AvailableStatus.CONNECTIONS_ONLY,
            threshold = 5,
            estimatedTotal = 10
        )

        val showMore = connectMemberViewProviders
            .first { it is VenueMemberViewProvider.ShowMore }
            .let { it as VenueMemberViewProvider.ShowMore }

        Assert.assertEquals(
            2,
            connectMemberViewProviders
                .filterIsInstance<VenueMemberViewProvider.NotBlurred>()
                .size
        )

        Assert.assertEquals(
            3,
            connectMemberViewProviders
                .filterIsInstance<VenueMemberViewProvider.Blurred>()
                .size
        )

        Assert.assertEquals(
            1,
            connectMemberViewProviders.filterIsInstance<VenueMemberViewProvider.ShowMore>().size
        )

        Assert.assertEquals(5, showMore.more)
    }

    @Test
    fun `returns 5 blurred and 5 showMore when unavailable`() {
        val connectMemberViewProviders = VenueMemberViewProviders(
            venueMembers = venueMembers,
            availableStatus = AvailableStatus.UNAVAILABLE,
            threshold = 5,
            estimatedTotal = 10
        )

        val showMore = connectMemberViewProviders
            .first { it is VenueMemberViewProvider.ShowMore }
            .let { it as VenueMemberViewProvider.ShowMore }

        Assert.assertEquals(
            5,
            connectMemberViewProviders
                .filterIsInstance<VenueMemberViewProvider.Blurred>()
                .size
        )

        Assert.assertEquals(
            1,
            connectMemberViewProviders.filterIsInstance<VenueMemberViewProvider.ShowMore>().size
        )

        Assert.assertEquals(5, showMore.more)
    }

    @Test
    fun `returns 5 notBlurred and 5 showMore when unavailable`() {
        val connectMemberViewProviders = VenueMemberViewProviders(
            venueMembers = venueMembers,
            availableStatus = AvailableStatus.AVAILABLE,
            threshold = 5,
            estimatedTotal = 10
        )

        val showMore = connectMemberViewProviders
            .first { it is VenueMemberViewProvider.ShowMore }
            .let { it as VenueMemberViewProvider.ShowMore }

        Assert.assertEquals(
            5,
            connectMemberViewProviders
                .filterIsInstance<VenueMemberViewProvider.NotBlurred>()
                .size
        )

        Assert.assertEquals(
            1,
            connectMemberViewProviders.filterIsInstance<VenueMemberViewProvider.ShowMore>().size
        )

        Assert.assertEquals(5, showMore.more)
    }

    @Test
    fun `returns 0 place holders when list is empty`() {

        val connectMemberViewProviders = VenueMemberViewProviders(
            venueMembers = emptyList(),
            availableStatus = AvailableStatus.AVAILABLE,
            threshold = 5,
            estimatedTotal = 10
        )

        Assert.assertEquals(
            0,
            connectMemberViewProviders
                .filterIsInstance<VenueMemberViewProvider.PlaceHolder>()
                .size
        )

    }

}