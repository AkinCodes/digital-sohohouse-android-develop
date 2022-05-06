package com.sohohouse.seven.more.change.password

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerFragment
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.hideKeyboard
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.FragmentChangePasswordBinding


class ChangePasswordFragment :
    BaseViewControllerFragment<ChangePasswordPresenter>(), ChangePasswordViewController {

    private val newPwdInput get() = binding.newPwdInput.text.toString()
    private val oldPwdInput get() = binding.oldPwdInput.text.toString()
    private val confirmPwdInput get() = binding.confirmPwdInput.text.toString()

    override fun createPresenter() = App.appComponent.changePasswordPresenter

    override val contentLayoutId get() = R.layout.fragment_change_password

    val binding by viewBinding(FragmentChangePasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpForms()
    }

    override fun onResume() {
        super.onResume()
        presenter.logChangePasswordView()
    }

    private fun setUpForms() {
        binding.confirmPwdInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onDone()
                true
            } else {
                false
            }
        }
        binding.saveChangesBtn.clicks {
            onDone()
        }
    }

    private fun onDone() {
        if (validateInput()) {
            makeChangePasswordRequest()
        }
    }

    private fun validateInput(): Boolean {
        var valid = true
        if (oldPwdInput.isNotEmpty()) {
            binding.oldPwdField.error = ""
        } else {
            binding.oldPwdField.error = getString(R.string.more_password_error)
            valid = false
        }
        if (newPwdInput.isNotEmpty()) {
            binding.newPwdField.error = ""
        } else {
            binding.newPwdField.error = getString(R.string.more_password_error)
            valid = false
        }
        if (confirmPwdInput.isNotEmpty()) {
            binding.confirmPwdField.error = ""
        } else {
            binding.confirmPwdField.error = getString(R.string.more_password_error)
            valid = false
        }

        if (valid) {
            if (newPwdInput == confirmPwdInput) {
                binding.confirmPwdField.error = ""
            } else {
                binding.confirmPwdField.error = getString(R.string.more_confirm_password_error)
                valid = false
            }
        }

        return valid
    }

    private fun makeChangePasswordRequest() {
        presenter.onChangePasswordRequested(
            oldPwdInput,
            newPwdInput,
            confirmPwdInput,
            App.buildConfigManager.clientSecret,
            App.buildConfigManager.applicationId
        )
    }

    override fun showGenericErrorDialog(errorCodes: Array<out String>) {
        val messageRes = ErrorHelper.errorCodeMap.get(errorCodes.firstOrNull())
            ?: R.string.change_password_fail_supporting
        CustomDialogFactory.createThemedAlertDialog(context = requireContext(),
            title = getString(R.string.change_password_fail_header),
            message = getString(messageRes),
            positiveButtonText = getString(R.string.change_password_fail_ok_cta),
            positiveClickListener = { _, _ ->
                activity?.hideKeyboard()
            })
            .show()
    }

    override fun onPasswordChanged() {
        clearForm()
        showSuccessMsg()
    }

    private fun showSuccessMsg() {
        CustomDialogFactory.createThemedAlertDialog(
            requireContext(),
            title = getString(R.string.change_password_success_header),
            message = getString(R.string.change_password_success_supporting),
            positiveButtonText = getString(R.string.dismiss_button_label)
        ).show()
    }

    private fun clearForm() = with(binding) {
        oldPwdInput.setText("")
        newPwdInput.setText("")
        confirmPwdInput.setText("")
    }

    override val loadingView: LoadingView
        get() = binding.changePwdLoadingView

}