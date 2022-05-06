package com.sohohouse.seven.discover.housenotes

import androidx.paging.DataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable

class HouseNotesDataSourceFactory(private val repo: HouseNotesRepo) :
    DataSource.Factory<Int, DiffItem>(),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private var dataSource: DataSource<Int, DiffItem>? = null

    override fun create(): DataSource<Int, DiffItem> {
        return HouseNotesDataSource(repo = repo, loadable = this, errorable = this).also {
            dataSource = it
        }
    }

    fun invalidate() {
        dataSource?.invalidate()
    }
}