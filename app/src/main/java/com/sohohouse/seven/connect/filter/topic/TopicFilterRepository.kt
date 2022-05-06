package com.sohohouse.seven.connect.filter.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterItem
import com.sohohouse.seven.connect.filter.base.FilterRepository
import com.sohohouse.seven.connect.filter.base.Filterable

abstract class TopicFilterRepository : FilterRepository()

class TopicFilterRepositoryImpl(
    private val stringProvider: StringProvider,
    private val userManager: UserManager
) : TopicFilterRepository() {

    override val items: LiveData<List<Filterable>> = loadTopics()

    private fun loadTopics(): LiveData<List<Filterable>> {
        val topics = getAvailableTopics(userManager.membershipType)
            .map {
                FilterItem(
                    filter = Filter(id = it.id, title = stringProvider.getString(it.title)),
                    enabled = true,
                    tag = ""
                )
            }
        return MutableLiveData(topics)
    }

    private fun getAvailableTopics(membershipType: String): List<Topic> {
        return when (membershipType.asEnumOrDefault<MembershipType>()) {
            MembershipType.U27 -> listOf(
                Topic.TopicArtAndDesign,
                Topic.Fashion,
                Topic.FilmAndEntertainment,
                Topic.FoodAndDrink,
                Topic.Music,
                Topic.Travel,
                Topic.Wellbeing,
                Topic.Work,
                Topic.U27
            )
            else -> listOf(
                Topic.TopicArtAndDesign,
                Topic.Fashion,
                Topic.FilmAndEntertainment,
                Topic.FoodAndDrink,
                Topic.Music,
                Topic.Travel,
                Topic.Wellbeing,
                Topic.Work
            )
        }
    }

}