package com.sohohouse.seven.branding

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.databinding.FragmentAppIconChangeBinding

class AppIconChangeDialog : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentAppIconChangeBinding.inflate(LayoutInflater.from(requireContext()))
        val iconType = getMembershipIcon(binding.icon)

        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.onboarding_welcome_dialog_ok) { _, _ ->
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra(BundleKeys.ICON_TYPE, iconType?.name)
                    })
            }
            .setOnKeyListener { _, keyCode, event ->
                keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
            }
            .setCancelable(false)
        return builder.create().apply {
            setCanceledOnTouchOutside(false)
            setOnShowListener { getButton(BUTTON_POSITIVE)?.requestFocus() }
        }
    }

    private fun getMembershipIcon(imageView: ImageView): IconType? {
        val iconType = arguments?.getString(BundleKeys.ICON_TYPE)?.asEnumOrDefault(IconType.DEFAULT)
            ?: IconType.DEFAULT
        imageView.setImageResource(iconType.drawableRes)
        return iconType
    }

    companion object {
        const val TAG = "app_icon_change_dialog"
    }
}