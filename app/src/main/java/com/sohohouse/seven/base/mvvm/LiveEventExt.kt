package com.sohohouse.seven.base.mvvm

fun LiveEvent<Any>.emitEvent() {
    value = Any()
}

fun LiveEvent<Any>.postEvent() {
    postValue(Any())
}