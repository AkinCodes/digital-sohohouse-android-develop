package com.sohohouse.seven.memberonboarding.induction.booking

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.book.eventdetails.payment.BookEventHelper
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Inquiry
import com.sohohouse.seven.network.core.models.Membership
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetEventsRequest
import com.sohohouse.seven.network.core.request.PatchMembershipAttributesRequest
import com.sohohouse.seven.network.core.request.PostInquiryRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InductionBookingPresenterTest {
    @MockK
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var venueRepo: VenueRepo

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var bookEventHelper: BookEventHelper

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when user makes induction follow-up inquiry and it is successful, inducted_at patch is made on their account`() {
        //GIVEN
        val cut = InductionBookingPresenter(
            zipRequestsUtil,
            userManager,
            venueRepo,
            bookEventHelper,
            analyticsManager
        )

        val userLocalHouse = "SD"

        val venues = value(VenueList(Venue().apply { id = userLocalHouse }))
        every { venueRepo.fetchVenuesSingle() } returns Single.just(venues)
        every { userManager.localHouseId } returns userLocalHouse

        every { zipRequestsUtil.issueApiCall(ofType(GetEventsRequest::class)) } returns Single.just(
            value(emptyList())
        )

        every { zipRequestsUtil.issueApiCall(ofType(PostInquiryRequest::class)) } returns Single.just(
            value(Inquiry())
        )

        every { zipRequestsUtil.issueApiCall(ofType(PatchMembershipAttributesRequest::class)) } returns Single.just(
            value(Membership())
        )

        val view = mockk<InductionBookingViewController>(relaxed = true)

        //WHEN
        cut.attach(view)
        cut.onActionPress()

        //THEN
        verify { zipRequestsUtil.issueApiCall(ofType(PostInquiryRequest::class)) }
        verify { zipRequestsUtil.issueApiCall(ofType(PatchMembershipAttributesRequest::class)) }

    }
}