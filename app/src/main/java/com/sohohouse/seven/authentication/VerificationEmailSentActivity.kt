package com.sohohouse.seven.authentication

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.databinding.ActivityVerificationEmailSentBinding
import javax.inject.Inject

class VerificationEmailSentActivity : InjectableActivity() {

    @Inject
    lateinit var logoutUtil: LogoutUtil

    override fun getContentLayout() = R.layout.activity_verification_email_sent

    val binding by viewBinding(ActivityVerificationEmailSentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.returnToSignIn.clicks {
            logoutUtil.logout(shouldGoToSignIn = true)
            finish()
        }
    }
}
