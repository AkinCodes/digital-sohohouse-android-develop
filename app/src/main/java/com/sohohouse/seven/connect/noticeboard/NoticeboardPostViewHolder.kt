package com.sohohouse.seven.connect.noticeboard

import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.databinding.ItemNoticeboardPostBinding
import com.sohohouse.seven.network.core.models.Reaction

class NoticeboardPostViewHolder constructor(
    private val binding: ItemNoticeboardPostBinding,
    private val onProfileClick: (profile: ProfileItem) -> Unit,
    private val onPostClick: ((post: NoticeboardPost) -> Unit)? = null,
    private val onReplyClick: ((post: NoticeboardPost) -> Unit)? = null,
    private val onDeleteClick: ((post: NoticeboardPost) -> Unit)? = null,
    private val onTopicClick: ((topic: Filter) -> Unit)? = null,
    private val onHouseClick: ((house: Filter) -> Unit)? = null,
    private val onCityClick: ((city: Filter) -> Unit)? = null,
    private val onReactionClick: ((postId: String) -> Unit)? = null,
    private val onReactToPostClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit)? = null,
    private val onRemoveReactionClick: ((reaction: Reaction, postID: NoticeboardPost) -> Unit)? = null,
    private val onReactionLongPress: ((reaction: Reaction?, postID: NoticeboardPost) -> Unit)? = null,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.authorName.movementMethod = LinkMovementMethod.getInstance()
    }

    fun bind(item: NoticeboardPost) = binding.run {
        messageContent.clicks { onPostClick?.invoke(item) }
        root.clicks { onPostClick?.invoke(item) }
        setProfilePhoto(item)
        bindName(item)
        timestamp.text = item.timeStamp
        messageContent.setLinkableText(item.messageContent)
        staffIndication.isVisible = item.profile.isStaff

        showHideReplyBtn(item)

        if (item.showDeleteBtn) {
            deletePostBtn.setVisible()
            onDeleteClick?.let { listener -> deletePostBtn.setOnClickListener { listener(item) } }
        } else {
            deletePostBtn.setGone()
        }

        setReactionImages(item.reactions, item.currentUserReaction, item.postId, item.reactionUrls)
        extraReactionsView.isVisible = false
        extraReactionsView.setReactions(item.reactionUrls)
        extraReactionsView.onReactionClick { onReactToPostClick?.invoke(it, item) }
        likeBtn.setOnLongClickListener {
            onReactionLongPress?.invoke(item.currentUserReaction, item)
            showReactionPopup()
            true
        }

        likeBtn.setOnClickListener {
            onReactToPostClick?.invoke(Reaction.THUMBS_UP, item)
        }
        currentReaction.setOnClickListener {
            item.currentUserReaction?.let { reaction ->
                onRemoveReactionClick?.invoke(reaction, item)
            }
        }

        bindTags(item)
    }

    private fun showReactionPopup() {
        binding.apply {
            extraReactionsView.alpha = 0f
            extraReactionsView.isVisible = true
            extraReactionsView.animate().apply {
                alpha(1f)
                duration = 400
            }
        }
    }

    private fun ItemNoticeboardPostBinding.setReactionImages(
        reactions: Map<Reaction, Int>,
        currentUserReaction: Reaction?,
        postId: String,
        reactionUrls: Map<Reaction, String>,
    ) {
        val reactionImageViews =
            listOf(firstReaction, secondReaction, thirdReaction, fourthReaction)
        val isReactedByCurrentUser = currentUserReaction != null

        if (reactions.isNotEmpty()) {
            reactionsCount.text = reactions.values.sum().toString()
        }
        reactionsCount.isVisible = reactions.isNotEmpty()

        reactionImageViews.forEachIndexed { index, imageView ->
            val url = reactionUrls[reactions.keys.elementAtOrNull(index)]

            imageView.isVisible = url?.let {
                Glide.with(context)
                    .load(url)
                    .into(imageView)
                true
            } ?: false
        }
        likeBtn.isVisible = !isReactedByCurrentUser
        currentReaction.isVisible = isReactedByCurrentUser

        if (isReactedByCurrentUser) {
            Glide.with(context)
                .load(reactionUrls[currentUserReaction])
                .into(currentReaction)
        }

        reactionListBtn.setOnClickListener {
            onReactionClick?.invoke(postId)
        }
    }


    private fun bindTags(item: NoticeboardPost) {
        binding.cityTag.apply {
            setVisible(item.city != null)
            text = item.city?.title
            clicks {
                item.city?.let { onCityClick?.invoke(it) }
            }
        }
        binding.houseTag.apply {
            setVisible(item.house != null)
            text = item.house?.title
            clicks {
                item.house?.let { onHouseClick?.invoke(it) }
            }
        }
    }

    private fun bindName(item: NoticeboardPost) {
        val rawString: String
        val name = item.profile.fullName
        val topic = item.topic
        rawString = when {
            topic != null -> {
                getString(R.string.user_posted_in_topic_label, name, topic.title)
            }
            else -> {
                name
            }
        }
        var spannable = rawString.createClickableSpannableForSubstring(name, {
            onProfileClick(item.profile)
        })
        if (topic != null) {
            spannable = spannable.createClickableSpannableForSubstring(topic.title, {
                onTopicClick?.invoke(topic)
            })
        }
        binding.authorName.text = spannable
    }

    private fun setProfilePhoto(item: NoticeboardPost) {
        binding.profileImage.apply {
            setImageFromUrl(
                item.profile.imageUrl,
                isRound = true,
                placeholder = R.drawable.ic_profile
            )
            setOnClickListener { onProfileClick(item.profile) }
        }
    }

    private fun showHideReplyBtn(item: NoticeboardPost) {
        binding.apply {
            replyBtn.setVisible()
            replyBtn.text = item.replyBtnLabel
            onReplyClick?.let { listener -> replyBtn.setOnClickListener { listener(item) } }
        }
    }

}