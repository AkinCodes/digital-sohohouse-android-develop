package com.sohohouse.seven.connect.filter.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sohohouse.seven.network.base.error.ServerError
import kotlinx.coroutines.flow.MutableStateFlow

abstract class FilterRepository {

    abstract val items: LiveData<List<Filterable>>

    protected val _error = MutableStateFlow<ServerError?>(null)

    val error: LiveData<ServerError?>
        get() = _error.asLiveData()

}