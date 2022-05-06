package com.sohohouse.seven.network.core

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.base.RequestFactory
import com.sohohouse.seven.network.base.error.ErrorDetailExtractor
import com.sohohouse.seven.network.base.error.ErrorDetailExtractorImpl
import com.sohohouse.seven.network.core.api.CoreApi

class CoreRequestFactory(
    api: CoreApi,
    idlingResource: CountingIdlingResource,
    errorDetailExtractor: ErrorDetailExtractor = ErrorDetailExtractorImpl(),
) : RequestFactory<CoreApi>(api, idlingResource, errorDetailExtractor)