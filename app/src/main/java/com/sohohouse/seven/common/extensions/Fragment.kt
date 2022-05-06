package com.sohohouse.seven.common.extensions

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R

fun Fragment.show(fm: FragmentManager, containerId: Int, tag: String, animate: Boolean = true) {
    fm.beginTransaction()
        .apply {
            if (animate) {
                setCustomAnimations(
                    R.anim.bottom_up_quick,
                    R.anim.bottom_down_quick,
                    R.anim.bottom_up_quick,
                    R.anim.bottom_down_quick
                )
            }
        }
        .addToBackStack(null)
        .replace(containerId, this, tag)
        .commit()
}

fun Fragment.dismiss() {
    activity?.supportFragmentManager?.popBackStack()
}

fun Fragment.startActivityAndFinish(intent: Intent) {
    startActivity(intent)
    requireActivity().finish()
}

fun Fragment.startActivitySafely(intent: Intent): Boolean {
    return if (intent.resolveActivity(requireContext().packageManager) != null) {
        startActivity(intent)
        true
    } else {
        FirebaseCrashlytics.getInstance().log("Unable to open intent: $intent")
        false
    }
}

fun Fragment.setFragmentResult(requestKey: String?) {
    requestKey?.let {
        setFragmentResult(requestKey, bundleOf())
    }
}

fun <T : Fragment> T.withResultListener(
    requestKey: String,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)
): T {
    return this.apply {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        setFragmentResultListener(requestKey, listener)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        clearFragmentResultListener(requestKey)
                    }
                }
            }
        })
    }
}

fun Fragment.setFragmentResultsListener(
    requestKeys: Array<String>,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)
) {
    requestKeys.forEach { setFragmentResultListener(it, listener) }
}

fun Fragment.setChildFragmentResultListener(
    requestKey: String,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)
) {
    childFragmentManager.setFragmentResultListener(
        requestKey,
        viewLifecycleOwner,
        listener
    )
}