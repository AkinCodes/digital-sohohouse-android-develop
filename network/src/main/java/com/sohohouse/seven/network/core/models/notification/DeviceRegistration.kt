package com.sohohouse.seven.network.core.models.notification

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "notification_device_registrations")
data class DeviceRegistration(
    @field:Json(name = "device_registration_token") var deviceRegistrationToken: String = "",
) : Resource(), Serializable