package com.sohohouse.seven.base.filter

import androidx.fragment.app.Fragment

abstract class BaseFilterFlowManager {
    lateinit var currentFragmentTag: String

    abstract fun transitionFrom(filterType: FilterType): Fragment
}