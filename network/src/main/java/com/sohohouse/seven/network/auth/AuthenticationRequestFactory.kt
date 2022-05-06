package com.sohohouse.seven.network.auth

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.base.RequestFactory
import com.sohohouse.seven.network.base.error.ErrorDetailExtractor
import com.sohohouse.seven.network.base.error.ErrorDetailExtractorImpl

class AuthenticationRequestFactory(
    api: AuthApi,
    idlingResource: CountingIdlingResource,
    errorDetailsExtractor: ErrorDetailExtractor = ErrorDetailExtractorImpl(),
) : RequestFactory<AuthApi>(api, idlingResource, errorDetailsExtractor)