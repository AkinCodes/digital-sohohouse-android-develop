package com.sohohouse.sendbird.data

import org.joda.time.DateTime

class ConnectionState private constructor(
    val token: String,
    private val isConnected: Boolean,
    private val expiresAt: DateTime? = null
) {

    constructor(
        token: String,
        expiresAt: Long
    ) : this(
        token,
        true,
        expiresAt = DateTime(expiresAt)
    )

    val isValid: Boolean
        get() = expiresAt?.isAfterNow == true && isConnected && token.isNotEmpty()

    companion object {
        fun notConnected() = ConnectionState(
            token = "",
            isConnected = false,
            expiresAt = null
        )
    }
}