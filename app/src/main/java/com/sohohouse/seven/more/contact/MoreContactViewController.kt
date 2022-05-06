package com.sohohouse.seven.more.contact

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.more.contact.recycler.BaseEnquiryItem

interface MoreContactViewController : LoadViewController, ErrorDialogViewController {
    fun showSubmitSuccessDialog()
    fun resetEnquiryForm()
    fun clearInquiryText()
    fun loadUrlInWebView(url: String)
    fun showEnquiryForm(
        dataList: MutableList<BaseEnquiryItem>,
        preselectedEnquiryTypes: MutableList<EnquiryType>,
        isBarredAccount: Boolean
    )
}