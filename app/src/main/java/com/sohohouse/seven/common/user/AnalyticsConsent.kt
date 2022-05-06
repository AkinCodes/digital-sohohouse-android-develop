package com.sohohouse.seven.common.user

import com.sohohouse.seven.BuildConfig

enum class AnalyticsConsent {
    NONE,
    ACCEPTED,
    REJECTED;

    companion object {

        fun fromOrdinal(ordinal: Int): AnalyticsConsent {
            return try {
                values()[ordinal]
            } catch (e: IndexOutOfBoundsException) {
                if (BuildConfig.DEBUG) ACCEPTED else NONE
            }
        }
    }
}