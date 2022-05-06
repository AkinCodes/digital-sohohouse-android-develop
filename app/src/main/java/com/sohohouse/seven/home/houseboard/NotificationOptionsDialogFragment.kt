package com.sohohouse.seven.home.houseboard

import android.app.Activity
import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.BottomSheetNotificationOptionsBinding

class NotificationOptionsDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_notification_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = BottomSheetNotificationOptionsBinding.bind(view)
        with(view) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius =
                        resources.getDimensionPixelOffset(R.dimen.home_content_corner_radius)
                    outline.setRoundRect(0, 0, view.width, (view.height + radius), radius.toFloat())
                }
            }
            clipToOutline = true
        }

        binding.settings.setOnClickListener {
            targetFragment?.onActivityResult(REQUEST_NOTIFICATION_OPTIONS,
                Activity.RESULT_OK,
                Intent().apply { putExtra(BUNDLE_KEY, RESULT_GO_TO_SETTIGNS) })
            dismiss()
        }

        binding.clearAll.setOnClickListener {
            targetFragment?.onActivityResult(REQUEST_NOTIFICATION_OPTIONS,
                Activity.RESULT_OK,
                Intent().apply { putExtra(BUNDLE_KEY, RESULT_CLEAR_ALL) })
            dismiss()
        }
    }

    companion object {
        const val TAG = "notification_options_dialog"
        const val BUNDLE_KEY = "notification_options_result"

        const val REQUEST_NOTIFICATION_OPTIONS = 101010
        const val RESULT_GO_TO_SETTIGNS = 0
        const val RESULT_CLEAR_ALL = 1
    }
}