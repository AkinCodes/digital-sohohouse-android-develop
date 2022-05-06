package com.sohohouse.seven.common.user

import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.network.common.HeaderInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    private val headerInterceptor: HeaderInterceptor,
    val prefsManager: PrefsManager
) {

    init {
        headerInterceptor.sessionToken = token
    }

    var token: String
        get() = prefsManager.token
        set(value) = setTokenValue(value)

    var refreshToken: String
        get() = prefsManager.refreshToken
        set(value) {
            prefsManager.refreshToken = value
        }

    fun isChasingMembership(membershipStatus: String): Boolean {
        return MembershipStatus.valueOf(membershipStatus) == MembershipStatus.CHASING
    }

    val isLoggedIn: Boolean
        get() = refreshToken.isNotBlank() && token.isNotBlank()

    private fun setTokenValue(value: String) {
        prefsManager.token = value
        headerInterceptor.sessionToken = value
    }

    val shouldShowAlertMap: MutableMap<String, Boolean> = mutableMapOf()

    fun logout() {
        token = ""
        refreshToken = ""

        for (shouldShowAlert in shouldShowAlertMap)
            shouldShowAlert.setValue(true)
    }

}