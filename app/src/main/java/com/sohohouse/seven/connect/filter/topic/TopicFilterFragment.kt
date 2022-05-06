package com.sohohouse.seven.connect.filter.topic

import androidx.annotation.Keep
import com.sohohouse.seven.connect.filter.base.FilterFragment
import com.sohohouse.seven.connect.filter.base.FilterType

@Keep
class TopicFilterFragment : FilterFragment<TopicFilterViewModel>() {
    override val viewModelClass: Class<TopicFilterViewModel>
        get() = TopicFilterViewModel::class.java

    override val filterType: FilterType
        get() = FilterType.TOPIC_FILTER
}