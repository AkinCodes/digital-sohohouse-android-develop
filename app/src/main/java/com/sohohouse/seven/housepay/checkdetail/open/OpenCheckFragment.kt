package com.sohohouse.seven.housepay.checkdetail.open

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.PinToTopOnItemPrepended
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.amountinput.AmountInputFragment
import com.sohohouse.seven.common.views.amountinput.AmountInputMode
import com.sohohouse.seven.common.views.dialog.CustomModalDialog
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentOpenCheckBinding
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.HousePayUiHelper
import com.sohohouse.seven.housepay.checkdetail.Under27DiscountDetailsBottomSheet
import com.sohohouse.seven.housepay.checkdetail.closed.CheckReceiptFragment
import com.sohohouse.seven.housepay.checkdetail.render.renderer.*
import com.sohohouse.seven.housepay.payment.ChoosePaymentDialogFragment
import com.sohohouse.seven.housepay.payment.ChoosePaymentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class OpenCheckFragment : BaseMVVMBottomSheet<OpenCheckViewModel>() {

    companion object {
        val TAG = OpenCheckFragment::class.java.simpleName
        const val REQ_KEY_CHECK_DISMISSED = "REQ_KEY_CHECK_DISMISSED"

        fun newInstance(checkId: String): OpenCheckFragment {
            return OpenCheckFragment().apply {
                arguments = bundleOf(BundleKeys.CHECK_ID to checkId)
            }
        }
    }

    @Inject
    lateinit var assistedFactory: OpenCheckViewModel.Factory

    override val contentLayout: Int
        get() = R.layout.fragment_open_check

    override val viewModelClass: Class<OpenCheckViewModel>
        get() = OpenCheckViewModel::class.java

    override val viewModel: OpenCheckViewModel by fragmentViewModel {
        assistedFactory.create(arguments?.getString(BundleKeys.CHECK_ID) ?: "")
    }

    @Suppress("UNCHECKED_CAST")
    private val adapter: RendererDiffAdapter<CheckItem>
        get() = binding.recyclerView.adapter as RendererDiffAdapter<CheckItem>

    private var _binding: FragmentOpenCheckBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeViewModel()
        setPaymentMethodChooserListener()
    }

    private fun setPaymentMethodChooserListener() {
        setChildFragmentResultListener(ChoosePaymentViewModel.PAYMENT_METHOD_CONFIRMED) { _, _ ->
            viewModel.onPaymentMethodConfirm()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCheck()
    }

    override fun dismiss() {
        setFragmentResult(REQ_KEY_CHECK_DISMISSED)
        super.dismiss()
    }

    private fun observeViewModel() {
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun observeViewModelEvents() {
        lifecycleScope.launch {
            viewModel.event
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    handle(it)
                }
        }
    }

    private fun handle(it: OpenCheckEvent) {
        when (it) {
            is OpenCheckEvent.ShowRichDialog -> {
                showRichDialog(it)
            }
            is OpenCheckEvent.OpenUrlInWebView -> {
                openUrlInWebVew(it)
            }
            OpenCheckEvent.ShowU27DiscountDetailsModal -> {
                showU27DiscountDetailsModal()
            }
            OpenCheckEvent.ShowU27DiscountDetailsModal -> {
                showU27DiscountDetailsModal()
            }
            is OpenCheckEvent.OpenCustomTipInput -> {
                openCustomTipInput(it)
            }
            is OpenCheckEvent.OpenHouseCreditInput -> {
                openHouseCreditInput(it)
            }
            OpenCheckEvent.OpenPaymentMethod -> {
                openPaymentMethodChooser()
            }
            OpenCheckEvent.DismissSelf -> {
                dismiss()
            }
            is OpenCheckEvent.GoToReceipt -> {
                goToReceipt(it)
            }
            OpenCheckEvent.ShowHousePayTerms -> showHousePayTerms()
        }
    }

    private fun goToReceipt(event: OpenCheckEvent.GoToReceipt) {
        CheckReceiptFragment.newInstance(event.checkId)
            .showSafe(parentFragmentManager)
        dismiss()
    }

    private fun openPaymentMethodChooser() {
        ChoosePaymentDialogFragment()
            .showSafe(childFragmentManager)
    }

    private fun openUrlInWebVew(it: OpenCheckEvent.OpenUrlInWebView) {
        WebViewBottomSheetFragment.withUrl(
            url = it.url,
            useBearerToken = it.requiresAuth
        ).showSafe(childFragmentManager)
    }

    private fun openHouseCreditInput(item: OpenCheckEvent.OpenHouseCreditInput) {
        AmountInputFragment.newInstance(
            AmountInputMode.Credits(
                initialAmountCents = item.initialAmountCents,
                creditAvailableCents = item.maxAmountCents,
                leftToPayCents = item.leftToPayCents,
                currencyCode = item.currencyCode
            )
        ) {
            viewModel.onHouseCreditConfirmed(it)
        }.showSafe(childFragmentManager)
    }

    private fun openCustomTipInput(item: OpenCheckEvent.OpenCustomTipInput) {
        AmountInputFragment.newInstance(
            AmountInputMode.Tips(
                item.initialAmountCents,
                item.currencyCode,
                item.leftToPayCents
            )
        ) {
            viewModel.onCustomTipConfirmed(it)
        }.showSafe(childFragmentManager)
    }

    private fun showU27DiscountDetailsModal() {
        Under27DiscountDetailsBottomSheet().showSafe(childFragmentManager)
    }

    private fun showRichDialog(item: OpenCheckEvent.ShowRichDialog) {
        CustomModalDialog.Builder()
            .withTitle(item.title)
            .withMessage(item.message)
            .withPositiveBtnText(item.confirmCta)
            .withNegativeBtnText(item.cancelCta)
            .withPositiveBtnClickListener {
                item.onConfirm()
            }
            .build()
            .showSafe(childFragmentManager)
    }

    private fun showHousePayTerms() {
        AcceptHousePayTermsFragment.Builder()
            .withPositiveBtnClickListener { viewModel.acceptHousePayTerms() }
            .build()
            .showSafe(childFragmentManager)
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    handle(it)
                }
        }
    }

    private fun handle(it: OpenCheckUiState) {
        when (it) {
            is OpenCheckUiState.Initial -> {
                onInitialState()
            }
            is OpenCheckUiState.Loading -> {
                onLoading()
            }
            is OpenCheckUiState.Working -> {
                onWorkingCheck(it)
            }
            is OpenCheckUiState.Paying -> {
                onPayingCheck(it)
            }
            is OpenCheckUiState.Empty -> {
                onEmptyCheck(it)
            }
            is OpenCheckUiState.ErrorState -> {
                onFailedToLoadCheck(it)
            }
            OpenCheckUiState.CheckPaid -> {
                onCheckPaid()
            }
        }
    }

    private fun onCheckPaid() {
        with(binding) {
            swipeRefreshLayout.isRefreshing = false
            settleTab.setInvisible()
            addPaymentMethod.setInvisible()
            checkPaidState.root.setVisible()
            checkHeader.lasUpdatedLabel.setGone()
            checkPaymentButtons.root.setInvisible()
            checkHeader.pageTitle.setText(R.string.housepay_payment_successful)
            checkHeader.lasUpdatedLabel.setGone()
            checkPaymentButtons.root.setInvisible()
            recyclerView.setInvisible()
            checkHeader.pageTitle.setText(R.string.housepay_payment_successful)
        }
    }

    private fun onInitialState() {
        with(binding) {
            settleTab.setInvisible()
            addPaymentMethod.setInvisible()
            checkPaymentButtons.root.setInvisible()
            checkEmptyState.root.setInvisible()
            checkPaidState.root.setInvisible()
            reloadableErrorStateView.setInvisible()
        }
    }

    private fun onPayingCheck(state: OpenCheckUiState.Paying) {
        with(binding) {
            checkHeader.lasUpdatedLabel.setVisible()
            swipeRefreshLayout.isRefreshing = false
            checkHeader.lasUpdatedLabel.markedTime = state.updatedAt
            recyclerView.setVisible()
            recyclerView.setDeepChildrenEnabled(
                enable = true,
                changeAlpha = true
            )
            checkEmptyState.root.setInvisible()
            settleTab.setInvisible()

            if (state.paymentMethod == null) {
                addPaymentMethod.setVisible()
                addPaymentMethod.isEnabled = true
                checkPaymentButtons.root.setInvisible()
            } else {
                addPaymentMethod.setInvisible()
                checkPaymentButtons.root.setEnabledWithAlpha(
                    enable = true,
                    changeAlpha = true
                )
                checkPaymentButtons.root.setVisible()
                checkPaymentButtons.checkPaymentOptionLabel.text =
                    state.paymentMethod?.getLabel(
                        requireContext().stringProvider
                    )
                checkPaymentButtons.checkPaymentOptionIcon.setImageResourceNotNull(state.paymentMethod?.icon)
                checkPaymentButtons.checkPayNow.setLoading(
                    loading = state.paymentInProgress,
                    enabled = state.paymentMethod != null && state.paymentInProgress.not()
                )
            }
        }
        adapter.submitItems(state.items)
        setContentEnabled(enabled = state.paymentInProgress.not())
    }

    private fun onFailedToLoadCheck(state: OpenCheckUiState.ErrorState) {
        with(binding) {
            recyclerView.setInvisible()
            reloadableErrorStateView.setVisible()
            checkEmptyState.root.setInvisible()
            settleTab.setInvisible()
            addPaymentMethod.setInvisible()
            checkPaymentButtons.root.setInvisible()
            with(reloadableErrorStateView) {
                setTitle(state.details.title)
                setSubtitle(state.details.message)
            }
        }
    }

    private fun onEmptyCheck(state: OpenCheckUiState.Empty) {
        with(binding) {
            checkPaymentButtons.root.setInvisible()
            addPaymentMethod.setInvisible()
        }
    }

    private fun onWorkingCheck(state: OpenCheckUiState.Working) {
        with(binding) {
            checkHeader.lasUpdatedLabel.setVisible()
            swipeRefreshLayout.isRefreshing = false
            checkHeader.lasUpdatedLabel.markedTime = state.updatedAt
            recyclerView.setVisible()
            recyclerView.setDeepChildrenEnabled(enable = true, changeAlpha = true)
            settleTab.setVisible()
            settleTab.isEnabled = true
            addPaymentMethod.setInvisible()
            checkEmptyState.root.setInvisible()
            checkPaymentButtons.root.setInvisible()
        }
        adapter.submitItems(state.items)
    }

    private fun onLoading() {
        with(binding) {
            checkHeader.lasUpdatedLabel.setVisible()
            checkHeader.lasUpdatedLabel.setTextOverrideTimer(getString(R.string.housepay_check_updating))
        }
        setContentEnabled(false)
    }

    private fun setContentEnabled(enabled: Boolean) {
        with(binding) {
            recyclerView.setDeepChildrenEnabled(enable = enabled, changeAlpha = true)
            settleTab.isEnabled = enabled
            checkPaymentButtons.checkPayNow.isEnabled = enabled
            checkPaymentButtons.root.setEnabledWithAlpha(enable = enabled, changeAlpha = true)
        }
    }

    private fun setUpViews() {
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshCheck()
            }
            recyclerView.adapter = createAdapter()
            adapter.registerAdapterDataObserver(PinToTopOnItemPrepended(recyclerView))
            checkHeader.lasUpdatedLabel.setUp(
                HousePayUiHelper.createLastUpdatedConfig()
            )
            checkHeader.closeBtn.clicks {
                dismiss()
            }
            reloadableErrorStateView.reloadClicks {
                binding.reloadableErrorStateView.setInvisible()
                viewModel.refreshCheck()
            }
            settleTab.clicks {
                viewModel.onSettleCheckClick()
            }
            addPaymentMethod.clicks {
                viewModel.onPaymentMethodClick()
            }
            checkPaymentButtons.root.clicks {
                viewModel.onPaymentMethodClick()
            }
            checkPaymentButtons.checkPayNow.clicks {
                viewModel.onPayNowClick()
            }
        }
    }

    override val isDraggable: Boolean
        get() = false

    private fun createAdapter() = RendererDiffAdapter<CheckItem>().apply {
        registerRenderers(
            LineItemRenderer(),
            TabIdRenderer(),
            WaiterIdRenderer(),
            SubtotalDueRenderer(),
            VenueHeaderRenderer(),
            ReveneCenterRenderer(),
            LineBreakRenderer(),
            OrderTotalRenderer(),
            VATlineItemRenderer(),
            ServiceChargeItemRenderer(),
            ServiceChargeAndTipsNoteRenderer(),
            NotificationBannerRenderer(),
            DiscountItemRenderer(),
            U27DiscountReminderRenderer(),
            SelectTipRenderer(),
            HouseCreditRenderer()
        )
    }
}