package com.sohohouse.seven.connect.noticeboard

import com.sohohouse.seven.R
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.topic.Topic
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeboardReactionIconsProvider
import com.sohohouse.seven.network.core.models.Checkin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeboardItemFactory @Inject constructor(
    private val stringProvider: StringProvider,
    private val userManager: UserManager,
    private val venueRepo: VenueRepo,
    private val iconProvider: NoticeboardReactionIconsProvider,
) {

    private fun getReplyBtnLabel(checkin: Checkin, showReplyCount: Boolean): String {
        if (showReplyCount) {
            return when (val replyCount = checkin.repliesResource.get()?.size) {
                0 -> stringProvider.getString(R.string.reply_cta)
                1 -> stringProvider.getString(R.string.reply_one)
                else -> stringProvider.getString(R.string.reply_many, replyCount.toString())
            }
        }
        return stringProvider.getString(R.string.reply_cta)
    }

    fun mapToItem(
        checkin: Checkin,
        showReplyCount: Boolean,
    ): NoticeboardPost {
        val profile = checkin.profile?.let { ProfileItem(it) } ?: ProfileItem()
        val houseTag = getHouseTag(checkin)
        val cityTag = checkin.tags?.city?.firstOrNull()?.let { Filter(it, it) }
        val topicTag = checkin.tags?.theme?.firstOrNull()
            ?.let { Filter(it, Topic.forId(it)?.let { stringProvider.getString(it.title) } ?: "") }

        val reactions = checkin.reactions.reactions.toList()
            .sortedByDescending { it.second }.toMap()

        return NoticeboardPost(
            checkin.id,
            profile = profile,
            messageContent = checkin.status,
            timeStamp = DateUtils.getTimeElapsedLabel(stringProvider, checkin.createdAt),
            replyBtnLabel = getReplyBtnLabel(checkin, showReplyCount),
            showDeleteBtn = profile.id == userManager.profileID,
            topic = topicTag,
            city = cityTag,
            house = houseTag,
            reactions = reactions,
            currentUserReaction = checkin.userReaction?.icon,
            reactionUrls = iconProvider.getAllReactions(),
            replies = checkin.replies.filterNotNull().map {
                mapToItem(it, false)
            }
        )
    }

    private fun getHouseTag(checkin: Checkin): Filter? {
        var venue = checkin.venues.firstOrNull()
        return if (venue != null) {
            Filter(venue.id, venue.name)
        } else {
            val venueID = checkin.venueResource.firstOrNull()?.id
            venue = venueRepo.venues().find { it.id == venueID }
            return venue?.let { Filter(it.id, it.name) }
        }
    }

}
