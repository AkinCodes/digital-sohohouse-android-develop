package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.network.core.models.Address


fun Address.formatAddress(): String {
    return this.lines?.let {
        if (it.isNotEmpty()) {
            val newAddressLines = it.toMutableList()
            newAddressLines.concatenateWithNewLine()
        } else {
            listOf(this.locality ?: "", this.country ?: "").concatenateWithNewLine()
        }
    } ?: listOf(this.locality ?: "", this.country ?: "").concatenateWithNewLine()
}