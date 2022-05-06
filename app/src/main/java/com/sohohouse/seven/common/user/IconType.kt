package com.sohohouse.seven.common.user

import androidx.annotation.DrawableRes
import com.sohohouse.seven.R

enum class IconType(@DrawableRes var drawableRes: Int) {
    NONE(R.mipmap.ic_launcher),
    DEFAULT(R.mipmap.ic_launcher),
    DEFAULT_V1(R.mipmap.ic_launcher_v1),
    DEFAULT_V2(R.mipmap.ic_launcher_v2),
    CONNECT(R.mipmap.ic_launcher_connect),
    FRIENDS(R.mipmap.ic_launcher_friends),
    FRIENDS_V1(R.mipmap.ic_launcher_friends_v1),
    SOHO_HOUSE(R.mipmap.ic_launcher_soho_house),
    STAFF(R.mipmap.ic_launcher_staff),
}