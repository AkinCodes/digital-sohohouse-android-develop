package com.sohohouse.seven.connect.noticeboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FilterEventParam
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.noticeboard.NoticeboardRepository.Companion.DELAY_FOR_POST_REFRESH_REQUEST
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.Reaction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection.HTTP_NOT_FOUND

class NoticeboardLandingViewModel @AssistedInject constructor(
    @Assisted private val profileId: String?,
    private val dataSourceFactory: NoticeboardDataSourceFactory,
    private val repo: NoticeboardRepository,
    private val filterManager: FilterManager,
    private val sohoApiService: SohoApiService,
    private val ioDispatcher: CoroutineDispatcher,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by dataSourceFactory,
    Errorable.ViewModel by dataSourceFactory {

    val items: LiveData<PagedList<DiffItem>> = LivePagedListBuilder(
        dataSourceFactory,
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(NoticeboardRepository.NOTICEBOARD_LANDING_PER_PAGE)
            .setPrefetchDistance(3)
            .build()
    ).build()

    private val _filters = MutableLiveData<List<Filter>>()
    val filters: LiveData<List<Filter>>
        get() = _filters

    private val _scrollToTopEvent = LiveEvent<Any>()
    val scrollToTopEvent: LiveEvent<Any> get() = _scrollToTopEvent

    private val dataSource: NoticeboardDataSource?
        get() = items.value?.dataSource as? NoticeboardDataSource?

    init {
        dataSourceFactory.profileId = profileId
        filterManager.clear()
        repo.invalidateCachedList()
        refresh()
    }

    fun refresh() {
        _filters.postValue(mutableListOf<Filter>().apply {
            addAll(filterManager.get(FilterType.HOUSE_FILTER))
            addAll(filterManager.get(FilterType.CITY_FILTER))
            addAll(filterManager.get(FilterType.TOPIC_FILTER))
        })
        dataSource?.refresh()
        _scrollToTopEvent.postEvent()
    }

    fun checkUpdateFilters() {
        val latest = filterManager.asList()
        val existing = filters.value ?: emptyList()
        val unChanged = latest.containsAll(existing) && existing.containsAll(latest)
        if (!unChanged) {
            refresh()
        }
    }

    fun onPostUpdated(postID: String?) {
        if (postID == null) return
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            val newPost = repo.getPost(postID)
            if (newPost.isSuccessful()) {
                dataSource?.update(newPost.response)
            } else {
                if (newPost.code == HTTP_NOT_FOUND) {
                    dataSource?.remove(postID)
                }
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            repo.deletePost(postId).fold(ifValue = {
            }, ifEmpty = {
                dataSource?.remove(postId)
            }, ifError = {
            })
        }
    }

    fun removeFilter(filter: Filter) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardFilterDeselect,
            FilterEventParam.withFilters(filterManager.asMap())
        )
        filterManager.remove(filter)
        refresh()
    }

    fun onHouseTagClick(house: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.HOUSE_FILTER, listOf(house))
        refresh()

        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardFilterPostTag,
            FilterEventParam.withFilters(filterManager.asMap())
        )
    }

    fun onCityTagClick(city: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.CITY_FILTER, listOf(city))
        refresh()

        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardFilterPostTag,
            FilterEventParam.withFilters(filterManager.asMap())
        )
    }

    fun onTopicClick(topic: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.TOPIC_FILTER, listOf(topic))
        refresh()

        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardFilterPostTag,
            FilterEventParam.withFilters(filterManager.asMap())
        )
    }

    fun onWritePostClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NoticeboardPost)
    }

    fun onFilterButtonClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NoticeboardFilter)
    }

    fun onReplyClick(post: NoticeboardPost) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostTap,
            FilterEventParam.withPostId(post.postId)
        )
    }

    fun onDeletePostClick(post: NoticeboardPost) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostDelete,
            FilterEventParam.withPost(post)
        )
    }

    fun onProfileClick(id: String) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostDelete,
            FilterEventParam.withProfileId(id)
        )
    }

    fun onPostDeleted(postId: String) {
        dataSource?.remove(postId)
    }

    fun reactToPost(reaction: Reaction, post: NoticeboardPost) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            logReactionEvents(post.postId, reaction, AnalyticsManager.Action.NoticeboardReactionTap)
            val response = sohoApiService.reactToPost(post.postId, reaction.toString())
            if (response.isSuccessful()) {
                dataSource?.update(post.addReaction(reaction))
            } else {
                showError(stringProvider.getString(R.string.error_general))
            }
        }
    }

    fun removeReactionFromPost(reaction: Reaction, post: NoticeboardPost) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            logReactionEvents(post.postId, reaction, AnalyticsManager.Action.NoticeboardReactionUntap)
            val result = sohoApiService.removeReactionFromPost(post.postId)
            if (result.isSuccessful()) {
                dataSource?.update(post.removeUserReaction())
            } else {
                showError(stringProvider.getString(R.string.error_general))
            }
        }
    }

    fun logReactionEvents(
        postID: String,
        reaction: Reaction?,
        noticeboardReactionTap: AnalyticsManager.Action,
    ) {
        analyticsManager.logEventAction(
            noticeboardReactionTap,
            AnalyticsManager.NoticeboardReactions.getParams(
                postID,
                NoticeboardLandingFragment.TAG,
                reaction
            )
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String?): NoticeboardLandingViewModel
    }
}
