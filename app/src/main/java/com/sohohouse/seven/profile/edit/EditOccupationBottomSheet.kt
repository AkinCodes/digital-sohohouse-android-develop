package com.sohohouse.seven.profile.edit

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.databinding.BottomsheetTextInputWithSuggestionsBinding
import com.sohohouse.seven.network.core.models.Occupation
import javax.inject.Inject

class EditOccupationBottomSheet : BaseBottomSheet(), Injectable,
    TextSuggestionsAdapter.Listener<EditOccupationViewModel.OccupationItem> {

    companion object {
        const val EXTRA_INITIAL_VALUE = "EXTRA_INITIAL_VALUE"
        const val REQ_KEY_PICK_OCCUPATION = "REQ_KEY_PICK_OCCUPATION"

        class Factory constructor(val initialValue: String?) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return EditOccupationBottomSheet().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_INITIAL_VALUE, this@Factory.initialValue)
                    }
                }
            }
        }
    }

    val binding by viewBinding(BottomsheetTextInputWithSuggestionsBinding::bind)

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val adapter = TextSuggestionsAdapter(this)

    private val textWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable?) {
            viewModel.onTextChange(s?.toString() ?: "")
        }
    }

    private val viewModel
        get() = ViewModelProvider(
            this,
            factory
        ).get(EditOccupationViewModel::class.java)

    override val contentLayout: Int
        get() = R.layout.bottomsheet_text_input_with_suggestions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.input.addTextChangedListener(textWatcher)
        setCurrentItems()
        setUpRecyclerView()
        observeItems()
        binding.header.editProfileModalDone.setOnClickListener { viewModel.onUserConfirm() }
        binding.input.focusAndShowKeyboard()
    }

    private fun setResult(occupation: Occupation?) {
        (context as? Listener?)?.onOccupationConfirmed(occupation)
        setFragmentResult(
            REQ_KEY_PICK_OCCUPATION,
            bundleOf(BundleKeys.OCCUPATION to occupation?.name)
        )
        dismiss()
    }

    private fun showInvalidOccupationMsg() {
        CustomDialogFactory.createThemedAlertDialog(
            requireContext(),
            title = getString(R.string.title_invalid_profession),
            message = getString(R.string.subtitle_invalid_profession),
            positiveButtonText = getString(R.string.general_error_ok_cta)
        )
            .show()
    }

    private fun setCurrentItems() {
        @Suppress("UNCHECKED_CAST")

        setInput(requireArguments().getSerializable(EXTRA_INITIAL_VALUE) as String?)
        binding.header.editProfileModalFieldTitle.setText(R.string.profile_occupation_label)
    }

    private fun observeItems() {
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.submitList(it ?: emptyList(), performDiffing = false)
            showHideSuggestionsHeader()
        }
        viewModel.showInvalidOccupation.observe(viewLifecycleOwner) {
            showInvalidOccupationMsg()
        }
        viewModel.occupationConfirmed.observe(viewLifecycleOwner) { setResult(it) }
    }

    private fun setUpRecyclerView() = with(binding) {
        suggestionsRv.layoutManager = LinearLayoutManager(context)
        suggestionsRv.itemAnimator = null
        suggestionsRv.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.input.removeTextChangedListener(textWatcher)
    }

    interface Listener {
        fun onOccupationConfirmed(occupation: Occupation?)
    }

    override fun onSuggestionSelected(suggestion: EditOccupationViewModel.OccupationItem) {
        setInput(suggestion.value)
    }

    private fun setInput(value: String?) {
        binding.input.setText(value)
        binding.input.setSelection(value?.length ?: 0)
    }

    private fun showHideSuggestionsHeader() {
        val suggestions = viewModel.autoCompleteSuggestions.value
        binding.suggestionsHeader.isInvisible = suggestions.isNullOrEmpty()
    }
}
