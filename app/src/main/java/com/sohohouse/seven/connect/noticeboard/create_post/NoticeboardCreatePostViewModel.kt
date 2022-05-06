package com.sohohouse.seven.connect.noticeboard.create_post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FilterEventParam
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.topic.Topic
import com.sohohouse.seven.connect.noticeboard.NoticeboardRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class NoticeboardCreatePostViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val repo: NoticeboardRepository,
    private val userManager: UserManager,
    private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager), Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _postCreatedEvent = LiveEvent<Any>()
    val postCreatedEvent: LiveData<Any> get() = _postCreatedEvent

    val tags: LiveData<EnumMap<FilterType, Filter?>> get() = _tags
    private val _tags = MutableLiveData<EnumMap<FilterType, Filter?>>()

    init {
        applyDefaultTags()
    }

    private fun applyDefaultTags() {
        if (userManager.membershipType == MembershipType.U27.name) {
            addTag(
                FilterType.TOPIC_FILTER,
                Filter(Topic.U27.id, stringProvider.getString(Topic.U27.title))
            )
        }
    }

    fun onPostSubmit(message: String) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setLoading()
            repo.createPost(
                content = message,
                venueID = tags.value?.get(FilterType.HOUSE_FILTER)?.id,
                city = tags.value?.get(FilterType.CITY_FILTER)?.id,
                theme = tags.value?.get(FilterType.TOPIC_FILTER)?.id
            ).fold(
                ifValue = {
                    _postCreatedEvent.postValue(Any())

                    analyticsManager.logEventAction(
                        AnalyticsManager.Action.NoticeboardPostSubmit,
                        FilterEventParam.withTags(id = it.id, tags = tags.value)
                    )
                },
                ifError = { handleError(it) },
                ifEmpty = { handleError(null) }
            )
            setIdle()
        }
    }

    fun onTagAdded(filterType: FilterType, filter: Filter?) {
        addTag(filterType, filter)
        val action = when (filterType) {
            FilterType.HOUSE_FILTER -> AnalyticsManager.Action.NoticeboardPostHouseAdd
            FilterType.CITY_FILTER -> AnalyticsManager.Action.NoticeboardPostCityAdd
            FilterType.TOPIC_FILTER -> AnalyticsManager.Action.NoticeboardPostTopicAdd
            FilterType.INDUSTRY_FILTER -> return
        }
        analyticsManager.logEventAction(action, FilterEventParam.withTags(tags = tags.value))
    }

    private fun addTag(filterType: FilterType, filter: Filter?) {
        _tags.value = (_tags.value ?: EnumMap(FilterType::class.java)).apply {
            this[filterType] = filter
        }
    }
}