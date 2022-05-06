package com.sohohouse.seven.guests

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.form.FormAdapter
import com.sohohouse.seven.common.form.FormItem
import com.sohohouse.seven.common.form.FormItemDecoration
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityGuestlistDetailsBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter

class GuestListDetailsActivity : BaseMVVMActivity<GuestListDetailsViewModel>(), Loadable.View,
    ErrorDialogViewController {

    companion object {
        private const val EXTRA_GUESTLIST_ID = "EXTRA_GUESTLIST_ID"
        private const val EXTRA_MODE = "EXTRA_MODE"

        fun getIntent(context: Context, id: String, mode: GuestListDetailsMode): Intent {
            return Intent(context, GuestListDetailsActivity::class.java).apply {
                putExtra(EXTRA_GUESTLIST_ID, id)
                putExtra(EXTRA_MODE, mode.name)
            }
        }
    }

    private val mode: GuestListDetailsMode by lazy {
        intent.getStringExtra(EXTRA_MODE)?.let {
            it.asEnumOrDefault(GuestListDetailsMode.MODE_NEW_GUEST_LIST)
        } ?: GuestListDetailsMode.MODE_NEW_GUEST_LIST
    }

    override fun getContentLayout() = R.layout.activity_guestlist_details

    private val binding by viewBinding(ActivityGuestlistDetailsBinding::bind)

    override val viewModelClass: Class<GuestListDetailsViewModel>
        get() = GuestListDetailsViewModel::class.java

    private val adapter = GuestListDetailsAdapter().apply {
        registerRenderers(
            GuestListFormItemRenderer(),
            GuestItemRenderer({ onEditNameClick(it) }, { onShareLinkClick(it) }),
            FormHeaderItemRenderer(),
            GuestHeaderItemRenderer(),
            GuestSubheaderItemRenderer()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()

        with(binding) {
            viewModel.init(intent.getStringExtra(EXTRA_GUESTLIST_ID) ?: "", mode)
            guestlistDetailsRv.adapter = adapter
            guestlistDetailsRv.addItemDecoration(
                FormItemDecoration(
                    this@GuestListDetailsActivity,
                    adapter
                )
            )

            observeLoadingState(this@GuestListDetailsActivity)
            observeErrorDialogEvents()

            guestlistAddGuest.setOnClickListener { onAddGuestClick() }
            guestlistAddAnotherGuest.setOnClickListener { onAddGuestClick() }
            guestlistDelete.clicks { onDeleteClick() }
            guestlistDone.clicks { finish() }
        }

        viewModel.addGuestAvailbale.observe(this) { binding.guestlistAddGuest.setVisible(it) }
        viewModel.addAnotherGuestAvailbale.observe(this) {
            binding.guestlistAddAnotherGuest.setVisible(
                it
            )
        }
        viewModel.doneAvailbale.observe(this) { binding.guestlistDone.setVisible(it) }
        viewModel.deleteAvailable.observe(this) { binding.guestlistDelete.setVisible(it) }

        viewModel.items.observe(this) {
            if (it != null) adapter.setItems(it.toMutableList())
        }
        viewModel.navigationExitEvent.observe(this) {
            finish()
        }
    }

    private fun onEditNameClick(item: GuestListDetailsAdapterItem.GuestItem) {
        CustomDialogFactory.createThemedInputDialog(this,
            getString(R.string.cta_edit_guest_name),
            hint = getString(R.string.label_guest_name),
            inputValue = item.guestName,
            negativeButtonText = getString(R.string.cta_cancel),
            positiveButtonText = getString(R.string.cta_done),
            onInputConfirmed = { newName ->
                viewModel.onExistingGuestNameChanged(
                    item.inviteId,
                    newName
                )
            })
            .show()
    }

    private fun onShareLinkClick(item: GuestListDetailsAdapterItem.GuestItem) {
        viewModel.logClickShareLink()

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, viewModel.buildShareMessage(item.inviteId))
            type = "text/plain"
        }
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.title_house_guest_invitation)
            )
        )
    }

    private fun onDeleteClick() {
        CustomDialogFactory.createThemedAlertDialog(this,
            title = getString(R.string.title_delete_invitation),
            message = getString(R.string.message_delete_invitation_confirm),
            positiveButtonText = getString(R.string.cta_confirm),
            negativeButtonText = getString(R.string.cta_cancel),
            positiveClickListener = DialogInterface.OnClickListener { _, _ ->
                viewModel.deleteGuestList()
            })
            .show()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        when (mode) {
            GuestListDetailsMode.MODE_NEW_GUEST_LIST -> {
                binding.toolbarTitle.text = getString(R.string.title_share_invitation)
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(false)
                }
            }
            GuestListDetailsMode.MODE_EXISTING_GUEST_LIST -> {
                binding.toolbarTitle.text = getString(R.string.title_your_invitation)
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_left_arrow)
                }
            }
        }

    }

    private fun onAddGuestClick() {
        CustomDialogFactory.createThemedInputDialog(this,
            getString(R.string.cta_add_guest),
            hint = getString(R.string.label_guest_name),
            negativeButtonText = getString(R.string.cta_cancel),
            positiveButtonText = getString(R.string.cta_done),
            onInputConfirmed = { viewModel.onNewGuestNameConfirmed(it) })
            .show()

    }

    override val loadingView: LoadingView
        get() = binding.activityGuestlistDetailsLoadingView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (mode == GuestListDetailsMode.MODE_NEW_GUEST_LIST) {
            menuInflater.inflate(R.menu.menu_close_btn, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_close) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

class GuestListDetailsAdapter : RendererDiffAdapter(), FormAdapter {

    override fun getFormRowType(adapterPosition: Int): FormRowType {
        return (mItems.getOrNull(adapterPosition) as? FormItem)?.rowType ?: FormRowType.NONE
    }

}

