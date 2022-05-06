package com.sohohouse.seven.connect.noticeboard.post_details

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FilterEventParam
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.noticeboard.*
import com.sohohouse.seven.connect.noticeboard.NoticeboardRepository.Companion.DELAY_FOR_POST_REFRESH_REQUEST
import com.sohohouse.seven.connect.noticeboard.post_details.adapter.NoticeboardPostReply
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.Reaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeboardPostDetailsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val repo: NoticeboardRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val filterManager: FilterManager,
    private val sohoApiService: SohoApiService,
    private val userManager: UserManager,
) : BaseViewModel(analyticsManager, ioDispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _items = MutableLiveData<List<DiffItem>>(listOf(LoadingItem))
    val items: LiveData<List<DiffItem>> get() = _items

    private val _replyLoadingState = MutableLiveData<LoadingState>()
    val replyLoadingState: LiveData<LoadingState> get() = _replyLoadingState

    val postNotFoundEvent = LiveEvent<Any>()
    val originalPostDeletedEvent = LiveEvent<Any>()
    val showProfile = MutableSharedFlow<ProfileItem>(extraBufferCapacity = 1)
    val onDeleteClick = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private var postItem: NoticeboardPost? = null
    private lateinit var originalPostId: String

    init {
        _replyLoadingState.postValue(LoadingState.Idle)
    }

    fun init(postId: String?) {
        if (!this::originalPostId.isInitialized) {
            if (postId != null) {
                this.originalPostId = postId
                showCachedPost()
                fetchPostWithReplies()
            } else {
                ErrorReporter.logException(Throwable("Post ID is null"))
                showErrorView()
            }
        }
    }

    private fun showCachedPost() {
        repo.getCachedPost(originalPostId)?.let { showCachedPostWithLoading(it) }
    }

    private fun showCachedPostWithLoading(post: NoticeboardPost) {
        this.postItem = post
        _items.postValue(listOf(post, LoadingItem))
    }

    private fun fetchPostWithReplies() {
        viewModelScope.launch(viewModelContext) {
            setLoading()
            fetchFullPost()
            setIdle()
        }
    }

    private suspend fun fetchFullPost() {
        val post = repo.getPost(originalPostId)
        if (post.isSuccessful()) {
            mapAndShowPostAndReplies(post.response)
        } else {
            postNotFoundEvent.postEvent()
        }
    }

    private fun mapAndShowPostAndReplies(post: NoticeboardPost) {
        val replies = post.replies
        val itemList = listOf(post) + replies.map { reply ->
            NoticeboardPostReply(
                reply = reply,
                showDeleteButton = reply.profile.id == userManager.profileID,
                onProfileClick = {
                    showProfile.tryEmit(reply.profile)
                },
                onDeleteClick = {
                    onDeleteClick.tryEmit(reply.postId)
                }
            )
        }
        _items.postValue(itemList)
    }

    override fun reloadDataAfterError() {
        fetchPostWithReplies()
    }

    fun refresh() {
        fetchPostWithReplies()
    }

    fun onReplySubmitted(reply: String) {
        viewModelScope.launch(viewModelContext) {
            _replyLoadingState.postValue(LoadingState.Loading)

            analyticsManager.logEventAction(
                AnalyticsManager.Action.NoticeboardPostDetailReplySubmit,
                FilterEventParam.withPost(postItem)
            )

            repo.createPost(reply, parentId = originalPostId).fold(ifValue = { reply ->
                fetchFullPost()
            }, ifEmpty = {
                handleError(null)
            }, ifError = {
                handleError(it)
            })
            _replyLoadingState.postValue(LoadingState.Idle)
        }

    }

    fun deletePost(postID: String, houseTag: String, cityTag: String, topicTag: String) {
        deleteItem(postID, FilterEventParam.withPost(postID, houseTag, cityTag, topicTag))
    }

    fun deleteReply(postID: String) {
        deleteItem(postID, FilterEventParam.withReply(postID))
    }

    private fun deleteItem(postID: String, analyticsArguments: Bundle) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            val isOriginalPost = postID == originalPostId
            analyticsManager.logEventAction(
                AnalyticsManager.Action.NoticeboardPostDeleteConfirm,
                analyticsArguments
            )

            repo.deletePost(postID).fold(ifValue = {
            }, ifEmpty = {
                if (isOriginalPost) {
                    originalPostDeletedEvent.postEvent()
                } else {
                    removeItem(postID)
                }
            }, ifError = {
            })
        }
    }

    private fun removeItem(postId: String) {
        val items = items.value ?: return
        this._items.postValue(items.toMutableList().apply {
            val index = indexOfFirst { it.key == postId }.takeUnless { it == -1 } ?: return
            removeAt(index)
        })
    }

    fun updateFiltersHouse(house: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.HOUSE_FILTER, listOf(house))
    }

    fun updateFiltersCity(city: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.CITY_FILTER, listOf(city))
    }

    fun updateFiltersTopic(topic: Filter) {
        filterManager.clear()
        filterManager.set(FilterType.TOPIC_FILTER, listOf(topic))
    }

    fun onBackPressed() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostDetailBack,
            FilterEventParam.withPost(postItem)
        )
    }

    fun onCancelDelete(post: NoticeboardPost?) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostDeleteBack,
            FilterEventParam.withPost(post)
        )
    }

    fun onCancelReplyDelete(postID: String) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardPostDeleteBack,
            FilterEventParam.withReply(postID)
        )
    }

    fun reactToPost(reaction: Reaction, post: NoticeboardPost) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            logReactionEvents(post.postId, reaction, AnalyticsManager.Action.NoticeboardReactionTap)
            val response = sohoApiService.reactToPost(post.postId, reaction.toString())
            if (response.isSuccessful()) {
                mapAndShowPostAndReplies(post.addReaction(reaction))
            } else {
                showErrorView()
            }
        }
    }

    fun removeReactionFromPost(reaction: Reaction, post: NoticeboardPost) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            logReactionEvents(post.postId,
                reaction,
                AnalyticsManager.Action.NoticeboardReactionUntap)
            val result = sohoApiService.removeReactionFromPost(post.postId)
            if (result.isSuccessful()) {
                mapAndShowPostAndReplies(post.removeUserReaction())
            } else {
                showErrorView()
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
                NoticeboardPostDetailsActivity.TAG,
                reaction
            )
        )
    }
}
