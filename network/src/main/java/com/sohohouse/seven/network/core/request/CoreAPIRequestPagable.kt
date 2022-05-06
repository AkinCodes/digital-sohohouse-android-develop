package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.models.Meta

interface CoreAPIRequestPagable<S> : CoreAPIRequest<S> {
    var page: Int?
    var perPage: Int?
    fun getMeta(response: S): Meta?
}