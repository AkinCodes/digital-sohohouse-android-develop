package com.sohohouse.seven.common.dagger.module

import com.sohohouse.seven.fcm.SohoFCMService
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun provideSohoFCMService(): SohoFCMService

}