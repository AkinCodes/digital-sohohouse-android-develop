package com.sohohouse.seven.common.extensions

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.imageloader.ImageLoader

fun ImageView.setImageFromUrl(
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    isRound: Boolean = false,
    isCenterCrop: Boolean = true,
    isFade: Boolean = true,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    if (url?.isNotEmpty() == true) {
        App.appComponent.imageLoader.load(
            url, isRound, isCenterCrop,
            isFade = if (hasImage) false else isFade
        )
            .apply { this.placeholder = placeholder }
            .into(this, object : ImageLoader.Callback {
                override fun onSuccess() {
                    onSuccess?.invoke()
                }

                override fun onError() {
                    onError?.invoke()
                }
            })
    } else {
        this.setImageResource(placeholder)
    }
}

val ImageView.hasImage: Boolean
    get() {
        val drawable = drawable
        var hasImage = drawable != null

        if (hasImage && drawable is BitmapDrawable) {
            hasImage = drawable.bitmap != null
        }

        return hasImage
    }

fun ImageView.setImageResourceNotNull(@DrawableRes res: Int?) {
    if (res != null && res != 0) {
        setImageResource(res)
    }
}

fun ImageView.setImageWithPlaceHolder(
    imageUrl: String,
    @DrawableRes holder: Int,
    onComplete: () -> Unit = {}
) {
    Glide.with(context)
        .load(imageUrl)
        .placeholder(holder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onComplete.invoke()
                return false
            }

        })
        .into(this)
}

