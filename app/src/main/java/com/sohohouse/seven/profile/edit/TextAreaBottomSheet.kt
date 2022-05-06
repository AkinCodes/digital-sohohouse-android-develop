package com.sohohouse.seven.profile.edit

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.databinding.BottomsheetTextareaBinding

class TextAreaBottomSheet : BaseBottomSheet() {

    companion object {
        private const val NO_MAX_CHARS = -1
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_MAX_CHARS = "EXTRA_MAX_CHARS"
        const val EXTRA_CURRENT_VALUE = "CURRENT_VALUE"
        const val EXTRA_HINT = "EXTRA_HINT"
        const val EXTRA_REQUEST_CODE = "REQUEST_CODE"
        const val EXTRA_INPUT_TYPE = "EXTRA_INPUT_TYPE"
        const val EXTRA_MAX_LINES = "EXTRA_MAX_LINES"

        class Factory(
            val title: String,
            val currentValue: String? = null,
            val hint: Int? = null,
            val maxChars: Int = NO_MAX_CHARS,
            val requestCode: Int? = null,
            val inputType: Int? = null,
            val maxLines: Int? = null
        ) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return TextAreaBottomSheet().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_TITLE, this@Factory.title)
                        putInt(EXTRA_MAX_CHARS, this@Factory.maxChars)
                        putString(EXTRA_CURRENT_VALUE, this@Factory.currentValue)
                        this@Factory.hint?.let { putInt(EXTRA_HINT, it) }
                        this@Factory.requestCode?.let { putInt(EXTRA_REQUEST_CODE, it) }
                        this@Factory.inputType?.let { putInt(EXTRA_INPUT_TYPE, it) }
                        this@Factory.maxLines?.let { putInt(EXTRA_MAX_LINES, it) }
                    }
                }
            }
        }
    }

    private val binding by viewBinding(BottomsheetTextareaBinding::bind)

    private val title by lazy { arguments?.getString(EXTRA_TITLE) }
    private val maxChars by lazy { arguments?.getInt(EXTRA_MAX_CHARS, NO_MAX_CHARS)!! }
    private val hint by lazy { arguments?.getInt(EXTRA_HINT, -1).takeIf { it != -1 } }
    private val requestCode by lazy { arguments?.getInt(EXTRA_REQUEST_CODE) }
    private val inputType by lazy { arguments?.getInt(EXTRA_INPUT_TYPE, -1).takeIf { it != -1 } }
    private val maxLines by lazy { arguments?.getInt(EXTRA_MAX_LINES, -1).takeIf { it != -1 } }

    private val initialValue by lazy { arguments?.getString(EXTRA_CURRENT_VALUE) }

    private val currentValue: String
        get() {
            return binding.freetextBottomsheetInput.text.toString()
        }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) setCharsRemainingLabel(s.length)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override val contentLayout: Int
        get() = R.layout.bottomsheet_textarea

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialValues()
        showKeyboard()
        implementCharacterLimit()
        setDoneListener()
    }

    private fun implementCharacterLimit() {
        if (maxChars != NO_MAX_CHARS) {
            setCharsRemainingLabel(currentValue.length)
            binding.freetextBottomsheetInput.filters = arrayOf(InputFilter.LengthFilter(maxChars))
            binding.freetextBottomsheetInput.addTextChangedListener(textWatcher)
        }
    }

    private fun setCharsRemainingLabel(currentCharCount: Int) {
        val charsRemaining = maxChars - currentCharCount
        if (charsRemaining == maxChars)
            binding.freetextBottomsheetCharsRemaining.text =
                getString(R.string.max_characters_label).replaceBraces(maxChars.toString())
        else
            binding.freetextBottomsheetCharsRemaining.text =
                getString(R.string.characters_remaining_label).replaceBraces(charsRemaining.toString())
    }

    private fun showKeyboard() {
        binding.freetextBottomsheetInput.focusAndShowKeyboard()
    }

    private fun setDoneListener() {
        binding.header.editProfileModalDone.setOnClickListener {
            (context as Listener).onTextAreaInputConfirmed(currentValue, requestCode)
            dismiss()
        }
    }

    private fun setInitialValues() = with(binding) {
        maxLines?.let { freetextBottomsheetInput.maxLines = it }
        inputType?.let { freetextBottomsheetInput.inputType = it }
        title?.let { header.editProfileModalFieldTitle.text = it }
        freetextBottomsheetInput.setText(initialValue)
        hint?.let { freetextBottomsheetInput.setHint(it) }
        freetextBottomsheetInput.setSelection(currentValue.length)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.freetextBottomsheetInput.removeTextChangedListener(textWatcher)
    }

    interface Listener {
        fun onTextAreaInputConfirmed(value: String, requestCode: Int?)
    }
}