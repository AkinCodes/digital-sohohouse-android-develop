package com.sohohouse.seven.common.baseclasses

import com.sohohouse.seven.base.filter.Filter

abstract class BaseFilterManager {

    // Default filter for the current flow - global, handpicked, house landing etc.
    internal var defaultFilter: Filter = Filter()

    // Filter applied during the current flow. If not applied then same as default
    internal var appliedFilter: Filter = Filter()

    // Filter current being filled based on user selection on filter screen
    internal var draftFilter: Filter = Filter()

    abstract fun applyDraft()
    abstract fun setToNoFilterState()

}