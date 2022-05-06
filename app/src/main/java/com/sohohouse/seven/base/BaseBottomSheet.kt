package com.sohohouse.seven.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.extensions.hideKeyboard

abstract class BaseBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(contentLayout, container, false)
    }

    protected val layoutBehavior: BottomSheetBehavior<*>?
        get() = ((view?.parent as? View)?.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as BottomSheetBehavior<*>?

    abstract val contentLayout: Int

    open val isDraggable = true
    open val fixedHeight: Int? get() = (activity?.window?.decorView?.height?.times(0.9f))?.toInt()

    private val dismissOnCollapsedCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        layoutBehavior?.addBottomSheetCallback(dismissOnCollapsedCallback)
        layoutBehavior?.isDraggable = isDraggable
        when (fixedHeight) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                layoutBehavior?.isFitToContents = true
            }
            else -> {
                setFixedHeight(fixedHeight)
            }
        }
        layoutBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setFixedHeight(fixedHeight: Int?) {
        fixedHeight ?: return
        val layoutBehavior = this.layoutBehavior
        if (layoutBehavior == null) {
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("Cannot resize bottom sheet - view is null"))
            return
        }

        requireView().layoutParams.apply {
            height = fixedHeight
        }
        requireView().requestLayout()
        layoutBehavior.peekHeight = 0
        layoutBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.hideKeyboard()
    }
}