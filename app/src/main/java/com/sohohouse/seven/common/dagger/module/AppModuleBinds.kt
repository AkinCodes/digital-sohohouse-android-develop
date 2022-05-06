package com.sohohouse.seven.common.dagger.module

import com.sohohouse.seven.common.dagger.qualifier.AllNotificationMapper
import com.sohohouse.seven.common.dagger.qualifier.FcmNotificationMapper
import com.sohohouse.seven.common.dagger.qualifier.SendBirdNotificationMapper
import com.sohohouse.seven.common.dagger.qualifier.SfmcNotificationMapper
import com.sohohouse.seven.home.repo.HousePayBannerDelegate
import com.sohohouse.seven.home.repo.HousePayBannerDelegateImpl
import com.sohohouse.seven.common.prefs.FileCreator
import com.sohohouse.seven.fcm.mappers.AllNotificationPayloadMapper
import com.sohohouse.seven.fcm.mappers.NotificationPayloadMapper
import com.sohohouse.seven.fcm.mappers.SendBirdNotificationPayloadMapper
import dagger.Binds
import dagger.Module
import dagger.Reusable
import javax.inject.Singleton

@Module
abstract class AppModuleBinds {

    @Binds
    @Reusable
    @AllNotificationMapper
    abstract fun bindNotificationPayloadMapper(
        impl: AllNotificationPayloadMapper
    ): NotificationPayloadMapper

    @Binds
    @Reusable
    @FcmNotificationMapper
    abstract fun bindFcmNotificationPayloadMapper(
        impl: com.sohohouse.seven.fcm.mappers.FcmNotificationMapper
    ): NotificationPayloadMapper

    @Binds
    @Reusable
    @SfmcNotificationMapper
    abstract fun bindSfmcNotificationPayloadMapper(
        impl: com.sohohouse.seven.fcm.mappers.SfmcNotificationMapper
    ): NotificationPayloadMapper

    @Binds
    @Reusable
    @SendBirdNotificationMapper
    abstract fun bindSendBirdNotificationMapper(
        impl: SendBirdNotificationPayloadMapper
    ): NotificationPayloadMapper

    @Binds
    @Singleton
    abstract fun bindFileProvider(impl: FileCreator.Impl): FileCreator

    @Binds
    abstract fun bindHousePayBannerDelegate(
        impl: HousePayBannerDelegateImpl
    ): HousePayBannerDelegate

}