package com.sohohouse.seven.more.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.encryption.PublicKeyEncryptable
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.Form
import com.sohohouse.seven.network.core.models.PaymentFormFields
import com.sohohouse.seven.network.core.request.GetPaymentFormRequest
import com.sohohouse.seven.network.core.request.PostPaymentCardRequest
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class AddPaymentPresenterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var pgpEncryptable: PublicKeyEncryptable

    @Mock
    lateinit var view: AddPaymentViewController

    @Mock
    lateinit var loadingView: LoadingView

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var userManager: UserManager

    lateinit var addPaymentPresenter: AddPaymentPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        addPaymentPresenter =
            AddPaymentPresenter(zipRequestsUtil, pgpEncryptable, analyticsManager, userManager)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `when view attached, fetch payment form`() {

        // GIVEN there is form
        val fields = listOf(PaymentFormFields())
        val key = "key"
        val form = Form(publicKey = key, paymentFormFields = fields)
        form.id = "1"

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetPaymentFormRequest>()))
            .thenReturn(Single.just(value(form)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        // WHEN the view is attached
        addPaymentPresenter.attach(view)

        // THEN the presenter fetches form and updates the view
        Mockito.verify(zipRequestsUtil).issueApiCall(any<GetPaymentFormRequest>())
        Mockito.verify(view).initLayout(form.id, fields)
    }

    @Test
    fun `when user adds a card, send api request and update view`() {
        // GIVEN view is set up
        val fields = listOf(PaymentFormFields())
        val key = "key"
        val form = Form(publicKey = key, paymentFormFields = fields)
        form.id = "1"

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetPaymentFormRequest>()))
            .thenReturn(Single.just(value(form)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        addPaymentPresenter.attach(view)

        val card = Card()
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<PostPaymentCardRequest>()))
            .thenReturn(Single.just(value(card)))
        Mockito.`when`(pgpEncryptable.encrypt(any(), eq(key)))
            .thenReturn(Single.just("ENCRYPTED"))

        // WHEN the user requests a card to be added
        addPaymentPresenter.onAddButtonClicked("id", listOf(Pair("1", "2")))

        // THEN the presenter informs of success
        Mockito.verify(zipRequestsUtil).issueApiCall(any<PostPaymentCardRequest>())
        Mockito.verify(view).addCardSuccess(card)
    }

    @Test
    fun `when user sends api request to add card, encryption library is envoked`() {
        // GIVEN form is fetched and user will add a card
        val fields = listOf(PaymentFormFields())
        val key = "key"
        val form = Form(publicKey = key, paymentFormFields = fields)
        form.id = "1"

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetPaymentFormRequest>()))
            .thenReturn(Single.just(value(form)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        addPaymentPresenter.attach(view)

        val card = Card()
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<PostPaymentCardRequest>()))
            .thenReturn(Single.just(value(card)))
        Mockito.`when`(pgpEncryptable.encrypt(any(), eq(key)))
            .thenReturn(Single.just("ENCRYPTED"))

        // WHEN the user has added the card
        addPaymentPresenter.onAddButtonClicked("id", listOf(Pair("1", "2")))

        // THEN the the presenter will encrypt the card before sending
        Mockito.verify(pgpEncryptable, times(1)).encrypt(any(), eq(key))
    }

    @Test
    fun `user tries to add a card, encryption fails, inform of an error to user`() {
        // GIVEN encryption will fail with the supplied form
        val fields = listOf(PaymentFormFields())
        val key = "key"
        val form = Form(publicKey = key, paymentFormFields = fields)
        form.id = "1"

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetPaymentFormRequest>()))
            .thenReturn(Single.just(value(form)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        addPaymentPresenter.attach(view)

        val card = Card()
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<PostPaymentCardRequest>()))
            .thenReturn(Single.just(value(card)))
        Mockito.`when`(pgpEncryptable.encrypt(any(), eq(key)))
            .thenReturn(Single.just(""))

        // WHEN the user requests a card to be added
        addPaymentPresenter.onAddButtonClicked("id", listOf(Pair("1", "2")))

        // THEN the presenter informs of error
        Mockito.verify(view).showGenericErrorDialog()
    }
}
