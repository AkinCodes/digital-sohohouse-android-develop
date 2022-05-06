package com.sohohouse.seven.network.vimeo

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.base.RequestFactory

class VimeoRequestFactory(api: VimeoApi, idlingResource: CountingIdlingResource) :
    RequestFactory<VimeoApi>(api, idlingResource)