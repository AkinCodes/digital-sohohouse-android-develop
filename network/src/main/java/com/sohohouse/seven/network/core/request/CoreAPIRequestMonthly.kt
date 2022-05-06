package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.models.Meta

interface CoreAPIRequestMonthly<S> : CoreAPIRequest<S> {
    var startsAtFrom: String?
    var startsAtTo: String?
    fun getMeta(response: S): Meta?
}