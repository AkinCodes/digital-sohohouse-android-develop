package com.sohohouse.seven.network.common

import com.sohohouse.seven.network.auth.AuthFailureWhitelist
import com.sohohouse.seven.network.common.interfaces.AuthHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED


@RunWith(RobolectricTestRunner::class)
class AccessTokenAuthenticatorTest {

    companion object {
        private const val MOCK_FRESH_TOKEN = "dXNlcjE6dXNlcjE="

        abstract class TestAuthHelper: AuthHelper {
            var loggedOut = false
        }
    }

    private fun  mockAuthHelper(): AuthHelper {
        return mockk(relaxed = true) {
            every { requestRefreshToken(any())} answers { firstArg<(String) -> Unit>().invoke(MOCK_FRESH_TOKEN) }
            every { token } returns "not implemented"
            every { refreshToken } returns "not implemented"
        }
    }

    private fun tokenProvider() = object : TestAuthHelper() {
        override val token: String
            get() = "TODO( token not implemented) "
        override val refreshToken: String
            get() = "TODO( refreshToken not implemented) "

        override fun requestRefreshToken(onSucess: (refreshToken: String) -> Unit) {
            onSucess(MOCK_FRESH_TOKEN)
        }

        override fun logout() {
            loggedOut = true
        }

        override fun forceLogout(reason: String) {
            loggedOut = true
        }
    }

    private fun tokenProviderRefreshFails() = object : TestAuthHelper() {
        override fun logout() {
            loggedOut = true
        }

        override fun forceLogout(reason: String) {
            loggedOut = true
        }
        override val token: String
            get() = "TODO( token not implemented) "
        override val refreshToken: String
            get() = "TODO( refreshToken not implemented) "

        override fun requestRefreshToken(onSucess: (refreshToken: String) -> Unit) {
            //do nothing
        }
    }

    @Test
    fun `When request fails with 401, it should refresh token and return new request with new token`() {
        val authProvider = tokenProvider()
        val authenticator = AccessTokenAuthenticator(authProvider)

        val dummyRequest = Request.Builder()
                .url("https://test.com")
                .get()
                .header(HeaderInterceptor.AUTHORIZATION_KEY, "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} $12345")
                .build()

        val response = Response.Builder()
                .request(dummyRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Unauthorized")
                .build()

        val refreshedRequest = authenticator.authenticate(null, response)

        Assert.assertNotNull(refreshedRequest)
        Assert.assertNotEquals(dummyRequest.header(HeaderInterceptor.AUTHORIZATION_KEY), (refreshedRequest?.header(HeaderInterceptor.AUTHORIZATION_KEY)))
        Assert.assertEquals("${HeaderInterceptor.BEARER_IDENTIFIER_KEY} $MOCK_FRESH_TOKEN", (refreshedRequest?.header(HeaderInterceptor.AUTHORIZATION_KEY)))
        Assert.assertEquals("${true}", refreshedRequest?.header(HeaderInterceptor.DID_RETRY_AUTH))
        Assert.assertFalse(authProvider.loggedOut)
    }

    @Test
    fun `When Initial request fails 401, we retry auth and request fails again, it should return null and not logout if url is whitelisted`() {
        val authHelper = mockAuthHelper()
        val authenticator = AccessTokenAuthenticator(authHelper)

        val whiteListedEndpoint = AuthFailureWhitelist.endpoints.first()
        val dummyRequest = Request.Builder()
                .url("https://api.production.sohohouse.com$whiteListedEndpoint")
                .get()
                .header(HeaderInterceptor.AUTHORIZATION_KEY, "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} $12345")
                .build()

        //Initial response failure
        val response = Response.Builder()
                .request(dummyRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Request Unauthorized")
                .build()

        val newRequest = authenticator.authenticate(null, response)
        Assert.assertEquals("${true}", newRequest?.header(HeaderInterceptor.DID_RETRY_AUTH))

        //New response failure
        val failedResponse = Response.Builder()
                .request(newRequest!!)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Request Unauthorized")
                .build()

        val resultingRequest = authenticator.authenticate(null, failedResponse)
        Assert.assertEquals(null, (resultingRequest))
        verify (exactly = 0) { authHelper.logout() }
    }

    @Test
    fun `When Initial request fails 401, we retry auth and request fails again, it should return null and logout if url is not whitelisted`() {
        val authHelper = mockAuthHelper()
        val authenticator = AccessTokenAuthenticator(authHelper)

        val dummyRequest = Request.Builder()
                .url("https://test.com")
                .get()
                .header(HeaderInterceptor.AUTHORIZATION_KEY, "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} $12345")
                .build()

        //Initial response failure
        val response = Response.Builder()
                .request(dummyRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Request Unauthorized")
                .build()

        val newRequest = authenticator.authenticate(null, response)
        Assert.assertEquals("${true}", newRequest?.header(HeaderInterceptor.DID_RETRY_AUTH))

        //New response failure
        val failedResponse = Response.Builder()
                .request(newRequest!!)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Request Unauthorized")
                .build()

        val resultingRequest = authenticator.authenticate(null, failedResponse)
        Assert.assertEquals(null, (resultingRequest))
        verify { authHelper.forceLogout(any()) }
    }

    @Test
    fun `When a refresh token call fails, return null`() {
        val authProvider = tokenProviderRefreshFails()
        val authenticator = AccessTokenAuthenticator(authProvider)

        val dummyRequest = Request.Builder()
                .url("https://test.com")
                .get()
                .header(HeaderInterceptor.AUTHORIZATION_KEY, "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} $12345")
                .build()

        val response = Response.Builder()
                .request(dummyRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(HTTP_UNAUTHORIZED)
                .message("Unauthorized")
                .build()

        val refreshedRequest = authenticator.authenticate(null, response)

        Assert.assertNull(refreshedRequest)
        Assert.assertTrue(authProvider.loggedOut)
    }

}