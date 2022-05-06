package com.sohohouse.seven.base.datasource

import com.sohohouse.seven.base.mvvm.LiveEvent

interface ErrorStateDataSource {
    val errorViewState: LiveEvent<Any>
}