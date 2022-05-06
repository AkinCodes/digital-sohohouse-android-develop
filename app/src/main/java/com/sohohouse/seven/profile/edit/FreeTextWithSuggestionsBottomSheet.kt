package com.sohohouse.seven.profile.edit

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.View
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.base.PinToTopAdapterDataObserver
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.databinding.BottomsheetTextInputWithSuggestionsBinding
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class FreeTextWithSuggestionsBottomSheet : BaseBottomSheet(),
    TextSuggestionsAdapter.Listener<AutoCompleteSuggestion> {

    companion object {
        const val MAX_CHARS = 200
    }

    private val binding by viewBinding(BottomsheetTextInputWithSuggestionsBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val contentLayout: Int
        get() = R.layout.bottomsheet_text_input_with_suggestions

    abstract val title: String

    abstract val placeholder: String

    abstract val viewModel: AutoCompleteViewModel<*>

    abstract val initialValue: String?

    private val currentValue: String
        get() {
            return binding.input.text.toString()
        }

    val adapter = TextSuggestionsAdapter(this)

    private lateinit var adapterDataObserver: PinToTopAdapterDataObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterDataObserver = PinToTopAdapterDataObserver(WeakReference(binding.suggestionsRv))
        binding.setInitialValues()
        setUpRecyclerView()
        observeSuggestions()
        observeInputChanges()
        binding.input.focusAndShowKeyboard()
        binding.input.filters = arrayOf(InputFilter.LengthFilter(MAX_CHARS))
    }

    private fun BottomsheetTextInputWithSuggestionsBinding.setInitialValues() {
        header.editProfileModalFieldTitle.text = title
        input.hint = placeholder
        input.setText(initialValue)
        input.setSelection(currentValue.length)

        showHideSuggestionsHeader()
        header.editProfileModalDone.setOnClickListener { onInputConfirmed(currentValue) }
    }

    private fun showHideSuggestionsHeader() {
        val suggestions = viewModel.autoCompleteSuggestions.value
        binding.suggestionsHeader.isInvisible = suggestions.isNullOrEmpty()
    }

    abstract fun onInputConfirmed(currentValue: String?)

    private val textWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable?) {
            viewModel.onTextChange(s?.toString() ?: "")
        }
    }

    private fun observeInputChanges() {
        binding.input.addTextChangedListener(textWatcher)
    }

    private fun observeSuggestions() {
        viewModel.autoCompleteSuggestions.observe(viewLifecycleOwner) {
            adapter.submitList(it ?: emptyList())
            showHideSuggestionsHeader()
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        suggestionsRv.itemAnimator = null
        suggestionsRv.layoutManager = LinearLayoutManager(context)
        suggestionsRv.adapter = adapter
        adapter.registerAdapterDataObserver(adapterDataObserver)
    }

    override fun onSuggestionSelected(suggestion: AutoCompleteSuggestion) = with(binding) {
        input.setText(suggestion.value)
        input.setSelection(suggestion.value.length)
    }

    override fun onDestroyView() {
        binding.input.removeTextChangedListener(textWatcher)
        adapter.unregisterAdapterDataObserver(adapterDataObserver)
        super.onDestroyView()
    }
}