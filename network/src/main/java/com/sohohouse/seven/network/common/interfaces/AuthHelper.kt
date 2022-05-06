package com.sohohouse.seven.network.common.interfaces

interface AuthHelper {

    val token: String

    val refreshToken: String

    /**
     * Refreshes the token. This call is made synchronously.
     */
    fun requestRefreshToken(onSucess: (accessToken: String) -> Unit)

    fun logout()

    fun forceLogout(reason: String)
}