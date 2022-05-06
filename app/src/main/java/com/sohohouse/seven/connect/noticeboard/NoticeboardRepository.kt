package com.sohohouse.seven.connect.noticeboard

import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.map
import com.sohohouse.seven.network.core.models.Checkin
import com.sohohouse.seven.network.core.request.DeleteRollCallRequest
import com.sohohouse.seven.network.core.request.GetNoticeboardPostsRequest
import com.sohohouse.seven.network.core.request.PostRollCallRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeboardRepository @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val sohoApiService: SohoApiService,
    private val itemFactory: NoticeboardItemFactory,
) {

    private var cachedPosts: PagedPostsData? = null

    fun invalidateCachedList() {
        cachedPosts = null
    }

    fun getPosts(
        profileId: String?,
        venues: Collection<String>,
        cities: Collection<String>,
        topics: Collection<String>,
        includeProfile: Boolean = true,
        pageSize: Int = NOTICEBOARD_LANDING_PER_PAGE,
        isInitialLoad: Boolean,
        nextPageCursor: String? = null,
    ): Either<ServerError, PagedPostsData> {
        if (isInitialLoad && cachedPosts != null) {
            return value(cachedPosts!!)
        }
        val request = if (profileId.isNullOrEmpty()) {
            GetNoticeboardPostsRequest(
                venueIds = venues.toTypedArray().formatWithCommas(),
                cities = cities.toTypedArray().formatWithCommas(),
                topics = topics.toTypedArray().formatWithCommas(),
                includeProfile = includeProfile,
                nextCursor = nextPageCursor,
                perPage = pageSize
            )
        } else {
            GetNoticeboardPostsRequest(
                profileId = profileId,
                includeProfile = includeProfile,
                includeVenue = false, //inclusion of venue cases auth error when fetching user's posts
                nextCursor = nextPageCursor,
                perPage = pageSize
            )
        }
        return zipRequestsUtil.issueApiCall(request).fold(ifValue = {
            val nextPageKey = GetNoticeboardPostsRequest.getMeta(it)?.page?.cursor?.next

            if (isInitialLoad) {
                cachedPosts = PagedPostsData(
                    items = it.map { post -> itemFactory.mapToItem(post, true) }.toMutableList(),
                    nextPageKey = nextPageKey
                )
            } else {
                cachedPosts?.let { cachedPosts ->
                    cachedPosts.items.addAll(it.map { post ->
                        itemFactory.mapToItem(post, true)
                    })
                    cachedPosts.nextPageKey = nextPageKey
                }
            }

            value(PagedPostsData(it.map { itemFactory.mapToItem(it, true) }.toMutableList(),
                nextPageKey))
        }, ifError = {
            error(it)
        }, ifEmpty = {
            empty()
        })
    }

    suspend fun getPost(postId: String, showReplyCount: Boolean = false): ApiResponse<NoticeboardPost> {
        return sohoApiService.getNoticeboardPost(postId, true)
            .map {
                itemFactory.mapToItem(it, showReplyCount)
            }
    }

    fun createPost(
        content: String,
        venueID: String? = null,
        parentId: String? = null,
        city: String? = null,
        theme: String? = null,
    ): Either<ServerError, Checkin> {
        return zipRequestsUtil.issueApiCall(
            PostRollCallRequest(
                status = content,
                venueId = venueID,
                parentId = parentId,
                city = city,
                theme = theme
            )
        )
    }

    fun deletePost(postId: String): Either<ServerError, Void> {
        return zipRequestsUtil.issueApiCall(DeleteRollCallRequest(postId))
    }

    fun deleteFromCache(postId: String) {
        cachedPosts?.items?.let {
            val index = it.indexOfFirst { it.postId == postId }.takeUnless { it == -1 } ?: return
            it.removeAt(index)
        }
    }

    fun replaceCachedPost(post: NoticeboardPost) {
        cachedPosts?.items?.let {
            val index =
                it.indexOfFirst { it.postId == post.postId }.takeUnless { it == -1 } ?: return
            it.set(index, post)
        }
    }

    fun getCachedPost(postId: String): NoticeboardPost? {
        return cachedPosts?.items?.firstOrNull { it.postId == postId }
    }

    data class PagedPostsData(val items: MutableList<NoticeboardPost>, var nextPageKey: String?)

    companion object {
        const val NOTICEBOARD_LANDING_PER_PAGE = 10
        const val DELAY_FOR_POST_REFRESH_REQUEST = 1000L
    }

}