package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.models.CursorMeta

interface CoreAPIRequestCursorPagable<S> : CoreAPIRequest<S> {
    var nextCursor: String?
    var perPage: Int?
    fun getMeta(response: S): CursorMeta?
}