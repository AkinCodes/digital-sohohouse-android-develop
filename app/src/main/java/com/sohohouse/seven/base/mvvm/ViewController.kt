package com.sohohouse.seven.base.mvvm

import androidx.lifecycle.LifecycleOwner
import android.content.Context

//Defines a ViewController for MVVM
interface ViewController {
    @Suppress("PropertyName")   //name conflict in Fragment
    val _context: Context?

    val lifecycleOwner: LifecycleOwner

    val viewModel: IViewModel
}