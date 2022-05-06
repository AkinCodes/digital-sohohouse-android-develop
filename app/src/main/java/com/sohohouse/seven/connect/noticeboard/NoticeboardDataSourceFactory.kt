package com.sohohouse.seven.connect.noticeboard

import androidx.paging.DataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.connect.filter.FilterManager

class NoticeboardDataSourceFactory(
    private val filterManager: FilterManager,
    private val repo: NoticeboardRepository,
) : DataSource.Factory<String, DiffItem>(),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    var profileId: String? = null

    override fun create(): DataSource<String, DiffItem> {
        return NoticeboardDataSourceImpl(
            profileId = profileId,
            filterManager = filterManager,
            repo = repo,
            loadable = this,
            errorable = this
        )
    }

}