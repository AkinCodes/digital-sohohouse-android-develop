package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ComponentLoadingButtonBinding

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attrs, defStyleAttrs) {

    private val binding = ComponentLoadingButtonBinding.inflate(LayoutInflater.from(context), this)
    private var label: String? = null

    init {
        val styledAttrs =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttrs, 0)
        val label = styledAttrs.getString(R.styleable.LoadingButton_label)
        this.label = label
        binding.button.text = label
        styledAttrs.recycle()
    }

    fun setLoading(loading: Boolean, enabled: Boolean = !loading) {
        isEnabled = enabled
        binding.loadingSpinner.setVisible(loading)
        binding.button.text = if (loading) null else this.label
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.button.setOnClickListener(l)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.button.isEnabled = enabled
    }

}