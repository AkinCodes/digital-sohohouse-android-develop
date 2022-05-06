package com.sohohouse.seven.connect.filter.house

import androidx.annotation.Keep
import com.sohohouse.seven.connect.filter.base.FilterFragment
import com.sohohouse.seven.connect.filter.base.FilterType

@Keep
class HouseFilterFragment : FilterFragment<HouseFilterViewModel>() {
    override val viewModelClass: Class<HouseFilterViewModel>
        get() = HouseFilterViewModel::class.java

    override val filterType: FilterType
        get() = FilterType.HOUSE_FILTER
}