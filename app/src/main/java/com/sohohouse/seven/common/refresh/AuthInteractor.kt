package com.sohohouse.seven.common.refresh

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.network.auth.AuthenticationRequestFactory
import com.sohohouse.seven.network.auth.request.RefreshTokenRequest
import com.sohohouse.seven.network.common.interfaces.AuthHelper
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.request.GetAccountRequest
import dagger.Lazy
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val logoutUtil: LogoutUtil,
    private val userSessionManager: UserSessionManager,
    private val authenticationRequestFactory: AuthenticationRequestFactory,
    private val coreRequestFactory: Lazy<CoreRequestFactory>
) : AuthHelper {
    override fun logout() {
        logoutUtil.logout()
    }

    override fun forceLogout(reason: String) {
        FirebaseCrashlytics.getInstance()
            .recordException(
                RuntimeException(
                    "Forced logout: $reason"
                )
            )
        logout()
    }

    override val token: String
        get() = userSessionManager.token

    override val refreshToken: String
        get() = userSessionManager.refreshToken

    override fun requestRefreshToken(onSucess: (accessToken: String) -> Unit) {
        authenticationRequestFactory
            .create(RefreshTokenRequest(userSessionManager.refreshToken))
            .blockingGet()
            .fold({
                logoutUtil.logout()
            }, {
                recordTokens(it.accessToken, it.refreshToken)
                appGatingCheck(onSucess, it.accessToken)
            }, {})
    }

    private fun appGatingCheck(onSucess: (accessToken: String) -> Unit, refreshedToken: String) {
        coreRequestFactory.get().create(GetAccountRequest(includeMembership = true))
            .blockingGet()
            .fold({
                logoutUtil.logout()
            }, {
                if (it.canAccessApp) {
                    onSucess(refreshedToken)
                } else {
                    logoutUtil.logout()
                }
            }, {})
    }

    private fun recordTokens(accessToken: String, refreshToken: String) {
        userSessionManager.refreshToken = refreshToken
        userSessionManager.token = accessToken
    }
}