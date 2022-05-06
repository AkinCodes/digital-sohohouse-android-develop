package com.sohohouse.seven.connect.mynetwork.requests

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.core.models.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConnectRequestsDataSource(
    private val repo: ConnectionRepository,
    private val scope: CoroutineScope,
    private val coroutineContext: CoroutineContext,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : PageKeyedDataSource<Int, ConnectionRequestItem>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ConnectionRequestItem>
    ) {
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ConnectionRequestItem>
    ) {
        scope.launch(coroutineContext) {
            setLoadingState(LoadingState.Loading)
            repo.getConnectionRequests(1, ConnectionRepository.ITEMS_PER_PAGE)
                .ifValue { callback.onResult(buildItems(it), 1, 2) }
                .ifError { showError(it.toString()) }
            setLoadingState(LoadingState.Idle)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ConnectionRequestItem>
    ) {
        scope.launch(coroutineContext) {
            setLoadingState(LoadingState.Loading)
            repo.getConnectionRequests(params.key, ConnectionRepository.ITEMS_PER_PAGE)
                .ifValue { callback.onResult(buildItems(it), params.key + 1) }
                .ifError { showError(it.toString()) }
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun buildItems(list: List<Connection>): List<ConnectionRequestItem> {
        return list.map { ConnectionRequestItem(it) }
    }

    class Factory(
        private val repo: ConnectionRepository,
        private val scope: CoroutineScope,
        private val coroutineContext: CoroutineContext,
        private val loadable: Loadable.ViewModel,
        private val errorable: Errorable.ViewModel
    ) : DataSource.Factory<Int, ConnectionRequestItem>() {

        private var dataSource: DataSource<Int, ConnectionRequestItem>? = null

        override fun create(): DataSource<Int, ConnectionRequestItem> {
            return ConnectRequestsDataSource(
                repo,
                scope,
                coroutineContext,
                loadable,
                errorable
            ).also {
                dataSource = it
            }
        }

        fun invalidate() = dataSource?.invalidate()
    }
}