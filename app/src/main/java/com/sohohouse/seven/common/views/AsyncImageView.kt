package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sohohouse.seven.R

class AsyncImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    @DrawableRes
    private val placeholder: Int

    var circleCrop: Boolean

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageView)
        placeholder = ta.getResourceId(R.styleable.AsyncImageView_placeholder, 0)
        circleCrop = ta.getBoolean(R.styleable.AsyncImageView_circleCrop, false)
        ta.recycle()

        if (circleCrop) {
            outlineProvider = CircleOutlineProvider()
            clipToOutline = true
        }
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        Glide.with(this).clear(this)
        if (resId == -1) {
            setImageDrawable(
                if (placeholder != 0) ContextCompat.getDrawable(
                    context,
                    placeholder
                ) else null
            )
            return
        }
        Glide.with(this)
            .applyDefaultRequestOptions(
                generateRequestOptions().dontAnimate().override(width, height)
            )
            .load(resId)
            .into(this)
    }

    fun setImageUrl(url: String?, placeholder: Int = this.placeholder) {
        Glide.with(this).clear(this)
        if (url.isNullOrEmpty()) {
            setImageDrawable(
                if (placeholder != 0) ContextCompat.getDrawable(
                    context,
                    placeholder
                ) else null
            )
            return
        }

        Glide.with(this)
            .applyDefaultRequestOptions(
                generateRequestOptions().dontAnimate().override(width, height)
            )
            .asBitmap()
            .load(url)
            .into(this)
    }

    fun setImageUrl(
        url: String?,
        placeholder: Int = this.placeholder,
        onResourceReady: (Bitmap) -> Bitmap
    ) {
        Glide.with(this).clear(this)
        if (url.isNullOrEmpty()) {
            setImageDrawable(
                if (placeholder != 0) ContextCompat.getDrawable(
                    context,
                    placeholder
                ) else null
            )
            return
        }

        Glide.with(this)
            .applyDefaultRequestOptions(
                generateRequestOptions().dontAnimate().override(width, height)
            )
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setImageBitmap(onResourceReady(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    private fun generateRequestOptions(): RequestOptions {
        var options = if (placeholder != 0) {
            RequestOptions.placeholderOf(placeholder).error(placeholder)
        } else {
            RequestOptions.errorOf(ColorDrawable(Color.TRANSPARENT))
        }

        options = when (scaleType) {
            ScaleType.CENTER_CROP -> options.fitCenter().optionalCenterCrop()
            ScaleType.FIT_CENTER -> options.fitCenter()
            ScaleType.CENTER_INSIDE -> options.centerInside().optionalFitCenter()
            else -> options.optionalFitCenter()
        }

        if (circleCrop) {
            options = options.optionalCircleCrop()
        }

        return options
    }
}