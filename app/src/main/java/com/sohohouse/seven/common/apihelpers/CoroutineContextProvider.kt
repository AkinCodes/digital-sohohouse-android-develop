package com.sohohouse.seven.common.apihelpers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CoroutineContextProvider @Inject constructor() {
    open val IO: CoroutineDispatcher = Dispatchers.IO
    open val Main: CoroutineDispatcher = Dispatchers.Main
}