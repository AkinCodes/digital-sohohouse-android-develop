package com.sohohouse.seven.more.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.request.DeletePaymentRequest
import com.sohohouse.seven.network.core.request.GetPaymentRequest
import com.sohohouse.seven.network.core.request.PatchDefaultPaymentRequest
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.PaymentCardType
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class MorePaymentPresenterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var view: MorePaymentActivity

    @Mock
    lateinit var loadingView: LoadingView

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var userManager: UserManager

    lateinit var morePaymentPresenter: MorePaymentPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        morePaymentPresenter = MorePaymentPresenter(zipRequestsUtil, analyticsManager, userManager)
    }

    @Test
    fun `when view attached, fetch payment cards`() {

        // GIVEN there are some cards
        val card = Card(
            cardType = PaymentCardType.AMEX.name,
            lastFour = "",
            isPrimary = true,
            status = PaymentCardStatus.EXPIRED.name
        )
        card.id = "1"
        val result = listOf(card)

        `when`(zipRequestsUtil.issueApiCall(any<GetPaymentRequest>()))
            .thenReturn(Single.just(value(result)))
        `when`(view.loadingView)
            .thenReturn(loadingView)

        // WHEN the view is attached
        morePaymentPresenter.attach(view)

        // THEN the presenter fetches cards and updates the view with the cards
        verify(zipRequestsUtil).issueApiCall(any<GetPaymentRequest>())
        verify(view).onDataReady(any())
    }

    @Test
    fun `when view attached, fetch payment cards returns no cards, show empty view`() {

        // GIVEN there are no cards
        val result = listOf<Card>()

        `when`(zipRequestsUtil.issueApiCall(any<GetPaymentRequest>()))
            .thenReturn(Single.just(value(result)))
        `when`(view.loadingView)
            .thenReturn(loadingView)

        // WHEN the view is attached
        morePaymentPresenter.attach(view)

        // THEN the presenter fetches cards and shows empty view
        verify(zipRequestsUtil).issueApiCall(any<GetPaymentRequest>())
        verify(view).showEmptyView()
    }

    @Test
    fun `when user sets payment as default, send api request and update view`() {

        // GIVEN view is set up
        val result = listOf<Card>()

        `when`(zipRequestsUtil.issueApiCall(any<GetPaymentRequest>()))
            .thenReturn(Single.just(value(result)))
        `when`(view.loadingView)
            .thenReturn(loadingView)
        morePaymentPresenter.attach(view)
        val card = Card()
        `when`(zipRequestsUtil.issueApiCall(any<PatchDefaultPaymentRequest>()))
            .thenReturn(Single.just(value(card)))

        // WHEN the user requests a card to be set as default
        morePaymentPresenter.setDefaultPaymentMethod("id")

        // THEN the presenter fetches cards and shows empty view
        verify(zipRequestsUtil).issueApiCall(any<PatchDefaultPaymentRequest>())
        verify(view).defaultPaymentUpdated(card)
    }

    @Test
    fun `when user deletes a payment, send api request and update view`() {

        // GIVEN view is set up
        val result = listOf<Card>()

        `when`(zipRequestsUtil.issueApiCall(any<GetPaymentRequest>()))
            .thenReturn(Single.just(value(result)))
        `when`(view.loadingView)
            .thenReturn(loadingView)
        morePaymentPresenter.attach(view)
        `when`(zipRequestsUtil.issueApiCall(any<DeletePaymentRequest>()))
            .thenReturn(Single.just(empty()))

        // WHEN the user requests a card to be set as default
        morePaymentPresenter.deletePaymentMethod("id")

        // THEN the presenter fetches cards and shows empty view
        verify(zipRequestsUtil).issueApiCall(any<DeletePaymentRequest>())
        verify(view).paymentDeleted("id")
    }
}