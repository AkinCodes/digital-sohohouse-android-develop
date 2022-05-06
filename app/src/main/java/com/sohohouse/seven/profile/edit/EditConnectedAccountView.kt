package com.sohohouse.seven.profile.edit

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.databinding.ItemEditConnectedAccountBinding
import com.sohohouse.seven.profile.Error
import com.sohohouse.seven.profile.concatenateMsg

class EditConnectedAccountView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val binding = ItemEditConnectedAccountBinding
        .inflate(LayoutInflater.from(context), this, true)

    init {

        if (!isInEditMode) {
            attributeSet?.let {
                val typedArray =
                    context.obtainStyledAttributes(it, R.styleable.EditConnectedAccountView)
                setTextToView(
                    typedArray,
                    binding.editConnectedAccountLabel,
                    R.styleable.EditConnectedAccountView_label
                )
                setHintToView(
                    typedArray,
                    binding.editConnectedAccountValue,
                    R.styleable.EditConnectedAccountView_hint
                )
                typedArray.recycle()
            }

            watchFilledState()
        }
    }

    private fun watchFilledState() {
        setFilledState()
        binding.editConnectedAccountValue.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                setFilledState()
                clearErrors()
            }
        })
    }

    private fun setFilledState() {
        binding.connectedAccountFilledIndicator.isActivated =
            binding.editConnectedAccountValue.text?.toString()?.isNotEmpty() == true
    }

    var value: String?
        get() = binding.editConnectedAccountValue.text.toString()
        set(value) {
            binding.editConnectedAccountValue.setText(value)
        }

    fun setErrors(errors: Set<Error>) = with(binding) {
        if (errors.isNotEmpty()) {
            connectedAccountFilledIndicator.imageTintList =
                ColorStateList.valueOf(getAttributeColor(R.attr.colorError))
            editConnectedAccountError.visibility = View.VISIBLE
            editConnectedAccountError.text = errors.concatenateMsg(resources)
        }
    }

    fun setHint(hint: String) {
        binding.editConnectedAccountValue.hint = hint
    }

    fun setLabel(label: String) {
        binding.editConnectedAccountLabel.text = label
    }

    fun clearErrors() = with(binding) {
        connectedAccountFilledIndicator.imageTintList = null
        editConnectedAccountError.setGone()
    }

    private fun setTextToView(typedArray: TypedArray, textView: TextView, index: Int) {
        if (index in 0 until typedArray.length() && typedArray.hasValue(index)) {
            val string = typedArray.getString(index)
            textView.text = string
            textView.contentDescription = string
        }
    }

    private fun setHintToView(typedArray: TypedArray, textView: TextView, index: Int) {
        if (index in 0 until typedArray.length() && typedArray.hasValue(index)) {
            val string = typedArray.getString(index)
            textView.hint = string
        }
    }

    private var textWatcher: TextWatcher? = null

    fun setTextChangeListener(afterTextChanged: (input: String) -> Unit) {
        binding.editConnectedAccountValue.removeTextChangedListener(this.textWatcher)
        this.textWatcher = object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged(s?.toString() ?: "")
            }
        }
        binding.editConnectedAccountValue.addTextChangedListener(textWatcher)
    }

    override fun setEnabled(enabled: Boolean) = with(binding) {
        super.setEnabled(enabled)
        editConnectedAccountValue.isEnabled = enabled
        val alpha = if (enabled) 1f else 0.5f
        editConnectedAccountLabel.alpha = alpha
        editConnectedAccountValue.alpha = alpha
        connectedAccountFilledIndicator.alpha = alpha
    }

}