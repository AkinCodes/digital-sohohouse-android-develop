package com.sohohouse.seven.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.sohohouse.seven.base.mvpimplementation.FragmentLifeCycleListener
import com.sohohouse.seven.base.mvpimplementation.FragmentLifeCycleManger
import com.sohohouse.seven.base.mvpimplementation.RetainedFragment

abstract class BaseFragment : Fragment() {
    private val mLifeCycleManager = FragmentLifeCycleManger()

    /**
     * The layout id for the fragment.
     *
     * @return a valid non-zero layout resource id
     */
    protected abstract val contentLayoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(contentLayoutId, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLifeCycleManager.clearListeners()
        mLifeCycleManager.addAll(createLifeCycleListenerList(ArrayList()))
        mLifeCycleManager.onViewCreated(this, view, savedInstanceState)
    }

    @CallSuper
    protected open fun createLifeCycleListenerList(
        lifeCycleListenerList: MutableList<FragmentLifeCycleListener?>,
    ): List<FragmentLifeCycleListener?> {
        return lifeCycleListenerList
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        mLifeCycleManager.onResume()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        var outState = outState
        outState = Bundle()
        mLifeCycleManager.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        mLifeCycleManager.onPause()
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mLifeCycleManager.onActivityResult(requestCode, resultCode, data)
    }

    val baseActivity: BaseActivity?
        get() = activity as BaseActivity?

    override fun onDestroy() {
        super.onDestroy()
        mLifeCycleManager.onDestroy()
    }

    protected val retainedFragment: RetainedFragment
        protected get() = baseActivity!!.retainedFragment
}