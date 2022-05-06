package com.sohohouse.seven.common.views

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Only shows if dialog is not already showing
 */
fun DialogFragment.showSafe(
    fragmentManager: FragmentManager,
    tag: String = javaClass.simpleName
) {
    if (fragmentManager.findFragmentByTag(tag) == null) {
        show(fragmentManager, tag)
    }
}