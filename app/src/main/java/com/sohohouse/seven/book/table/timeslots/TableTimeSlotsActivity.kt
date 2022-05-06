package com.sohohouse.seven.book.table.timeslots

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.table.EditTableSearchBottomSheet
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.TableBookingHouseDetailsViewHolder
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.completebooking.TableCompleteBookingActivity
import com.sohohouse.seven.common.BundleKeys.IS_SELECT_RESTAURANT_STATE
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.design.adapter.RendererAdapter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.databinding.ActivityTableTimeslotsBinding
import com.sohohouse.seven.network.core.models.Menu

class TableTimeSlotsActivity : BaseMVVMActivity<TableTimeSlotsViewModel>(), Loadable.View {

    companion object {
        private const val EXTRA_DETAILS = "details"
        const val RESULT_SEARCH_EDITED = 4536274

        fun newIntent(
            context: Context,
            details: TableBookingDetails,
            isSelectRestaurantState: Boolean
        ): Intent {
            return Intent(context, TableTimeSlotsActivity::class.java).apply {
                this.putExtra(EXTRA_DETAILS, details)
                this.putExtra(IS_SELECT_RESTAURANT_STATE, isSelectRestaurantState)
            }
        }
    }

    override val viewModelClass: Class<TableTimeSlotsViewModel>
        get() = TableTimeSlotsViewModel::class.java

    private val details by lazy { intent.getSerializableExtra(EXTRA_DETAILS) as TableBookingDetails }
    private val timeSlotsAdapter = RendererAdapter<BookSlot>()
    private val menusAdapter = RendererAdapter<Menu>()

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private val binding by viewBinding(ActivityTableTimeslotsBinding::bind)

    override fun getContentLayout(): Int = R.layout.activity_table_timeslots

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            btnConfirm.setOnClickListener { onBookSlotClick() }
            editSearchBtn.setOnClickListener { openEditSearch() }

            timeSlotsAdapter.registerRenderer(TimeSlotRenderer(::slotClick))
            menusAdapter.registerRenderer(MenuRenderer())
            listTimeSlots.adapter = timeSlotsAdapter
            listTimeSlots.layoutManager =
                GridLayoutManager(
                    this@TableTimeSlotsActivity, TableBookingUtil.TIME_SLOTS_SPAN_COUNT
                )

            menusRv.adapter = menusAdapter
            menusRv.layoutManager = LinearLayoutManager(
                this@TableTimeSlotsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            initDetails(viewModel.init(details))

            observeLoadingState(lifecycleOwner, ::onLoadingStateChange)
            viewModel.isConfirmEnabled.observe(lifecycleOwner, { confirmButtonUpdate(it) })
            viewModel.timeSlots.observe(lifecycleOwner, { populateTimeSlots(it) })
            viewModel.confirmation.observe(lifecycleOwner, { gotoConfirmationScreen(it) })
            viewModel.bookingErrorMessage.observe(lifecycleOwner, { showErrorMessage(it) })
        }
    }

    override val loadingView: LoadingView?
        get() = binding.rootLoading

    private fun ActivityTableTimeslotsBinding.initDetails(details: TableBookingDetails) {
        restaurantTitle.text = details.name
        tableImage.setImageFromUrl(details.imageUrl)
        specialNotesDescription.text = details.specialNotes
        descriptionText.text = details.description
        TableBookingHouseDetailsViewHolder(houseDetails).bind(
            details.venueDetails
        )
        populateMenus(details)
    }

    private fun ActivityTableTimeslotsBinding.populateMenus(details: TableBookingDetails) {
        viewMenuTitle.setVisible(details.menus.size == 1)
        viewMenuTitle.setOnClickListener {
            startActivitySafely(IntentUtils.openUrlIntent(details.menus.firstOrNull()?.menuUrl))
        }

        if (details.menus.size > 1) {
            menusTitle.setVisible()
            menusRv.setVisible()
            menusAdapter.submitItems(details.menus)
        }
    }

    private fun confirmButtonUpdate(isEnabled: Boolean) {
        binding.btnConfirm.isEnabled = isEnabled
    }

    private fun populateTimeSlots(slots: List<BookSlot>) {
        timeSlotsAdapter.submitItems(slots)
    }

    private fun showErrorMessage(stringId: Int) {
        TableBookingUtil.createErrorDialog(this, stringId) { showContactUs() }
            .show()
    }

    private fun showContactUs() {
        openWebView(supportFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

    private fun slotClick(slot: BookSlot) {
        viewModel.selectSlot(slot)
    }

    private fun onBookSlotClick() {
        viewModel.confirmSlot()
    }

    private fun gotoConfirmationScreen(slot: TableBookingDetails) {
        resultLauncher.launch(TableCompleteBookingActivity.newIntent(this, slot))
    }

    private fun onLoadingStateChange(loadingState: LoadingState) {
        binding.btnConfirm.isEnabled = loadingState != LoadingState.Loading
        binding.listTimeSlots.setDirectChildrenEnabled(
            loadingState != LoadingState.Loading,
            changeAlpha = true
        )
    }

    private fun openEditSearch() {
        EditTableSearchBottomSheet.newInstance(viewModel.formInput)
            .withResultListener(EditTableSearchBottomSheet.REQ_KEY) { _, bundle ->
                setResult(RESULT_SEARCH_EDITED, Intent().apply {
                    putExtras(bundle)
                })
                finish()
            }.showSafe(supportFragmentManager, EditTableSearchBottomSheet.TAG)
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, intent)
        super.onBackPressed()
    }

}