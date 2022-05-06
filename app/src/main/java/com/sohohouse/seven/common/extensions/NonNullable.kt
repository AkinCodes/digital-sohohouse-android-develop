package com.sohohouse.seven.common.extensions

/**
 * Extension to check if a collection of values are not null
 */
fun <T : Any, R : Any> Collection<T?>.whenAllNotNull(block: (List<T>) -> R) {
    if (this.all { it != null }) {
        block(this.filterNotNull())
    }
}

/**
 * Extension to check if two values are not null and gives the result in a lambda
 */
fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (p1: T1, p2: T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

/**
 * Extension which check if this char sequence is not empty and gives the result in a lambda
 */
inline fun String.isNotEmpty(block: (String) -> Unit) {
    if (this.isNotEmpty()) block(this)
}