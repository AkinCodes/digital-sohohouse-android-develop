package com.sohohouse.seven.more.contact.recycler

import android.annotation.SuppressLint
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ViewTextInputEnquiryBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

interface EnquiryTextChangedListener {
    fun textChanges(text: CharSequence)
}

class TextInputEnquiryViewHolder(private val binding: ViewTextInputEnquiryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        with(binding.inquiryText) {
            imeOptions = EditorInfo.IME_ACTION_DONE
            setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        }
    }

    @SuppressLint("CheckResult")
    fun bind(item: TextInputEnquiryItemType, listener: EnquiryTextChangedListener) {
        with(binding.inquiryText) {
            val text = getText(item)
            setText(text, TextView.BufferType.EDITABLE)
            textChanges()
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listener.textChanges(it)
                }
        }
    }

    fun focusOnContactForm() = with(binding.inquiryText) {
        this.parent.requestChildFocus(this, this)
    }

    private fun getText(item: TextInputEnquiryItemType): String {
        if (item.text.isEmpty().not() || item.hints.isEmpty()) return item.text.toString()

        val builder = StringBuilder().apply {
            with(item.hints.iterator()) {
                while (hasNext()) {
                    append(getString(next()))
                    if (hasNext()) {
                        append(System.lineSeparator())
                    }
                }
            }
        }
        return builder.toString().also { text ->
            item.text = text
        }
    }
}