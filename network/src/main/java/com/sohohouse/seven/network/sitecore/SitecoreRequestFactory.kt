package com.sohohouse.seven.network.sitecore

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.base.RequestFactory

class SitecoreRequestFactory constructor(
    coreApi: SitecoreApi,
    idlingResource: CountingIdlingResource,
) : RequestFactory<SitecoreApi>(coreApi, idlingResource)
