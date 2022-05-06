package com.sohohouse.seven.network.core.models

import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "features")
class Feature : Resource(), Serializable