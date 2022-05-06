package com.sohohouse.seven.guests

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.hideKeyboard
import com.sohohouse.seven.common.form.FormAdapter
import com.sohohouse.seven.common.form.FormItem
import com.sohohouse.seven.common.form.FormItemDecoration
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityNewGuestListBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import java.util.*

class NewGuestListActivity : BaseMVVMActivity<NewGuestListViewModel>(),
    DatePickerDialog.OnDateSetListener, LocationPickerFragment.Listener, Loadable.View {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NewGuestListActivity::class.java)
        }
    }

    private val adapter = NewGuestListAdapter().apply {
        registerRenderers(
            NewGuestItemRenderer { onShareLinkClick(it) }
        )
    }

    override val viewModelClass: Class<NewGuestListViewModel>
        get() = NewGuestListViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.activity_new_guest_list
    }

    private val binding by viewBinding(ActivityNewGuestListBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setUpForm()
        observeFormData()
        observeGuestListCreatedEvent()
        observeLoadingState(this)
        setUpGuestAdapter()
        observeGuests()
        observerShareInvitationEvent()
        observeCreateEvent()
    }

    private fun observeCreateEvent() {
        viewModel.guestItemCreatedEvent.observe(this) {
            disableClicksOnForm()
        }
    }

    private fun setUpGuestAdapter() {

        with(binding) {
            guestlistRv.adapter = adapter
            guestlistRv.addItemDecoration(
                FormItemDecoration(
                    this@NewGuestListActivity,
                    adapter
                )
            )
        }
    }

    private fun observeGuests() {
        viewModel.guestList.observe(this) {
            adapter.setItems(it)
        }
    }

    private fun observerShareInvitationEvent() {
        viewModel.shareInvitationEvent.observe(this) {
            if (it != -1) {
                shareInvitation(it.toString())
            }
        }
    }

    private fun shareInvitation(inviteID: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, viewModel.getShareMessage(inviteID))
            type = "text/plain"
        }
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.title_house_guest_invitation)
            )
        )
    }

    private fun disableClicksOnForm() {
        binding.createInviteForm.disableForm()
    }

    private fun setUpForm() = with(binding) {
        createInviteForm.onDateClick { openDateDialog() }
        createInviteForm.onLocationClick { openLocationPickerFragment() }
        createInviteFormConfirm.clicks {
            viewModel.onConfirmClick()
        }
    }

    private fun setUpToolbar() = with(binding) {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_left_arrow)
        }
        toolbarTitle.text = getString(R.string.title_new_invitation)
    }

    private fun observeGuestListCreatedEvent() {
        viewModel.navigateToGustListDetailsEvent.observe(this) {
            startActivity(
                GuestListDetailsActivity.getIntent(
                    this,
                    it,
                    GuestListDetailsMode.MODE_NEW_GUEST_LIST
                )
            )
            finish()
        }
    }

    private fun observeFormData() {
        viewModel.dateItem.observe(this) {
            binding.createInviteForm.bindDateData(it)
        }
        viewModel.houseItem.observe(this) {
            binding.createInviteForm.bindHouseData(it)
        }
        viewModel.submitEnabled.observe(this) { enabled ->
            binding.createInviteFormConfirm.isEnabled = enabled
        }
        viewModel.houseClosedErrorEvent.observe(this) {
            showHouseClosedErrorDialog()
        }
    }

    private fun showHouseClosedErrorDialog() {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            title = getString(R.string.error_title_selected_house_is_closed),
            message = getString(R.string.error_message_cannot_send_invitation),
            positiveButtonText = getString(R.string.dismiss_button_label)
        )
            .show()
    }

    private fun openLocationPickerFragment() {
        LocationPickerFragment.newInstance(viewModel.houseItem.value?.id)
            .show(supportFragmentManager, LocationPickerFragment::class.java.simpleName)
    }

    private fun openDateDialog() {
        hideKeyboard()
        val (selectedDate, minDate, maxDate) = viewModel.getDatePickerData()
        val selectedDateCalendar = Calendar.getInstance().apply { time = selectedDate }
        DatePickerDialog(
            this, R.style.Dialog_DatePicker, this, selectedDateCalendar.get(Calendar.YEAR),
            selectedDateCalendar.get(Calendar.MONTH),
            selectedDateCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minDate.time
            datePicker.maxDate = maxDate.time
        }.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onDateSelected(year, month, dayOfMonth)
    }

    override fun onLocationSelected(type: LocationType) {
        if (type is LocationType.SingleVenue)
            viewModel.onHouseSelected(type.venue)
    }

    override val loadingView: LoadingView
        get() = binding.activityNewGuestListLoadingView


    private fun onShareLinkClick(item: NewGuestItem) {
        if (item.invitationId != null) {
            shareInvitation(item.invitationId!!)
        } else {
            viewModel.onShareClicked(item)
        }
    }
}

class NewGuestListAdapter : RendererDiffAdapter(), FormAdapter {

    override fun getFormRowType(adapterPosition: Int): FormRowType {
        return (mItems.getOrNull(adapterPosition) as? FormItem)?.rowType ?: FormRowType.NONE
    }

}
