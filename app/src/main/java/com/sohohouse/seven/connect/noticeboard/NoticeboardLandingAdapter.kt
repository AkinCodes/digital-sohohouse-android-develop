package com.sohohouse.seven.connect.noticeboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.databinding.ItemCreatePostBinding
import com.sohohouse.seven.databinding.ItemNoticeboardEmptyStateBinding
import com.sohohouse.seven.databinding.ItemNoticeboardPostBinding
import com.sohohouse.seven.network.core.models.Reaction

class NoticeboardLandingAdapter constructor(
    private val onProfileClick: (profile: ProfileItem) -> Unit,
    private val onReplyClick: (post: NoticeboardPost) -> Unit,
    private val onDeletePostClick: (post: NoticeboardPost) -> Unit,
    private val onCreatePostClick: () -> Unit,
    private val onTopicClick: (topic: Filter) -> Unit,
    private val onHouseClick: (house: Filter) -> Unit,
    private val onCityClick: (city: Filter) -> Unit,
    private val onReactionClick: ((postId: String) -> Unit)? = null,
    private val onReactToPostClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit),
    private val onRemoveReactionClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit)? = null,
    private val onReactionLongPress: ((reaction: Reaction?, postID: NoticeboardPost) -> Unit)? = null,
) : PagedListAdapter<DiffItem, RecyclerView.ViewHolder>(DefaultDiffItemCallback()) {

    companion object {
        private const val ITEM_TYPE_POST = 111
        private const val ITEM_TYPE_CREATE_POST = 222
        private const val ITEM_TYPE_EMPTY_STATE = 333
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_POST -> {
                NoticeboardPostViewHolder(
                    ItemNoticeboardPostBinding.inflate(layoutInflater, parent, false),
                    onProfileClick = onProfileClick,
                    onReplyClick = onReplyClick,
                    onPostClick = onReplyClick,
                    onDeleteClick = onDeletePostClick,
                    onTopicClick = onTopicClick,
                    onHouseClick = onHouseClick,
                    onCityClick = onCityClick,
                    onReactionClick = onReactionClick,
                    onReactToPostClick = onReactToPostClick,
                    onRemoveReactionClick = onRemoveReactionClick,
                    onReactionLongPress = onReactionLongPress
                )
            }
            ITEM_TYPE_CREATE_POST -> {
                object : RecyclerView.ViewHolder(ItemCreatePostBinding
                    .inflate(layoutInflater, parent, false)
                    .apply { createPostBtn.clicks { onCreatePostClick() } }
                    .root
                ) {}
            }
            ITEM_TYPE_EMPTY_STATE -> {
                object : RecyclerView.ViewHolder(
                    ItemNoticeboardEmptyStateBinding
                        .inflate(layoutInflater, parent, false)
                        .root
                ) {}
            }
            else -> throw IllegalArgumentException("Invalid viewType!")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoticeboardPostViewHolder -> holder.bind(getItem(position) as NoticeboardPost)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NoticeboardPost -> ITEM_TYPE_POST
            is CreatePostItem -> ITEM_TYPE_CREATE_POST
            is NoticeboardEmptyStateItem -> ITEM_TYPE_EMPTY_STATE
            else -> -1
        }
    }

}
