package com.sohohouse.seven.common.deeplink

import android.net.Uri
import androidx.lifecycle.LiveData

interface DeeplinkRepo {

    fun get(): LiveData<Uri>

    fun delete()

    fun put(uri: Uri?, redirect: Boolean = false)

}