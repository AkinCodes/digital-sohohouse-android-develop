package com.sohohouse.seven.network.sitecore

object SitecoreResourceFactory {

    private const val SITECORE_BASE_URL =
        "https://res.cloudinary.com/soho-house/image/upload/sitecore-prod/"
    private const val SITECORE_THUMBNAIL_URL_PREFIX = "/-/media/"

    // /-/media/images/editorial/2020/03-march/eni-aluko/00enialukocreditcourtesyofenialuko.jpg
    fun getImageUrl(url: String): String? {
        if (!url.startsWith(SITECORE_THUMBNAIL_URL_PREFIX)) return null
        return url.replace(SITECORE_THUMBNAIL_URL_PREFIX, SITECORE_BASE_URL)
    }

}