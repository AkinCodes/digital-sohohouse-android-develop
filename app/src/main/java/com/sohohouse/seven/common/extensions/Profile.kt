package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.network.core.models.Profile

val Profile.fullName get() = firstName + if (lastName.isNotBlank()) " $lastName" else ""
