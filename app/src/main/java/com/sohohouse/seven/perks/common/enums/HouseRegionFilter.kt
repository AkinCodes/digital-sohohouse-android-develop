package com.sohohouse.seven.perks.common.enums

import com.sohohouse.seven.R

enum class HouseRegionFilter(val id: String, val resourceString: Int) {
    EUROPE("EUROPE", R.string.region_eu),
    WORLDWIDE("WORLDWIDE", R.string.region_ww),
    NORTH_AMERICA("NORTH_AMERICA", R.string.region_na),
    GLOBAL("GLOBAL", R.string.global_perk_label),
    UK("UK", R.string.region_uk),
    CWH("CWH", R.string.region_cwh)
}