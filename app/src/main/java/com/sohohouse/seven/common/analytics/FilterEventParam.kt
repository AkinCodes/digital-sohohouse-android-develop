package com.sohohouse.seven.common.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.noticeboard.NoticeboardPost

object FilterEventParam {

    fun withPostId(id: String?) = Bundle().apply {
        putString(BundleKeys.NOTICEBOARD_POST_ID, id)
    }

    fun withPost(postID: String, houseTag: String, citiTag: String, topicTag: String): Bundle {
        return bundleOf(
            BundleKeys.NOTICEBOARD_POST_ID to postID,
            BundleKeys.FILTERS_POST_TAGS_HOUSE to houseTag,
            BundleKeys.FILTERS_POST_TAGS_CITY to citiTag,
            BundleKeys.FILTERS_POST_TAGS_TOPIC to topicTag,
        )
    }

    fun withReply(postID: String): Bundle {
        return bundleOf(BundleKeys.NOTICEBOARD_POST_ID to postID)
    }

    fun withPost(post: NoticeboardPost?) = Bundle().apply {
        if (post == null) return@apply
        putString(BundleKeys.NOTICEBOARD_POST_ID, post.postId)
        putString(BundleKeys.FILTERS_POST_TAGS_HOUSE, post.house?.id)
        putString(BundleKeys.FILTERS_POST_TAGS_CITY, post.city?.id)
        putString(BundleKeys.FILTERS_POST_TAGS_TOPIC, post.topic?.id)
    }

    fun withProfileId(id: String?): Bundle = Bundle().apply {
        putString(BundleKeys.NOTICEBOARD_PROFILE_ID, id)
    }

    fun withFilters(map: Map<FilterType, Collection<Filter>>) = Bundle().apply {
        map.entries.forEach { entry ->
            val key = when (entry.key) {
                FilterType.HOUSE_FILTER -> BundleKeys.FILTERS_POST_TAGS_HOUSE
                FilterType.CITY_FILTER -> BundleKeys.FILTERS_POST_TAGS_CITY
                FilterType.TOPIC_FILTER -> BundleKeys.FILTERS_POST_TAGS_TOPIC
                FilterType.INDUSTRY_FILTER -> BundleKeys.FILTERS_POST_TAGS_INDUSTRIES
            }

            putString(key, entry.value.joinToString(",") { it.id })
        }
    }

    fun withTags(id: String? = null, tags: Map<FilterType, Filter?>?) = Bundle().apply {
        putString(BundleKeys.NOTICEBOARD_POST_ID, id)
        tags?.entries?.forEach {
            val key = when (it.key) {
                FilterType.HOUSE_FILTER -> BundleKeys.FILTERS_POST_TAGS_HOUSE
                FilterType.CITY_FILTER -> BundleKeys.FILTERS_POST_TAGS_CITY
                FilterType.TOPIC_FILTER -> BundleKeys.FILTERS_POST_TAGS_TOPIC
                FilterType.INDUSTRY_FILTER -> BundleKeys.FILTERS_POST_TAGS_INDUSTRIES
            }
            putString(key, it.value?.id)
        }
    }

}