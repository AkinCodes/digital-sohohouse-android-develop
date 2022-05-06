package com.sohohouse.seven.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.sohohouse.seven.App.Companion.appComponent
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleListener
import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleManager
import com.sohohouse.seven.base.mvpimplementation.RetainedFragment
import com.sohohouse.seven.base.mvvm.ConnectionLiveData
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.utils.AppVariantUtils.lockActivityOrientation
import com.sohohouse.seven.common.views.snackbar.Snackbar
import com.sohohouse.seven.common.views.snackbar.SnackbarState
import com.sohohouse.seven.shake.ShakeLifeCycleListener

/**
 * All classes should inherit from this class.  It contains required set-up for the application.
 *
 *
 * Created by sumesh on 2/4/16.
 */
abstract class BaseActivity : AppCompatActivity() {
    @Deprecated("")
    private var mRetainedFragment: RetainedFragment? = null
    private val mLifeCycleManager = ActivityLifeCycleManager()

    // we'll keep this until refactoring to mvvm is done
    private val connectionLiveData = ConnectionLiveData(this)
    private val connectionObserver = Observer { connected: Boolean ->
        val titleRes =
            if (connected) R.string.snack_bar_title_online else R.string.snack_bar_title_offline
        val state = if (connected) SnackbarState.POSITIVE else SnackbarState.NEGATIVE
        val snackbar = Snackbar.Builder(this).setTitle(titleRes).setState(state).build()
        snackbar?.show()
        if (connected) {
            onConnected()
        } else {
            onDisconnected()
        }
    }

    protected open fun onDisconnected() {
        //open for impl
    }

    protected open fun onConnected() {
        //open for impl
    }

    protected open fun setBrandingTheme() {
        setTheme(R.style.BaseTheme)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        initRetainedFragment()
        setBrandingTheme()
        setContentView(getContentLayout())
        lockActivityOrientation(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mLifeCycleManager.clearListeners()
        mLifeCycleManager.addAll(createLifeCycleListenerList(ArrayList()))
        mLifeCycleManager.onPostCreated(this, savedInstanceState)
        connectionLiveData.observe(this, connectionObserver)
        if (intent.hasExtra(BundleKeys.INTENT_BOUNCE_DESTINATION_ACTIVITY)) {
            val intent = Intent(
                this,
                intent.getSerializableExtra(BundleKeys.INTENT_BOUNCE_DESTINATION_ACTIVITY)
                        as? Class<Activity?>
            )
            intent.putExtras(getIntent().extras!!)
            intent.removeExtra(BundleKeys.INTENT_BOUNCE_DESTINATION_ACTIVITY)
            startActivity(intent)
        }
    }

    @CallSuper
    open fun createLifeCycleListenerList(
        lifeCycleListenerList: MutableList<ActivityLifeCycleListener?>
    ): List<ActivityLifeCycleListener?> {
        lifeCycleListenerList.add(ShakeLifeCycleListener(this))
        return lifeCycleListenerList
    }

    protected abstract fun getContentLayout(): Int

    val retainedFragment: RetainedFragment
        get() {
            val fm = supportFragmentManager
            val retainedFragment =
                fm.findFragmentByTag(FRAG_TAG_RETAINED_FRAGMENT) as RetainedFragment?
            return retainedFragment ?: mRetainedFragment!!
        }

    private fun initRetainedFragment() {
        if (!isFinishing && !isDestroyed) {
            // find the retained fragment on activity restarts
            val fm = supportFragmentManager
            mRetainedFragment =
                fm.findFragmentByTag(FRAG_TAG_RETAINED_FRAGMENT) as RetainedFragment?

            // create the fragment and data the first time
            if (mRetainedFragment == null) {
                // add the fragment
                mRetainedFragment = RetainedFragment()
                fm.beginTransaction().add(mRetainedFragment!!, FRAG_TAG_RETAINED_FRAGMENT).commit()
            }
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        mLifeCycleManager.onResume()
    }

    @CallSuper
    public override fun onSaveInstanceState(outState: Bundle) {
        mLifeCycleManager.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        mLifeCycleManager.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        mLifeCycleManager.onDestroy()
        connectionLiveData.observe(this, connectionObserver)
    }

    @CallSuper
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mLifeCycleManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val FRAG_TAG_RETAINED_FRAGMENT = "FRAG_TAG_RETAINED_FRAGMENT"
    }
}