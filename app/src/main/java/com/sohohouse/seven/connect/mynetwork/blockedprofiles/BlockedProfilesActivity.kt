package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.LoadingDialogFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.snackbar.Snackbar
import com.sohohouse.seven.databinding.ActivityBlockedMembersBinding

class BlockedProfilesActivity : BaseMVVMActivity<BlockedProfilesViewModel>(), Injectable,
    Loadable.View {

    override val viewModelClass: Class<BlockedProfilesViewModel>
        get() = BlockedProfilesViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.activity_blocked_members
    }

    val binding by viewBinding(ActivityBlockedMembersBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val adapter = PagedRendererAdapter<DiffItem>().apply {
            registerRenderer(BlockedProfileRenderer(::onUnblockClick))
        }

        with(binding) {
            backArrow.setOnClickListener {
                onBackPressed()
            }
            recyclerView.adapter = adapter
        }

        viewModel.blockedContacts.observe(this, {
            adapter.submitList(it)
        })

        observeLoadingState(this) {
            val isIdl = LoadingState.Idle == it
            with(binding) {
                progressBar.setVisible(!isIdl)
                emptyState.setVisible(isIdl && adapter.itemCount == 0)
            }
            if (isIdl) showLoadingDialog(false)
        }

        viewModel.errorState.collectLatest(this) {
            Snackbar(binding.emptyState, false).apply {
                enableSwipeToDismiss(true)
                setTitle(resources.getString(it))
                show()
            }
        }
    }

    private fun onUnblockClick(item: BlockedProfile) {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.connect_unblock_cta) + " ${item.fullName}?")
            .setPositiveButton(
                R.string.connect_unblock_cta
            ) { _, _ ->
                viewModel.unblockContact(item)
                showLoadingDialog(true)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLoadingDialog(show: Boolean) {
        if (show) {
            LoadingDialogFragment().showSafe(supportFragmentManager, LoadingDialogFragment.TAG)
        } else {
            (supportFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG) as? DialogFragment)?.dismiss()
        }
    }

}