package com.sohohouse.seven.network.base.error

import org.json.JSONArray
import org.json.JSONObject

interface ErrorDetailExtractor {
    fun extractErrorDetails(rawErrorBody: String): String
}

class ErrorDetailExtractorImpl : ErrorDetailExtractor {
    override fun extractErrorDetails(rawErrorBody: String): String {
        val errors = JSONObject(rawErrorBody).opt("errors") as? JSONArray?
        return (errors?.get(0) as? JSONObject)
            ?.getString("detail")
            ?.removePrefix("Validation failed: ")
            ?: ""
    }
}