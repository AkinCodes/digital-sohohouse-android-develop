package com.sohohouse.seven.common.utils

import android.os.Build

/**
 * Created by jamesbritton on 24/05/2016 at 17:22.
 *
 * Android Version Checking, add extra versions as and when applicable
 */

object VersionUtils {

    val isAtLeastPie: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    val isAtLeastNougat: Boolean
        get() = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N

    val isAtLeastNougatMR1: Boolean
        get() = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1

    val isAtLeastOreo: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

}
