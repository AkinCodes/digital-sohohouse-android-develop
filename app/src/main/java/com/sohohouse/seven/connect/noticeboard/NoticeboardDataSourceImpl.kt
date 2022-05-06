package com.sohohouse.seven.connect.noticeboard

import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.FilterType

abstract class NoticeboardDataSource : PageKeyedDataSource<String, DiffItem>() {
    abstract fun remove(postId: String)
    abstract fun update(post: NoticeboardPost)
    abstract fun refresh()
}

class NoticeboardDataSourceImpl(
    private val profileId: String?,
    private val filterManager: FilterManager,
    private val repo: NoticeboardRepository,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel,
) : NoticeboardDataSource(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    private val includeCreatePostItem: Boolean get() = this.profileId.isNullOrEmpty()

    override fun refresh() {
        repo.invalidateCachedList()
        invalidate()
    }

    override fun remove(postId: String) {
        repo.deleteFromCache(postId)
        invalidate()
    }

    override fun update(post: NoticeboardPost) {
        repo.replaceCachedPost(post)
        invalidate()
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, DiffItem>,
    ) {
        setLoadingState(LoadingState.Loading)
        repo.getPosts(
            profileId = profileId,
            venues = filterManager.get(FilterType.HOUSE_FILTER).map { it.id },
            cities = filterManager.get(FilterType.CITY_FILTER).map { it.id },
            topics = filterManager.get(FilterType.TOPIC_FILTER).map { it.id },
            isInitialLoad = true
        ).fold(
            ifValue = { pagedPostsData ->
                val items = mapToAdapterItems(pagedPostsData.items, isFirstPage = true)
                    .toMutableList().apply {
                        if (includeCreatePostItem) {
                            add(0, CreatePostItem)
                        }
                    }
                callback.onResult(items, null, pagedPostsData.nextPageKey)
            },
            ifEmpty = { callback.onResult(emptyList(), null, null) },
            ifError = { showError() })
        setLoadingState(LoadingState.Idle)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, DiffItem>) {
        setLoadingState(LoadingState.Loading)
        repo.getPosts(
            profileId = profileId,
            venues = filterManager.get(FilterType.HOUSE_FILTER).map { it.id },
            cities = filterManager.get(FilterType.CITY_FILTER).map { it.id },
            topics = filterManager.get(FilterType.TOPIC_FILTER).map { it.id },
            isInitialLoad = false,
            nextPageCursor = params.key
        ).fold(
            ifValue = { pagedPostsData ->
                val items = mapToAdapterItems(pagedPostsData.items, isFirstPage = false)
                callback.onResult(items, pagedPostsData.nextPageKey)
            },
            ifEmpty = { callback.onResult(emptyList(), null) },
            ifError = { ErrorReporter.logException(Throwable("Error fetching next page of posts: $it")) })
        setLoadingState(LoadingState.Idle)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, DiffItem>) {
        //do nothing
    }

    private fun mapToAdapterItems(
        checkins: List<NoticeboardPost>,
        isFirstPage: Boolean,
    ): List<DiffItem> {
        return if (isFirstPage && checkins.isEmpty()) {
            listOf(NoticeboardEmptyStateItem)
        } else {
            checkins
        }
    }

}
