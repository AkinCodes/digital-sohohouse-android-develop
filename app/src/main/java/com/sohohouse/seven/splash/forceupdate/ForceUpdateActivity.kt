package com.sohohouse.seven.splash.forceupdate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.databinding.ActivityForceUpdateBinding

class ForceUpdateActivity : BaseViewControllerActivity<ForceUpdatePresenter>(),
    ForceUpdateViewController {

    override fun createPresenter(): ForceUpdatePresenter {
        return ForceUpdatePresenter()
    }

    override fun getContentLayout() = R.layout.activity_force_update

    val binding by viewBinding(ActivityForceUpdateBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        binding.forceUpdateView.setOnPrimaryButtonClickListener {
            presenter.onPrimaryButtonClicked()
        }
    }

    override fun updateApp(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun logout() {
        App.appComponent.logoutUtil.logout(false)
    }
}
