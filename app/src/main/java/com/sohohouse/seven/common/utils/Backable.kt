package com.sohohouse.seven.common.utils

interface Backable {
    /* Use this interface to delegate onBack press to fragments. True in return value means that fragment handle onBack press. */
    fun onBackPressed(): Boolean
}