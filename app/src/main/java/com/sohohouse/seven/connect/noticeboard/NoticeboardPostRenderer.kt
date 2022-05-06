package com.sohohouse.seven.connect.noticeboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.common.renderers.SimpleRenderer
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.databinding.ItemNoticeboardPostBinding
import com.sohohouse.seven.network.core.models.Reaction

class NoticeboardPostRenderer(
    private val onProfileClick: (profile: ProfileItem) -> Unit,
    private val onReplyClick: (post: NoticeboardPost) -> Unit,
    private val onDeleteClick: (post: NoticeboardPost) -> Unit,
    private val onTopicClick: ((topic: Filter) -> Unit)? = null,
    private val onHouseClick: ((house: Filter) -> Unit)? = null,
    private val onCityClick: ((city: Filter) -> Unit)? = null,
    private val onReactionClick: ((postId: String) -> Unit)? = null,
    private val onReactToPostClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit),
    private val onRemoveReactionClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit)? = null,
    private val onReactionLongPress: ((reaction: Reaction?, postID: NoticeboardPost) -> Unit)? = null,
) : BaseRenderer<NoticeboardPost, NoticeboardPostViewHolder>(NoticeboardPost::class.java) {

    override fun bindViewHolder(item: NoticeboardPost, holder: NoticeboardPostViewHolder) {
        holder.bind(item)
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_noticeboard_post
    }

    override fun createViewHolder(view: View): NoticeboardPostViewHolder {
        val binding = ItemNoticeboardPostBinding.bind(view)
        return NoticeboardPostViewHolder(
            binding,
            onProfileClick = onProfileClick,
            onReplyClick = onReplyClick,
            onDeleteClick = onDeleteClick,
            onTopicClick = onTopicClick,
            onHouseClick = onHouseClick,
            onCityClick = onCityClick,
            onReactionClick = onReactionClick,
            onReactToPostClick = onReactToPostClick,
            onRemoveReactionClick = onRemoveReactionClick,
            onReactionLongPress = onReactionLongPress
        )
    }
}

class LoadingItemRenderer : SimpleRenderer<LoadingItem>(LoadingItem::class.java) {
    override fun bindViewHolder(item: LoadingItem?, holder: RecyclerView.ViewHolder?) {
    }

    override fun getLayoutResId() = R.layout.component_list_loading
}
