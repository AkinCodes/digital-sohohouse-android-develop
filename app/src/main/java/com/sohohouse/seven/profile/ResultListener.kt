package com.sohohouse.seven.profile

import android.os.Bundle
import androidx.core.os.bundleOf

interface ResultListener {

    fun onResult(requestKey: String = "", bundle: Bundle = bundleOf())

}