package com.sohohouse.seven.book.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetEventsCategoriesRequest
import kotlinx.coroutines.*

interface EventCategoryRepository {

    val categories: LiveData<List<EventCategory>>

    val error: LiveData<ServerError>

    fun fetch()

    fun cancel()

}

class EventCategoryRepositoryImpl(private val zipRequestsUtil: ZipRequestsUtil) :
    EventCategoryRepository {

    private val _categories = MutableLiveData<List<EventCategory>>()

    override val categories: LiveData<List<EventCategory>>
        get() = _categories

    private val _error: MutableLiveData<ServerError> = MutableLiveData()

    override val error: LiveData<ServerError>
        get() = _error

    private val job = Job()

    init {
        fetch()
    }

    override fun fetch() {
        CoroutineScope(Dispatchers.IO + job).launch {
            zipRequestsUtil.issueApiCall(GetEventsCategoriesRequest()).fold(
                ifValue = { _categories.postValue(it) },
                ifError = { _error.postValue(it) },
                ifEmpty = { _categories.postValue(emptyList()) }
            )
        }
    }

    override fun cancel() {
        job.cancelChildren()
        job.cancel()
    }

}