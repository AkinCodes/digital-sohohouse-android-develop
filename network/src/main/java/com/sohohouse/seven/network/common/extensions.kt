package com.sohohouse.seven.network.common

import moe.banana.jsonapi2.Document
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.Resource

fun <T: Resource> HasOne<T>.safeGet(document: Document?): T? {
    return if (document != null) {
        get(document)
    } else {
        null
    }
}

fun <T: Resource> HasMany<T>.safeGet(document: Document?): MutableList<T>? {
    return if (document != null) {
        get(document)
    } else {
        null
    }
}