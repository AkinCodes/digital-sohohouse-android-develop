package com.sohohouse.seven.network.core.common.extensions

fun Array<String>.formatWithCommas(): String? {
    if (isNotEmpty()) {
        return this.joinToString(separator = ",")
    }
    return null
}

fun Array<String>.addBlankIfEmpty() = if (isEmpty()) plus("") else this