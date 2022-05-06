package com.sohohouse.seven.authentication.signin

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.Keep
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputLayout
import com.sohohouse.seven.App
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentSignInBinding
import com.sohohouse.seven.debug.DebugActivity

@Keep
class SignInFragment : BaseMVVMFragment<SignInViewModel>(), Loadable.View,
    ErrorDialogViewController {

    override val contentLayoutId: Int
        get() = R.layout.fragment_sign_in

    val binding by viewBinding(FragmentSignInBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLoginForm()
        observeLoadingState(this)
        observeErrorDialogEvents()
        viewModel.loginSuccessEvent.observe(this) { loginSucceeded() }
        viewModel.loginFailEvent.observe(this) { loginFailed() }
    }

    override fun onResume() {
        super.onResume()
        setSubmitBtnEnabledState()
        viewModel.setScreenName(name = AnalyticsManager.Screens.Login.name)
    }

    override val viewModelClass: Class<SignInViewModel>
        get() = SignInViewModel::class.java

    private val fieldsValid get() = isEmailValid() && isPasswordValid()

    @SuppressLint("SetTextI18n")
    private fun setUpLoginForm() = with(binding) {
        signInBtn.clicks { onLoginSubmit() }

        forgotPwBtn.clicks {
            viewModel.logForgotPasswordClick()
            WebViewBottomSheetFragment.withUrl(App.buildConfigManager.forgotPasswordUrl)
                .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
        }

        if (BuildConfig.DEBUG) {
            with(prodBtn) {
                setVisible()
                text = getAppModeText()
                clicks {
                    App.buildConfigManager.isStaging = !App.buildConfigManager.isStaging
                    text = getAppModeText()
                    App.appComponent.logoutUtil.logout()
                }
            }
        }

        emailInput.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                if (isEmailValid()) validateEmail()
                setSubmitBtnEnabledState()
            }
        })

        passwordInput.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                if (isPasswordValid()) validatePassword()
                setSubmitBtnEnabledState()
            }
        })

        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validatePassword()
                onLoginSubmit()
                true
            } else {
                false
            }
        }

        if (BuildConfig.DEBUG) {
            emailInput.setText("everyuk_account@example.com")
            passwordInput.setText("password")
        }

        hideErrorText(emailField)
        hideErrorText(passwordField)
        showApplicationVersion()
    }

    private fun getAppModeText() =
        "Change to ${if (App.buildConfigManager.isStaging) DebugActivity.PRODUCTION_STRING_KEY else DebugActivity.STAGING_STRING_KEY}"

    private fun hideErrorText(textInputLayout: TextInputLayout) {
        if (textInputLayout.childCount == 2) {
            textInputLayout.getChildAt(1).visibility = View.GONE
        }
    }

    private fun onLoginSubmit() {
        if (fieldsValid) {
            viewModel.login(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString(),
                App.buildConfigManager.clientSecret,
                App.buildConfigManager.applicationId
            )
        } else {
            updateErrorState(forced = true)
        }
    }

    private fun validateEmail() {
        setErrorText(binding.emailField, isEmailValid())
        updateErrorState()
    }

    private fun validatePassword() {
        setErrorText(binding.passwordField, isPasswordValid())
        updateErrorState()
    }

    private fun setErrorText(textInputLayout: TextInputLayout, valid: Boolean) {
        textInputLayout.error = if (valid) null else " "
        hideErrorText(textInputLayout)
    }

    private fun setSubmitBtnEnabledState() {
        binding.signInBtn.isEnabled = isPasswordValid() && isEmailValid()
    }

    private fun isPasswordValid(): Boolean {
        return binding.passwordInput.text.toString().isNotEmpty()
    }

    private fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(
            binding.emailInput.text?.trim().toString()
        )
            .matches()
    }

    private fun loginSucceeded() {
        with(requireActivity()) {
            val intent = appComponent.authenticationFlowManager.navigateFrom(
                context = requireContext(),
                launcherComponentEnabled = viewModel.isAppIconUpdated(this)
            )
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun loginFailed() {
        updateErrorState(forced = true)
    }

    private fun updateErrorState(forced: Boolean = false) = with(binding) {
        if (!textInputContainer.isChecked && !forced) return

        val result = fieldsValid
        setErrorText(emailField, result)
        setErrorText(passwordField, result)

        if (result) {
            textInputContainer.isChecked = false
            errorMessage.setGone()
        } else {
            textInputContainer.isChecked = true
            errorMessage.setVisible()
        }
    }

    fun showGenericErrorDialog(message: Array<out String>) {
        val messageRes = ErrorHelper.errorCodeMap.get(message.firstOrNull())
            ?: R.string.login_error_body
        CustomDialogFactory.createThemedAlertDialog(
            context = requireContext(),
            title = getString(R.string.login_error_header),
            message = getString(messageRes),
            positiveButtonText = getString(R.string.login_error_cta)
        ).show()
    }

    override val loadingView: LoadingView
        get() = binding.fragmentSignInLoadingView
    //endregion

    private fun showApplicationVersion() {
        context?.let {
            val version = if (App.buildConfigManager.isCurrentlyStaging) {
                "${BuildConfig.VERSION_NAME} - ${DebugActivity.STAGING_STRING_KEY}"
            } else {
                BuildConfig.VERSION_NAME
            }
            val appVersion = it.getString(R.string.more_app_version_label).replaceBraces(version)
            binding.applicationVersion.text = appVersion
        }
    }
}
