package com.sohohouse.seven.book.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetVenuesRequest
import kotlinx.coroutines.*

interface HouseRepository {

    val error: LiveData<ServerError>

    val venues: LiveData<List<Venue>>

    val allVenues: LiveData<Map<String, Venue>>

    fun fetchVenues()

    fun cancel()
}

class HouseRepositoryImpl(
    private val zipRequestsUtil: ZipRequestsUtil
) : HouseRepository {

    private val job = Job()

    private val _allVenues = MutableLiveData<Map<String, Venue>>()

    override val allVenues: LiveData<Map<String, Venue>>
        get() = _allVenues

    private val _venues = MutableLiveData<List<Venue>>()

    override val venues: LiveData<List<Venue>>
        get() = _venues

    private val _error: MutableLiveData<ServerError> = MutableLiveData()

    override val error: LiveData<ServerError>
        get() = _error

    init {
        fetchVenues()
    }

    override fun cancel() {
        job.cancelChildren()
        job.cancel()
    }

    override fun fetchVenues() {
        CoroutineScope(Dispatchers.IO + job).launch {
            zipRequestsUtil.issueApiCall(GetVenuesRequest()).fold(
                ifValue = { venues ->
                    onVenueFetched(
                        allVenues = venues.associateBy { it.id },
                        venues = venues.filter { venue ->
                            venue.isTopLevel && (
                                    venue.venueType == GetVenuesRequest.CWH_VENUE_TYPE
                                            || venue.venueType == GetVenuesRequest.HOUSE_VENUE_TYPE
                                            || venue.venueType == HouseType.STUDIO.name)
                        })
                },
                ifEmpty = { onVenueFetched() },
                ifError = { Either.Error(it) }
            )
        }
    }

    private fun onVenueFetched(
        allVenues: Map<String, Venue> = emptyMap(),
        venues: List<Venue> = emptyList()
    ) {
        _venues.postValue(venues)
        _allVenues.postValue(allVenues)
    }
}