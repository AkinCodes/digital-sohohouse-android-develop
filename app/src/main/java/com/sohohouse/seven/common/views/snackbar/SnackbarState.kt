package com.sohohouse.seven.common.views.snackbar

import com.sohohouse.seven.R

enum class SnackbarState(val value: IntArray) {
    DEFAULT(intArrayOf(R.attr.state_default)),
    POSITIVE(intArrayOf(R.attr.state_positive)),
    NEGATIVE(intArrayOf(R.attr.state_negative))
}