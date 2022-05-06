package com.sohohouse.seven.network.auth

import androidx.test.espresso.idling.CountingIdlingResource
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.network.FakeErrorDetailExtractor
import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.auth.request.AuthenticationAPIRequest
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import io.reactivex.Scheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLPeerUnverifiedException

class AuthenticationRequestFactoryTest {

    private lateinit var requestFactory: AuthenticationRequestFactory

    @Mock
    private lateinit var api: AuthApi

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        requestFactory = AuthenticationRequestFactory(
            api,
            Mockito.mock(CountingIdlingResource::class.java),
            FakeErrorDetailExtractor()
        )

        RxJavaPlugins.setIoSchedulerHandler { t: Scheduler -> Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        Mockito.reset(api)
        RxJavaPlugins.reset()
    }

    @Test
    fun `server returns 200`() {
        // GIVEN server returns 200, with a valid success object
        val successObject = ""
        val response = mock<Response<String>> {
            on(it.body()).thenReturn(successObject)
            on(it.isSuccessful).thenReturn(true)
        }
        val call = mock<Call<String>> {
            on(it.execute()).thenReturn(response)
        }
        val request =
                mock<AuthenticationAPIRequest<String>> {
                    on(it.createCall(api)).thenReturn(call)
                }

        // WHEN when we start the created call
        val testObserver =
                requestFactory.create(request).test()

        // THEN the output of the call is the valid success object
        testObserver.assertValue { it == value(successObject) }
    }

    @Test
    fun `server returns any non-200 status`() {
        // GIVEN that server will return a 401 for some response
        val unauthCode = 401
        val unauthMsg = "Unauthorized"
        val okhttpResponse = mock<ResponseBody> {
            on(it.string()).thenReturn(unauthMsg)
        }
        val response = mock<Response<Any>> {
            on(it.code()).thenReturn(unauthCode)
            on(it.errorBody()).thenReturn(okhttpResponse)
            on(it.isSuccessful).thenReturn(false)
        }
        val call = mock<Call<Any>> {
            on(it.execute()).thenReturn(response)
        }
        val request = mock<AuthenticationAPIRequest<Any>> {
            on(it.createCall(api)).thenReturn(call)
            on(it.mapError(unauthCode, unauthMsg)).thenReturn(AuthError.INVALID_OAUTH_TOKEN)
        }

        // WHEN the request is created and executed
        val observer = requestFactory.create(request).test()

        // THEN verify that the request will process the error
        verify(request).mapError(unauthCode, unauthMsg)
        observer.assertValue { it == error(AuthError.INVALID_OAUTH_TOKEN) }
    }

    @Test
    fun `server has bad cert`() {
        // GIVEN server has an invalid certificate
        // WHEN an authenticated api call is made
        // THEN verify invalid cert error reported
        testException(SSLPeerUnverifiedException("Invalid Certificate"), ServerError.INVALID_CERT)
    }

    @Test
    fun `disconnect from server`() {
        // GIVEN disconnected from server
        // WHEN an authenticated api call is made
        // THEN verify no internet connection error surfaced
        testException(UnknownHostException(), ServerError.NO_INTERNET)
    }

    @Test
    fun `server timeout`() {
        // GIVEN server times out
        // WHEN an authenticated api call is made
        // THEN verify timeout reported
        testException(SocketTimeoutException(), ServerError.TIMEOUT)
    }

    @Test
    fun `server unknown error`() {
        // GIVEN server fails for some reason
        // WHEN an authenticated api call is made
        // THEN verify complete meltdown reported
        testException(UnknownServiceException(), ServerError.COMPLETE_MELTDOWN)
    }

    private fun testException(e: Exception, expectedError: ServerError) {
        val call = mock<Call<Any>> {
            on(it.execute()).thenThrow(e)
        }
        val request = mock<AuthenticationAPIRequest<Any>> {
            on(it.createCall(api)).thenReturn(call)
        }

        val observer = requestFactory.create(request).test()

        observer.assertValue { it == error(expectedError) }
    }
}