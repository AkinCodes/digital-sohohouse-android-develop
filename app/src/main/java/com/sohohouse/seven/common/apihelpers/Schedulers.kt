package com.sohohouse.seven.common.apihelpers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Schedulers @Inject constructor() {
    open val IO: Scheduler get() = Schedulers.io()
    open val Main: Scheduler get() = AndroidSchedulers.mainThread()
}