package com.sohohouse.seven.network.vimeo.model

import com.google.gson.annotations.SerializedName


data class VimeoResponse(@SerializedName(value = "request") val request: Request? = null)

data class Request(@SerializedName(value = "files") val files: Files? = null)

data class Files(@SerializedName(value = "progressive") val progressive: List<Progressive>? = null)

data class Progressive(
    @SerializedName(value = "url") val url: String,
)