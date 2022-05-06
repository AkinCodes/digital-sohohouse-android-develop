package com.sohohouse.seven.common.extensions


@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
@Throws(ClassCastException::class)
fun <T : Any> Any.cast(): T {
    return this as T::class
}