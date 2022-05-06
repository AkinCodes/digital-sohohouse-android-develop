package com.sohohouse.seven.more.change.password

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController

interface ChangePasswordViewController : ViewController, LoadViewController,
    ErrorDialogViewController {
    fun onPasswordChanged()
}