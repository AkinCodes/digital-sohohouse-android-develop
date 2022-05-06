package com.sohohouse.seven.planner

import androidx.annotation.StringRes

data class PlannerHeaderAdapterItem constructor(
    @StringRes val stringRes: Int, val isSubHeader: Boolean = false,
    val showExtraBottomPadding: Boolean = false,
    var hideExtraTopPadding: Boolean = true
) : BasePlannerAdapterItem(
    if (isSubHeader) PlannerAdapterItemType.SUBLIST_HEADER
    else PlannerAdapterItemType.HEADER
)