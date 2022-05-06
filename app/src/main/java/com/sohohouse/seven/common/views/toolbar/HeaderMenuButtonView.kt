package com.sohohouse.seven.common.views.toolbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ViewHeaderMenuButtonBinding

class HeaderMenuButtonView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : ViewSwitcher(context, attrs) {

    private val binding = ViewHeaderMenuButtonBinding
        .inflate(LayoutInflater.from(context), this)

    init {
        setInAnimation(context, R.anim.fade_in)
        setOutAnimation(context, R.anim.fade_out)
    }

    fun setImageUrl(imageUrl: String?, @DrawableRes fallback: Int? = null) {
        if (imageUrl.isNullOrEmpty()) return

        App.appComponent.imageLoader
            .load(url = imageUrl)
            .into(object : CustomViewTarget<ImageView, Bitmap>(binding.houseIcon) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if (fallback != null) {
                        this@HeaderMenuButtonView.visibility = View.VISIBLE
                        binding.houseIcon.setImageResource(fallback)
                    } else {
                        this@HeaderMenuButtonView.visibility = View.INVISIBLE
                    }
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.houseIcon.setImageBitmap(resource)
                    this@HeaderMenuButtonView.visibility = View.VISIBLE
                }
            })
    }

    fun showBadge() {
        binding.indicator.setImageResource(R.drawable.ic_red_dot)
    }

    fun clearBadge() {
        binding.indicator.setImageResource(R.drawable.ic_chevron_down_white)
    }
}