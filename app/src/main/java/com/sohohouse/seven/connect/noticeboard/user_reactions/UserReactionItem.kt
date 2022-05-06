package com.sohohouse.seven.connect.noticeboard.user_reactions

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeBoardReaction

data class UserReactionItem(
    val profileItem: ProfileItem,
    val reaction: NoticeBoardReaction,
    val onProfileClick: () -> Unit,
) : DiffItem {
    override val key = this
}