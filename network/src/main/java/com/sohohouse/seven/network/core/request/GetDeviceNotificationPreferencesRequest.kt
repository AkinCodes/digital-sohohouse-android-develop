package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.DeviceNotificationPreferences
import retrofit2.Call

class GetDeviceNotificationPreferencesRequest
    : CoreAPIRequest<List<DeviceNotificationPreferences>> {
    override fun createCall(api: CoreApi): Call<List<DeviceNotificationPreferences>> {
        return api.getDeviceNotificationPreferences()
    }
}