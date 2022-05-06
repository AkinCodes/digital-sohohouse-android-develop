package com.sohohouse.seven.connect.noticeboard

import android.os.Parcelable
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeBoardReaction
import com.sohohouse.seven.network.core.models.Reaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoticeboardPost(
    val postId: String,
    val profile: ProfileItem,
    var timeStamp: String,
    var messageContent: String,
    var replyBtnLabel: String? = null,
    val showDeleteBtn: Boolean,
    val currentUserReaction: Reaction? = null,
    val reactions: Map<Reaction, Int> = emptyMap(),
    val topic: Filter? = null,
    val city: Filter? = null,
    val house: Filter? = null,
    val reactionUrls: Map<Reaction, String> = emptyMap(),
    val replies: List<NoticeboardPost> = emptyList(),
) : DiffItem, Parcelable {

    override val key: Any
        get() = hashCode()
}

fun NoticeboardPost.addReaction(reaction: Reaction): NoticeboardPost {
    if (currentUserReaction == reaction) return this
    return copy(currentUserReaction = reaction,
        reactions = reactions.toMutableMap().apply {
            val currentReaction = get(reaction) ?: 0
            set(reaction, currentReaction + 1)
        }
    )
}

fun NoticeboardPost.removeUserReaction(): NoticeboardPost {
    return copy(
        currentUserReaction = null,
        reactions = reactions.toMutableMap().apply {
            val currentReaction = get(currentUserReaction) ?: 0
            if (currentReaction == 1) {
                remove(currentUserReaction)
            } else {
                currentUserReaction?.let { set(it, currentReaction - 1) }
            }
        }
    )
}

object LoadingItem : DiffItem {
    override val key: Any
        get() = javaClass
}

object CreatePostItem : DiffItem {
    override val key: Any
        get() = javaClass
}

object NoticeboardEmptyStateItem : DiffItem {
    override val key: Any
        get() = javaClass
}