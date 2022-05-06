package com.sohohouse.seven.housepay.checkdetail.closed

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.extensions.setInvisible
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.startActivitySafely
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.databinding.FragmentCheckReceiptBinding
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.HousePayUiHelper
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckFragment
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckViewModel
import com.sohohouse.seven.housepay.checkdetail.render.renderer.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckReceiptFragment : BaseMVVMBottomSheet<CheckReceiptViewModel>() {

    companion object {
        val TAG = CheckReceiptFragment::class.java.simpleName

        fun newInstance(checkId: String): CheckReceiptFragment {
            return CheckReceiptFragment().apply {
                arguments = bundleOf(BundleKeys.CHECK_ID to checkId)
            }
        }
    }

    @Inject
    lateinit var assistedFactory: CheckReceiptViewModel.Factory

    override val contentLayout: Int
        get() = R.layout.fragment_check_receipt
    override val viewModelClass: Class<CheckReceiptViewModel>
        get() = CheckReceiptViewModel::class.java

    private var _binding: FragmentCheckReceiptBinding? = null
    private val binding: FragmentCheckReceiptBinding get() = _binding!!

    @Suppress("UNCHECKED_CAST")
    private val adapter: RendererDiffAdapter<CheckItem>
        get() = binding.receiptRv.adapter as RendererDiffAdapter<CheckItem>

    override val viewModel: CheckReceiptViewModel by fragmentViewModel {
        assistedFactory.create(arguments?.getString(BundleKeys.CHECK_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCheckReceiptBinding.bind(view)

        setUpViews()

        observeViewModelState()
        observeViewModelEvents()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshCheck()
    }

    private fun observeViewModelEvents() {
        lifecycleScope.launch {
            viewModel.event
                .flowWithLifecycle(lifecycle)
                .collect {
                    handle(it)
                }
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(lifecycle)
                .collect {
                    handle(it)
                }
        }
    }

    private fun setUpViews() {
        binding.checkHeader.lasUpdatedLabel.setUp(
            HousePayUiHelper.createLastUpdatedConfig()
        )
        binding.checkHeader.closeBtn.setOnClickListener {
            dismiss()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshCheck()
        }
        binding.checkHeader.pageTitle.text = getString(R.string.housepay_your_receipt_title)

        binding.receiptRv.adapter = createAdapter()
        binding.receiptRv.layoutManager = LinearLayoutManager(requireContext())

        binding.reloadableErrorStateView.reloadClicks {
            viewModel.refreshCheck()
        }
    }

    private fun handle(event: CheckReceiptEvent) {
        when (event) {
            is CheckReceiptEvent.CallPhone -> {
                requireContext().startActivitySafely(
                    IntentUtils.dialIntent(event.phoneNumber)
                )
            }
            is CheckReceiptEvent.OpenLink -> {
                requireContext().startActivitySafely(
                    IntentUtils.openUrlIntent(event.url)
                )
            }
        }
    }

    private fun handle(state: CheckReceiptUiState) {
        when (state) {
            CheckReceiptUiState.Initial -> showInitialState()
            CheckReceiptUiState.Loading -> showLoadingState()
            is CheckReceiptUiState.Receipt -> showReceiptState(state)
            is CheckReceiptUiState.ErrorState -> showErrorState(state)
        }
    }

    private fun showErrorState(state: CheckReceiptUiState.ErrorState) {
        with(binding.reloadableErrorStateView) {
            setVisible()
            setTitle(state.error.title)
            setSubtitle(state.error.message)
        }
        binding.receiptRv.setInvisible()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.checkHeader.lasUpdatedLabel.setInvisible()
    }

    private fun showInitialState() {

    }

    private fun showLoadingState() {
        binding.checkHeader.lasUpdatedLabel.setVisible()
        binding.checkHeader.lasUpdatedLabel.setTextOverrideTimer(
            getString(R.string.housepay_check_updating)
        )
    }

    private fun showReceiptState(state: CheckReceiptUiState.Receipt) {
        binding.checkHeader.lasUpdatedLabel.setVisible()
        binding.receiptRv.setVisible()
        binding.swipeRefreshLayout.isRefreshing = false
        adapter.submitItems(state.items)
        binding.checkHeader.lasUpdatedLabel.markedTime = state.updatedAt
    }

    private fun createAdapter(): RendererDiffAdapter<CheckItem> {
        return RendererDiffAdapter<CheckItem>().apply {
            registerRenderers(
                EmailReceiptItemRenderer,
                CheckLinkItemRenderer,
                DiscountNotAppliedWalkedOutItemRenderer,
                PaymentDetailsItemRenderer,
                VenueDetailsItemRenderer,
                ReceiptPaymentDetailsHeaderRenderer,
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
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override val isDraggable: Boolean
        get() = false
}