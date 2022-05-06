package com.sohohouse.seven.common.extensions

fun <K, V> Map<K, V>.getValue(key: K, default: V): V {
    return get(key) ?: default
}