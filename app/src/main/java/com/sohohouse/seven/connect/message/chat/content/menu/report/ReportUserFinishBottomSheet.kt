package com.sohohouse.seven.connect.message.chat.content.menu.report

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.databinding.UserReportFinishBinding

class ReportUserFinishBottomSheet : BaseBottomSheet() {

    override val contentLayout: Int = R.layout.user_report_finish

    override val fixedHeight = WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserReportFinishBinding.bind(view).apply {
            messagingDone.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ReportUserFinishBottomSheet"
    }
}