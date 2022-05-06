package com.sohohouse.seven.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityVerifyAccountBinding
import javax.inject.Inject

class VerifyAccountActivity : BaseMVVMActivity<VerifyAccountViewModel>(), Loadable.View,
    ErrorDialogViewController {

    companion object {
        const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"

        fun getIntent(context: Context, accountId: String): Intent {
            return Intent(context, VerifyAccountActivity::class.java).apply {
                putExtra(EXTRA_ACCOUNT_ID, accountId)
            }
        }
    }

    @Inject
    lateinit var logoutUtil: LogoutUtil

    override val viewModelClass: Class<VerifyAccountViewModel>
        get() = VerifyAccountViewModel::class.java

    override fun getContentLayout() = R.layout.activity_verify_account

    val binding by viewBinding(ActivityVerifyAccountBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()
        observeEvents()
    }

    private fun observeEvents() {
        observeLoadingState(this)
        observeErrorDialogEvents()
        viewModel.verificationLinkSentEvent.observe(this, Observer {
            startActivity(Intent(this, VerificationEmailSentActivity::class.java))
        })
    }

    private fun setUpViews() {
        binding.resendVerificationLink.clicks {
            viewModel.onSendVerificationLinkClick(intent.getStringExtra(EXTRA_ACCOUNT_ID) ?: "")
            startActivity(Intent(this, VerificationEmailSentActivity::class.java))
            finish()
        }

        binding.returnToSignIn.clicks {
            logoutUtil.logout(shouldGoToSignIn = true)
            finish()
        }
    }

    override val loadingView: LoadingView
        get() = binding.activityVerifyAccountLoadingView

}
