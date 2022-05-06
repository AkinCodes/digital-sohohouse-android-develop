package com.sohohouse.seven.connect.mynetwork.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectionRequestsViewModel @Inject constructor(
    private val repo: ConnectionRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val dataSourceFactory =
        ConnectRequestsDataSource.Factory(repo, viewModelScope, viewModelContext, this, this)

    val connectionRequests: LiveData<PagedList<ConnectionRequestItem>> = LivePagedListBuilder(
        object : DataSource.Factory<Int, ConnectionRequestItem>() {
            override fun create(): DataSource<Int, ConnectionRequestItem> =
                dataSourceFactory.create()
        },
        PagedList.Config.Builder().setEnablePlaceholders(false)
            .setPageSize(ConnectionRepository.ITEMS_PER_PAGE).build()
    ).build()

    private val _connectionsChanged: LiveEvent<Any> = LiveEvent()

    val connectionsChanged: LiveData<Any>
        get() = _connectionsChanged

    fun refresh() = dataSourceFactory.invalidate()

    fun acceptRequest(id: String) {
        patchConnectionRequest(id, MutualConnectionRequests.STATE_ACCEPTED)
    }

    fun ignoreRequest(id: String) {
        patchConnectionRequest(id, MutualConnectionRequests.STATE_HIDDEN)
    }

    private fun patchConnectionRequest(id: String, status: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            repo.patchConnectionRequest(MutualConnectionRequests(state = status).also {
                it.id = id
            })
                .ifValue {
                    refresh()
                    _connectionsChanged.postValue(Any())
                }
                .ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

}