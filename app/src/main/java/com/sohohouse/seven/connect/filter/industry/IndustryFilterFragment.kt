package com.sohohouse.seven.connect.filter.industry

import androidx.annotation.Keep
import com.sohohouse.seven.connect.filter.base.FilterFragment
import com.sohohouse.seven.connect.filter.base.FilterType

@Keep
class IndustryFilterFragment : FilterFragment<IndustryFilterViewModel>() {
    override val viewModelClass: Class<IndustryFilterViewModel>
        get() = IndustryFilterViewModel::class.java

    override val filterType: FilterType
        get() = FilterType.TOPIC_FILTER
}