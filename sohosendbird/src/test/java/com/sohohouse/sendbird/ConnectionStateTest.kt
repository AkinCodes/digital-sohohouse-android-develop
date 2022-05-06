package com.sohohouse.sendbird

import com.sohohouse.sendbird.data.ConnectionState
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class ConnectionStateTest {

    @ExperimentalTime
    @Test
    fun `token validation is correct`() {
        val connection = ConnectionState(
            token = "ჩურჩხელა",
            expiresAt = System.currentTimeMillis() + Duration.days(2).inWholeMilliseconds
        )

        assert(connection.isValid)
    }
}