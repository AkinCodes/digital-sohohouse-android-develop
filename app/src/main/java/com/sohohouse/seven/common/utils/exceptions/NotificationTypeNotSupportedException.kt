package com.sohohouse.seven.common.utils.exceptions

class NotificationTypeNotSupportedException(trigger: String?) : Throwable
    ("This notification trigger ($trigger) is not handled by the app")

class NotificationNavigationScreenNotRecognisedException() : Throwable()