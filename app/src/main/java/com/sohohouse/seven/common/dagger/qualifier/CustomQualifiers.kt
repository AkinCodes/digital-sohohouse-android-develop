package com.sohohouse.seven.common.dagger.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AllNotificationMapper

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FcmNotificationMapper

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SfmcNotificationMapper

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SendBirdNotificationMapper