package com.sohohouse.seven.home.suggested_people

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.network.core.models.RecommendationDto

class SuggestedAdapterItem(
    val isOptedInForSuggestions: Boolean,
    val suggestedMembers: List<SuggestedPeopleAdapterItem>,
) : DiffItem {
    override val key: Any = this
}


sealed class SuggestedPeopleAdapterItem : DiffItem {

    abstract val viewType: Int

    data class WildCard(
        val avatarUrl: String,
        val id: String,
    ) : SuggestedPeopleAdapterItem() {

        override val viewType: Int = WILD_CARD
        override val key: Any = this

        constructor(dto: RecommendationDto) : this(
            dto.profileUrl ?: "",
            dto.id ?: "",
        )
    }

    data class NormalItem(
        val avatarUrl: String,
        val name: String,
        val description: String,
        val id: String,
        val reason: SuggestionReason,
    ) : SuggestedPeopleAdapterItem() {

        override val viewType: Int = NORMAL_ITEM
        override val key: Any = this

        constructor(dto: RecommendationDto) : this(
            dto.profileUrl ?: "",
            dto.firstname.plus(" ").plus(dto.lastname),
            dto.profession ?: "",
            dto.id ?: "a",
            SuggestionReason.parseReason(dto)
        )
    }

    data class Placeholder(val imageResId: Int) : SuggestedPeopleAdapterItem() {
        override val viewType: Int = PLACEHOLDER
        override val key: Any = this
    }

    companion object {
        const val PLACEHOLDER = 0
        const val NORMAL_ITEM = 1
        const val WILD_CARD = 2
    }
}

enum class SuggestionReason(val reason: String, @StringRes val stringRes: Int) {
    EVENT_ATTENDED("event_attended", R.string.reason_event_attended),
    MUTUAL_CONNECTION("mutual_connection", R.string.reason_mutual_connection),
    INDUSTRY("industry", R.string.reason_industry),
    POSITION("Position", R.string.reason_position),
    HOUSE_LOCAL("house_local", R.string.reason_local_house),
    PROFILE_CITY("profile_city", R.string.reason_profile_city),
    HOUSE_FAVORITE("house_favorite", R.string.reason_favourite_house),
    INTERESTS_PROFILE("interests_profile", R.string.reason_interests_profile),
    HOUSE_VISITED("house_visited", R.string.reason_house_visited),
    PROPOSER("proposer", R.string.reason_empty),
    PROPOSEE("proposee", R.string.reason_empty),
    INTERACTION("interaction", R.string.reason_empty),
    NOTICEBOARD_INTEREST_TAG("noticeboard_interest_tag", R.string.reason_empty),
    WILDCARD("wildcard", R.string.reason_wild_card),
    NONE("none", R.string.reason_empty);

    companion object {
        fun parseReason(dto: RecommendationDto): SuggestionReason {
            val reason = dto.reasons?.firstOrNull()
            return values().find { it.reason == reason } ?: NONE
        }
    }
}

fun RecommendationDto.getSuggestedPeopleAdapterItem(): SuggestedPeopleAdapterItem {
    return if (SuggestionReason.parseReason(this) == SuggestionReason.WILDCARD)
        SuggestedPeopleAdapterItem.WildCard(this)
    else
        SuggestedPeopleAdapterItem.NormalItem(this)
}