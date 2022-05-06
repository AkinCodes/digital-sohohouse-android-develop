package com.sohohouse.seven.profile.edit.pronouns

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.design.adapter.RendererAdapter
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.FragmentEditPronounsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EditPronounsFragment : BaseMVVMBottomSheet<EditPronounsViewModel>() {

    companion object {
        const val REQ_CODE = "46378472390"

        class Factory(private val pronouns: MutableList<String>) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return EditPronounsFragment().apply {
                    arguments = bundleOf(BundleKeys.PRONOUNS to pronouns)
                }
            }
        }
    }

    override val viewModelClass: Class<EditPronounsViewModel>
        get() = EditPronounsViewModel::class.java
    override val contentLayout: Int
        get() = R.layout.fragment_edit_pronouns

    private val adapter = RendererAdapter<EditPronounsViewModel.PronounItem>().apply {
        registerRenderer(PronounRenderer { viewModel.onPronounSelected(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentEditPronounsBinding.bind(view).apply {
            pronounsRv.adapter = adapter
            pronounsRv.layoutManager = FlexboxLayoutManager(requireContext())
            btnConfirm.clicks {
                viewModel.onConfirmed()
                setFragmentResult(
                    REQ_CODE,
                    bundleOf(BundleKeys.PRONOUNS to viewModel.selectedPronouns)
                )
                dismiss()
            }
            btnCancel.clicks {
                dismiss()
            }
            closeBtn.clicks {
                dismiss()
            }
        }

        viewModel.items.onEach {
            adapter.submitItems(it)
        }.launchIn(lifecycleScope)
    }

    private val selectedPronouns: List<String>
        get() {
            return arguments?.getStringArrayList(BundleKeys.PRONOUNS) ?: emptyList()
        }

    @Inject
    lateinit var assistedFactory: EditPronounsViewModel.Factory

    override val viewModel: EditPronounsViewModel by fragmentViewModel {
        assistedFactory.create(selectedPronouns.toMutableList())
    }

}