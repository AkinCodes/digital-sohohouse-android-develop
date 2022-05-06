package com.sohohouse.seven.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys

class ErrorDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(
                arguments?.getString(BundleKeys.TITLE) ?: getString(R.string.general_error_header)
            )
            .setMessage(
                arguments?.getString(BundleKeys.MESSAGE) ?: getString(R.string.error_general)
            )
            .setPositiveButton(R.string.general_error_ok_cta) { dialog, which -> }
            .create()
    }

    companion object {
        const val TAG = "error_dialog_fragment"
    }
}