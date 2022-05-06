package com.sohohouse.seven.common.views

interface ExpandableListView {

    var expanded: Boolean

    interface Listener {

        fun onExpandableListChanged(expanded: Boolean)

    }
}