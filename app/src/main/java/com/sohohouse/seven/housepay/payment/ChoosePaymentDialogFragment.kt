package com.sohohouse.seven.housepay.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.databinding.FragmentHousepayChoosePaymentMethodBinding
import com.sohohouse.seven.more.payment.threeds.AddPayment3dsActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

class ChoosePaymentDialogFragment : BaseMVVMBottomSheet<ChoosePaymentViewModel>() {

    private var _binding: FragmentHousepayChoosePaymentMethodBinding? = null
    private val binding: FragmentHousepayChoosePaymentMethodBinding get() = _binding!!

    @Inject
    lateinit var factory: ChoosePaymentViewModel.Factory

    override val viewModel: ChoosePaymentViewModel
            by lazy { factory.create() }

    override val contentLayout: Int
        get() = R.layout.fragment_housepay_choose_payment_method
    override val viewModelClass: Class<ChoosePaymentViewModel>
        get() = ChoosePaymentViewModel::class.java

    @Suppress("UNCHECKED_CAST")
    private val adapter: RendererDiffAdapter<ChoosePaymentMethodListItem>
        get() = binding.housepayChoosePaymentMethodRv.adapter
                as RendererDiffAdapter<ChoosePaymentMethodListItem>

    private val addNewCardLauncher = registerForActivityResult(
        AddNewCardActivityResultListener()
    ) { cardAdded ->
        if (cardAdded) {
            viewModel.onNewCardAdded()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHousepayChoosePaymentMethodBinding.bind(view)

        setUpViews()
        observeViewModelState()
        observeViewModelEvents()
    }

    override val fixedHeight: Int
        get() = ViewGroup.LayoutParams.WRAP_CONTENT

    private fun setUpViews() {
        with(binding.housepayChoosePaymentMethodRv) {
            adapter = createAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
        binding.housepayChhosePaymentMethodConfirm.clicks {
            viewModel.onConfirmClick()
        }
        binding.housepayChhosePaymentMethodCancel.clicks {
            viewModel.onCancelClick()
        }
    }

    private fun observeViewModelEvents() {
        lifecycleScope.launch {
            viewModel.event
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    handleEvent(it)
                }
        }
    }

    private fun handleEvent(event: ChoosePaymentViewModel.Event) {
        when (event) {
            is ChoosePaymentViewModel.Event.DismissDialog -> {
                setFragmentResult(event.reqKey)
                dismiss()
            }
            ChoosePaymentViewModel.Event.OpenAddNewCard -> {
                addNewCardLauncher.launch(Unit)
            }
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            viewModel.state
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    adapter.submitItems(it.listItems)
                }
        }
    }

    private fun createAdapter() = RendererDiffAdapter<ChoosePaymentMethodListItem>().apply {
        registerRenderers(
            PaymentMethodItemRenderer(),
            AddNewCardItemRenderer()
        )
    }

}

private class AddNewCardActivityResultListener : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, AddPayment3dsActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}