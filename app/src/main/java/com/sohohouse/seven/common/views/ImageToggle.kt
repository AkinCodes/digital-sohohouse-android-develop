package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.sohohouse.seven.R

class ImageToggle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var onImageSrc: Int = -1
    private var offImageSrc: Int = -1
    private var onAltText: String = ""
    private var offAltText: String = ""
    private var isChecked = false

    init {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ImageToggle)

            onImageSrc = attributes.getResourceId(R.styleable.ImageToggle_onSrc, -1)
            offImageSrc = attributes.getResourceId(R.styleable.ImageToggle_offSrc, -1)
            onAltText = attributes.getString(R.styleable.ImageToggle_onAltText).orEmpty()
            offAltText = attributes.getString(R.styleable.ImageToggle_offAltText).orEmpty()

            contentDescription = if (isChecked) onAltText else offAltText
            setImage(if (isChecked) onImageSrc else offImageSrc)

            attributes.recycle()
        }
    }

    var checked: Boolean
        get() = isChecked
        set(value) {
            isChecked = value
            setImage(if (isChecked) onImageSrc else offImageSrc)
            contentDescription = if (isChecked) onAltText else offAltText
        }

    fun setOnToggleListener(listener: (Boolean) -> Any) {
        setOnClickListener {
            isChecked = !isChecked
            setImageResource(if (isChecked) onImageSrc else offImageSrc)
            contentDescription = if (isChecked) onAltText else offAltText
            listener(isChecked)
        }
    }

    private fun setImage(drawableRes: Int) =
        drawableRes.takeIf { it != -1 }?.let { setImageResource(it) }
}
