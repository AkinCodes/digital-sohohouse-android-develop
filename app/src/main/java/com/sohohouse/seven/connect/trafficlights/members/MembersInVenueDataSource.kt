package com.sohohouse.seven.connect.trafficlights.members

import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.network.base.model.Either
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MembersInVenueDataSource(
    private val trafficLightsRepo: TrafficLightsRepo,
    private val coroutineScope: CoroutineScope,
    private val coroutineContext: CoroutineContext,
    private val estimatedTotalFlow: MutableStateFlow<Int?>,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) :
    PageKeyedDataSource<Int, VenueMember>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    private companion object {
        private const val PER_PAGE = 10
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, VenueMember>
    ) {
        setLoading()

        coroutineScope.launch(coroutineContext) {
            trafficLightsRepo
                .getVenueMembers(
                    perPage = PER_PAGE,
                    isInitialLoad = true,
                )
                .fold(
                    ifValue = { venueMembers ->
                        callback.onResult(venueMembers, null, venueMembers.nextPage)
                        estimatedTotalFlow.value = venueMembers.estimatedTotal
                    },
                    ifError = { showError(it.toString()) },
                    ifEmpty = { Either.Empty() }
                )
        }

        setIdle()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, VenueMember>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, VenueMember>) {
        setLoading()

        coroutineScope.launch(coroutineContext) {
            trafficLightsRepo
                .getVenueMembers(perPage = PER_PAGE, page = params.key, isInitialLoad = false)
                .fold(
                    ifValue = { venueMembers ->
                        callback.onResult(venueMembers, venueMembers.nextPage)
                        estimatedTotalFlow.value = venueMembers.estimatedTotal
                    },
                    ifError = { showError(it.toString()) },
                    ifEmpty = { Either.Empty() }
                )
        }

        setIdle()
    }
}
