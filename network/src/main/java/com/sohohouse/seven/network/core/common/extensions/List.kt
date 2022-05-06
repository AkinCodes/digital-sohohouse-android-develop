package com.sohohouse.seven.network.core.common.extensions

fun <T> List<T>?.nullIfEmpty() = if (this.isNullOrEmpty()) null else this
