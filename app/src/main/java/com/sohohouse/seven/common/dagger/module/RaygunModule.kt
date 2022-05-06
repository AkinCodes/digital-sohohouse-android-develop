package com.sohohouse.seven.common.dagger.module

import android.app.Application
import com.raygun.raygun4android.RaygunClient
import com.sohohouse.seven.common.utils.BuildVariantConfig
import dagger.Module

@Module
class RaygunModule(application: Application) {
    init {
        RaygunClient.init(application, BuildVariantConfig.RAYGUN_API_KEY)
        RaygunClient.enableCrashReporting()
    }
}