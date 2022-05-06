package com.sohohouse.seven.network

import com.sohohouse.seven.network.base.error.ErrorDetailExtractor

class FakeErrorDetailExtractor: ErrorDetailExtractor {
    override fun extractErrorDetails(rawErrorBody: String): String {
        return ""
    }
}