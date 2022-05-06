package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ViewTagItemBinding

class TagItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var binding : ViewTagItemBinding? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TagItemView, defStyleAttr, 0)
        val title = ta.getString(R.styleable.TagItemView_title) ?: ""
        ta.recycle()

        orientation = VERTICAL

        if (!isInEditMode) {
            binding = ViewTagItemBinding.inflate(LayoutInflater.from(context), this)
        }
        binding?.title?.text = title
    }

    var tagName: String?
        get() = binding?.tagText?.text.toString()
        set(value) {
            binding?.tagText?.text = value
        }
}