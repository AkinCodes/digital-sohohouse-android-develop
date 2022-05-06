package com.sohohouse.seven.profile.edit.interests

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.BottomsheetEditInterestsBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.edit.pill.InterestItemRenderer
import com.sohohouse.seven.profile.edit.pill.SectionItemRenderer
import java.io.Serializable

class EditInterestsBottomSheet : BaseMVVMBottomSheet<EditInterestsViewModel>(), Injectable,
    ErrorViewStateViewController {
    companion object {
        const val EXTRA_CURRENT_INTERESTS = "EXTRA_CURRENT_INTERESTS"

        class Factory(val currentInterests: List<Interest>?) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return EditInterestsBottomSheet().apply {
                    arguments = Bundle().apply {
                        putSerializable(
                            EXTRA_CURRENT_INTERESTS,
                            this@Factory.currentInterests as Serializable?
                        )
                    }
                }
            }
        }
    }

    private val binding by viewBinding(BottomsheetEditInterestsBinding::bind)

    private val adapter = RendererDiffAdapter().apply {
        registerRenderers(
            InterestItemRenderer(),
            SectionItemRenderer()
        )
    }

    override val viewModelClass: Class<EditInterestsViewModel>
        get() = EditInterestsViewModel::class.java

    override val contentLayout: Int
        get() = R.layout.bottomsheet_edit_interests

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeItems()
        observeErrorViewEvents()
        initViewModel()
        binding.setUpViews()
    }

    private fun BottomsheetEditInterestsBinding.setUpViews() {
        confirmBtn.setOnClickListener { onConfirmed() }
        headerLayout.resetBtn.clicks { viewModel.onResetClick() }
        headerLayout.header.setText(R.string.title_interests_picker)
        headerLayout.subtext.text = resources.getQuantityString(
            R.plurals.subtitle_interests_picker,
            EditInterestsViewModel.MAX_INTERESTS,
            EditInterestsViewModel.MIN_INTERESTS,
            EditInterestsViewModel.MAX_INTERESTS
        )
        cancelBtn.clicks { dismiss() }
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel.init(
            requireArguments().getSerializable(EXTRA_CURRENT_INTERESTS) as? List<Interest>?
                ?: emptyList()
        )
    }

    private fun showMaxInterestsError() {
        CustomDialogFactory.createThemedAlertDialog(
            requireContext(),
            message = resources.getQuantityString(
                R.plurals.max_interests_error_title,
                EditInterestsViewModel.MAX_INTERESTS,
                EditInterestsViewModel.MAX_INTERESTS
            ),
            positiveButtonText = getString(R.string.general_error_ok_cta)
        )
            .show()
    }

    private fun onConfirmed() {
        (context as Listener).onInterestsConfirmed(viewModel.getSelectedInterests())
        dismiss()
    }

    private fun observeItems() {
        viewModel.items.observe(viewLifecycleOwner) { adapter.setItems(it ?: emptyList()) }
        viewModel.itemChangeEvent.observe(viewLifecycleOwner) { adapter.notifyItemChanged(it) }
        viewModel.showSelectionLimitHit.observe(viewLifecycleOwner) { showMaxInterestsError() }
        viewModel.confirmButtonEnabled.observe(viewLifecycleOwner) {
            binding.confirmBtn.isEnabled = it
        }
    }

    private fun setUpRecyclerView() = with(binding.interestsRv) {
        layoutManager = FlexboxLayoutManager(context)
        itemAnimator = null
        this.adapter = this@EditInterestsBottomSheet.adapter
    }

    interface Listener {
        fun onInterestsConfirmed(interests: List<Interest>)
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorView
    }
}