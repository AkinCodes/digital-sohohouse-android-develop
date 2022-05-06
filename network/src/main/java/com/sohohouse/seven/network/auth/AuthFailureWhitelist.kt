package com.sohohouse.seven.network.auth

/**
 * Defines endpoints that should not cause an app logout if we receive a 401 response from them
 */
object AuthFailureWhitelist {

    val endpoints = arrayOf(
        "/communications/notifications"
    )

}