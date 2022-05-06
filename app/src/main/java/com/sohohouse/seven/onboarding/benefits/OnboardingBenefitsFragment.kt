package com.sohohouse.seven.onboarding.benefits

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.views.ItemPaddingDecoration
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentBenefitsBinding

@Keep
class OnboardingBenefitsFragment : BaseMVVMFragment<OnboardingBenefitsViewModel>(),
    Loadable.View, Errorable.View {

    override val viewModelClass: Class<OnboardingBenefitsViewModel>
        get() = OnboardingBenefitsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.fragmentBenefitsLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorView

    override val contentLayoutId: Int
        get() = R.layout.fragment_benefits

    private val binding by viewBinding(FragmentBenefitsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OnboardingBenefitsAdapter()

        binding.setupViews(adapter)

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        viewModel.navigation.observe(viewLifecycleOwner) { intent ->
            startActivity(intent)
            requireActivity().finish()
        }
        observeLoadingState(viewLifecycleOwner)
        observeErrorState(viewLifecycleOwner) { viewModel.getBenefits() }
        viewModel.getBenefits()
    }

    private fun FragmentBenefitsBinding.setupViews(adapter: OnboardingBenefitsAdapter) {
        with(toolbar) {
            toolbarTitle.setText(R.string.label_membership)
            toolbarBackBtn.setOnClickListener { requireActivity().onBackPressed() }
        }

        with(recyclerView) {
            this.adapter = adapter
            addItemDecoration(
                ItemPaddingDecoration(
                    RecyclerView.VERTICAL,
                    resources.getDimensionPixelSize(R.dimen.dp_16)
                )
            )
        }
    }
}