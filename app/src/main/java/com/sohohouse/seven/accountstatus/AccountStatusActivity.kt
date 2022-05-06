package com.sohohouse.seven.accountstatus

import android.os.Bundle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.user.MembershipStatus
import com.sohohouse.seven.common.views.FullScreenPromptFragment
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.more.contact.MoreContactActivity

class AccountStatusActivity : BaseMVVMActivity<AccountStatusViewModel>(),
    FullScreenPromptFragment.ButtonListener {

    companion object {
        const val CHASING_COMPLETE_REQUEST_CODE = 1234
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_account_status
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateToFullScreenPrompt(viewModel.membershipStatus)
    }

    private fun navigateToFullScreenPrompt(membershipStatus: MembershipStatus) {
        viewModel.navigateFrom(this, membershipStatus)
    }

    private fun loadUrl(url: String) {
        WebViewBottomSheetFragment.withUrl(url, useBearerToken = true)
            .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    //region ButtonListener
    override fun onStatusPrimaryButtonClicked() {
        viewModel.navigateFromPrimaryButton(this)
    }

    override fun onStatusSecondaryButtonClicked() {
        viewModel.navigateFromSecondaryButton(this)
    }
    //endregion

    fun contactSupport() {
        startActivity(MoreContactActivity.getIntentForMembershipChangeInquiry(this))
    }

    fun updatePayment() {
        loadUrl(viewModel.paymentUpdateUrl)
    }

    fun logOut() {
        viewModel.logout(shouldGoToSignIn = true)
    }

    override val viewModelClass: Class<AccountStatusViewModel>
        get() = AccountStatusViewModel::class.java
}
