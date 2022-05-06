package com.sohohouse.seven.connect.noticeboard

import android.content.Context
import androidx.fragment.app.DialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.dialog.CustomBottomSheetDialog

object NoticeboardUtil {

    const val DELETE_POST_DIALOG_TAG = "DELETE_POST_DIALOG_TAG"

    fun createDeletePostConfirmDialog(
        context: Context,
        onConfirmClick: () -> Unit,
        onCancelClick: () -> Unit = {}
    ): DialogFragment {
        return CustomBottomSheetDialog.Builder()
            .withTitle(context.getString(R.string.delete_post_title))
            .withMessage(context.getString(R.string.delete_post_message))
            .withPositiveBtnText(context.getString(R.string.cta_confirm))
            .withNegativeBtnText(context.getString(R.string.cta_cancel))
            .withPositiveBtnClickListener { onConfirmClick() }
            .withNegativeBtnClickListener { onCancelClick() }
            .build()
    }

}