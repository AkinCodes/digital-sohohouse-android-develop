package com.sohohouse.seven.common.utils

import android.util.Patterns

//TODO test
object UrlUtils {

    const val GET = "GET"
    const val LOCATION_HEADER_FIELD = "location"
    private const val PROTOCOL = "https://"
    private const val PROTOCOL_BEGIN = "http"

    fun sanitiseUrl(url: String?): String? {
        if (url == null) return null
        var sanitised = url

        sanitised = sanitised.removeSurrounding("\\'").replace(" ", "")

        if (!url.startsWith(PROTOCOL_BEGIN)) {
            sanitised = "$PROTOCOL$url"
        }
        return if (Patterns.WEB_URL.matcher(sanitised).matches()) sanitised else null
    }

}