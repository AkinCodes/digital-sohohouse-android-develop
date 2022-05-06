package com.sohohouse.seven.base

import android.content.Context
import android.view.WindowManager
import androidx.annotation.CallSuper
import com.sohohouse.seven.base.mvpimplementation.FragmentLifeCycleListener
import com.sohohouse.seven.base.mvpimplementation.MvpLifeCycleListener
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationAdapterItem
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationDialog
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationListener

abstract class BaseViewControllerFragment<P : BasePresenter<*>?>
    : BaseFragment(), ViewController, InAppNotificationListener {

    private var mvpLifeCycleListener: MvpLifeCycleListener<*, *>? = null
    abstract override fun createPresenter(): P

    override fun getPresenter(): P {
        return mvpLifeCycleListener!!.presenter as P
    }

    override fun createLifeCycleListenerList(
        lifeCycleListenerList: MutableList<FragmentLifeCycleListener?>,
    ): List<FragmentLifeCycleListener?> {
        lifeCycleListenerList.add(
            MvpLifeCycleListener(
                KEY_MAIN_MVP_LISTENER, this, retainedFragment
            ).also {
                mvpLifeCycleListener = it
            }
        )
        return super.createLifeCycleListenerList(lifeCycleListenerList)
    }

    override fun onCreated() {
        // do nothing by default
    }

    override fun setScreenName(screenName: String) {
        presenter?.setScreenName(requireActivity().localClassName, screenName)
    }

    @CallSuper
    override fun onDialogPrimaryButtonClicked() {
    }

    @CallSuper
    override fun onDialogSecondaryButtonClicked() {
    }

    /**
     * Have to keep two implementations of this because stupid Java won't let us call this from a
     * kotlin interface.
     */
    override fun onNotificationDataReady(context: Context, item: InAppNotificationAdapterItem) {
        val dialog = InAppNotificationDialog(context, item, this, null, null)
        val lp = WindowManager.LayoutParams()
        val attribute = dialog.window?.attributes ?: throw NullPointerException()
        lp.copyFrom(attribute)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.window?.attributes = lp
    }

    companion object {
        private const val KEY_MAIN_MVP_LISTENER = "KEY_MAIN_MVP_LISTENER"
    }
}