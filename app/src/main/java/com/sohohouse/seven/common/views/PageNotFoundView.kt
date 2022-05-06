package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.ViewPageNotFoundBinding

class PageNotFoundView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding = ViewPageNotFoundBinding
        .inflate(LayoutInflater.from(context), this)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PageNotFoundView)
        val title = ta.getString(R.styleable.PageNotFoundView_title)
            ?: context.getString(R.string.label_page_not_available)
        val subtitle = ta.getString(R.styleable.PageNotFoundView_subtitle)
            ?: ""
        val buttonText = ta.getString(R.styleable.PageNotFoundView_buttonText)
            ?: context.getString(R.string.go_back_cta)
        ta.recycle()

        isVisible = isInEditMode

        setTitle(title)
        setSubtitle(subtitle)
        setButtonText(buttonText)
    }

    private fun setTitle(text: String?) {
        binding.title.text = text
    }

    private fun setSubtitle(text: String?) {
        binding.supporting.text = text
    }

    private fun setButtonText(text: String?) {
        binding.goBackBtn.text = text
    }

    fun buttonClick(onClick: () -> Unit) {
        binding.goBackBtn.clicks { onClick() }
    }

}