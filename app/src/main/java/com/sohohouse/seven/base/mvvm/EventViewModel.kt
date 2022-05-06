package com.sohohouse.seven.base.mvvm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.request.GetEventDetailsRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface EventViewModel {

    val event: LiveData<Event>

    fun getEvent(eventId: String?): Single<Event>

}

class EventViewModelImpl @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val loadable: Loadable.ViewModel? = null
) : EventViewModel {

    private val _event = MutableLiveData<Event>()

    override val event: LiveData<Event>
        get() = _event

    @SuppressLint("CheckResult")
    override fun getEvent(eventId: String?): Single<Event> {
        if (eventId.isNullOrEmpty()) return Single.error(Throwable("Event id cannot be empty"))

        return zipRequestsUtil.issueApiCall(
            GetEventDetailsRequest(
                eventId = eventId,
                includeBookings = true,
                includeResource = true
            )
        )
            .let { single -> if (loadable != null) single.compose(loadable.loadTransformer()) else single }
            .flatMap { either ->
                when (either) {
                    is Either.Value -> Single.just(either.value)
                    is Either.Error -> Single.error(Throwable(either.error.toString()))
                    is Either.Empty -> Single.just(Event())
                }
            }
            .doOnSuccess { event -> _event.postValue(event) }
            .subscribeOn(Schedulers.io())
    }

}