package com.sohohouse.seven.common.extensions

fun List<String>.concatenateWithNewLine(): String {
    return this.concatenateWithSymbol("\n")
}

fun List<String>.concatenateWithSpace(): String {
    return this.concatenateWithSymbol(" ")
}

fun List<String>.concatenateWithSymbol(symbol: String): String {
    return this.joinToString(symbol)
}

fun <T> List<T>.one(predicate: (T) -> Boolean): Boolean = count(predicate) == 1

fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean {
    return firstOrNull(predicate) != null
}

inline fun <reified T> arrayOfNonNull(vararg elements: T?): Array<T> {
    return arrayOf(*elements).filterNotNull().toTypedArray()
}

fun <T> MutableCollection<T>.addNonNull(element: T?) {
    if (element != null) add(element)
}

fun <T> MutableCollection<T>.addAllNonNull(elements: Collection<T>) {
    addAll(elements.filterNotNull())
}