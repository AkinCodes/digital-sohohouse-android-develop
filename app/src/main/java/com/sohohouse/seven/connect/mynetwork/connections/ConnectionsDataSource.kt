package com.sohohouse.seven.connect.mynetwork.connections

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.discover.DiscoverMembersAdapterItem
import com.sohohouse.seven.connect.discover.GetDiscoverMembersAdapterItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.shareprofile.ShareProfileAdapterItem
import com.sohohouse.seven.network.core.models.Connection
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.Connected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConnectionsDataSource(
    private val repo: ConnectionRepository,
    private val scope: CoroutineScope,
    private val coroutineContext: CoroutineContext,
    private val userManager: UserManager,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : PageKeyedDataSource<Int, DiffItem>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    private val myId: String = userManager.profileID

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {}

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DiffItem>
    ) {
        scope.launch(coroutineContext) {
            setLoadingState(LoadingState.Loading)
            repo.getConnections(1, ConnectionRepository.ITEMS_PER_PAGE)
                .ifValue {
                    callback.onResult(
                        listOf(ShareProfileAdapterItem()) +
                                listOf(getDiscoverMembers()) + buildItems(it), 1, 2
                    )
                }
                .ifError { showError(it.toString()) }
            setLoadingState(LoadingState.Idle)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {
        scope.launch(coroutineContext) {
            setLoadingState(LoadingState.Loading)
            repo.getConnections(params.key, ConnectionRepository.ITEMS_PER_PAGE)
                .ifValue { callback.onResult(buildItems(it), params.key + 1) }
                .ifError { showError(it.toString()) }
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun buildItems(items: List<Connection>): List<ProfileItem> {
        return items.map {
            ProfileItem(
                getMemberProfile(it),
                Connected,
                Connection.getValidConnectionId(myId, it)
            )
        }
    }

    private fun getMemberProfile(connection: Connection): Profile {
        return connection.sender.get(connection.document).takeIf { it.id != myId }
            ?: connection.receiver.get(connection.document)
    }

    private fun getDiscoverMembers(): GetDiscoverMembersAdapterItem =
        GetDiscoverMembersAdapterItem {
            when {
                userManager.connectRecommendationOptIn.isEmpty() -> DiscoverMembersAdapterItem.ShowOptInPrompt
                //TODO userManager.isProfileEmpty -> DiscoverMembersAdapterItem.ShowCompleteProfilePrompt
                else -> DiscoverMembersAdapterItem.ShowSuggestedPeople
            }
        }

    //private fun getShareProfileAdapterItem() = ShareProfileAdapterItem()

    class Factory(
        private val repo: ConnectionRepository,
        private val userManager: UserManager,
        private val scope: CoroutineScope,
        private val coroutineContext: CoroutineContext,
        private val loadable: Loadable.ViewModel,
        private val errorable: Errorable.ViewModel
    ) : DataSource.Factory<Int, DiffItem>() {

        private var dataSource: DataSource<Int, DiffItem>? = null

        override fun create(): DataSource<Int, DiffItem> {
            return ConnectionsDataSource(
                repo,
                scope,
                coroutineContext,
                userManager,
                loadable,
                errorable
            ).also {
                dataSource = it
            }
        }

        fun invalidate() = dataSource?.invalidate()
    }
}