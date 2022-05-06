package com.sohohouse.seven.housepay.home

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.FragmentHousePayReceiptsBinding
import com.sohohouse.seven.housepay.checkdetail.closed.CheckReceiptFragment
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.network.core.models.housepay.Check

class HousePayReceiptsFragment : BaseMVVMFragment<HousePayReceiptsViewModel>(),
    Loadable.View {

    private val binding by viewBinding(FragmentHousePayReceiptsBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_house_pay_receipts

    override val viewModelClass: Class<HousePayReceiptsViewModel>
        get() = HousePayReceiptsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.listLoading

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HousePayReceiptsAdapter {
            viewCheckReceipt(it)
        }

        initExpandableListView(adapter)

        viewModel.receiptsStateFLow.collectLatest(viewLifecycleOwner) { list ->
            binding.housepayReceiptsNoDataView.isVisible = list.isEmpty()
            if (list.isNotEmpty()) {
                adapter.setItemsList(list as HashMap<CharSequence, List<Check>>)
            }
        }

        observeLoadingState(viewLifecycleOwner) {
            (requireActivity() as? MainNavigationController)?.setLoadingState(it)
        }

        viewModel.getReceipts()
    }

    private fun initExpandableListView(adapter: HousePayReceiptsAdapter) {
        val width: Int = resources.displayMetrics.widthPixels
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            resources.displayMetrics
        ).toInt()

        with(binding) {
            housepayReceiptsExpandableListView.setIndicatorBoundsRelative(width - px, width)
            housepayReceiptsExpandableListView.setAdapter(adapter)
        }
    }

    private fun viewCheckReceipt(checkId: String) {
        CheckReceiptFragment.newInstance(checkId).show(
            childFragmentManager,
            CheckReceiptFragment.TAG
        )
    }
}