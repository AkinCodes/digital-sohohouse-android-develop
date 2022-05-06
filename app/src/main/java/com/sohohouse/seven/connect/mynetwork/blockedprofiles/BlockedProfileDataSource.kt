package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Loadable

class BlockedProfileDataSource(
    private val viewModel: BlockedProfilesViewModel,
    loadable: Loadable.ViewModel
) : PageKeyedDataSource<Int, DiffItem>(),
    Loadable.ViewModel by loadable {

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {}

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DiffItem>
    ) {
        viewModel.getBlockedProfiles(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {}

    class Factory(
        private val viewModel: BlockedProfilesViewModel,
        private val loadable: Loadable.ViewModel
    ) : DataSource.Factory<Int, DiffItem>() {

        private var dataSource: DataSource<Int, DiffItem>? = null

        override fun create(): DataSource<Int, DiffItem> {
            return BlockedProfileDataSource(
                viewModel,
                loadable
            ).also {
                dataSource = it
            }
        }

        fun invalidate() = dataSource?.invalidate()
    }
}