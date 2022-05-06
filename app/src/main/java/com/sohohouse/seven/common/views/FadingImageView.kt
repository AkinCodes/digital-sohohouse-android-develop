package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.sohohouse.seven.App
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.FadingImageviewLayoutBinding

class FadingImageView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var previousBackgroundImageUrl: String? = null

    private val binding = FadingImageviewLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    fun setBackgroundImage(imageUrl: String?) = with(binding) {
        if (!imageUrl.isNullOrBlank()) {
            App.appComponent.imageLoader.load(imageUrl)
                .into(object : CustomViewTarget<ImageView, Bitmap>(fadingBackgroundImage) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        setPreviousBackground()
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        fadingBackgroundImage.setImageBitmap(resource)
                        setPreviousBackground()
                    }

                    private fun setPreviousBackground() {
                        backgroundImage.setImageFromUrl(previousBackgroundImageUrl, isFade = false)
                        previousBackgroundImageUrl = imageUrl
                    }
                })
        } else {
            backgroundImage.setImageFromUrl("")
            fadingBackgroundImage.setImageFromUrl("")
            previousBackgroundImageUrl = ""
        }
    }

}