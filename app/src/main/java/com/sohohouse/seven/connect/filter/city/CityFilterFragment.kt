package com.sohohouse.seven.connect.filter.city

import androidx.annotation.Keep
import com.sohohouse.seven.connect.filter.base.FilterFragment
import com.sohohouse.seven.connect.filter.base.FilterType

@Keep
class CityFilterFragment : FilterFragment<CityFilterViewModel>() {
    override val viewModelClass: Class<CityFilterViewModel>
        get() = CityFilterViewModel::class.java

    override val filterType: FilterType
        get() = FilterType.CITY_FILTER
}