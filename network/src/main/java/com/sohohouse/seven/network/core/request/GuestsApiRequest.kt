package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.models.GuestsMeta

interface GuestsApiRequest<S> : CoreAPIRequest<S> {
    fun getMeta(response: S): GuestsMeta

}