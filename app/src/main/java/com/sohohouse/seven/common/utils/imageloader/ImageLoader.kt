package com.sohohouse.seven.common.utils.imageloader

import android.graphics.Bitmap
import android.util.Size
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.target.Target

@Deprecated("Use regular Glide instead")
abstract class ImageLoader {
    open fun load(
        url: String,
        isRound: Boolean = false,
        isCenterCrop: Boolean = true,
        isFade: Boolean = true,
        size: Size? = null
    ): ImageLoaderRequest {
        return ImageLoaderRequest(
            this,
            url,
            isRound = isRound,
            isCenterCrop = isCenterCrop,
            isFade = isFade,
            size = size
        )
    }

    abstract fun into(
        request: ImageLoaderRequest,
        imageView: ImageView,
        callback: Callback? = null
    )

    abstract fun into(request: ImageLoaderRequest, target: Target<Bitmap>)

    interface Callback {
        fun onSuccess()
        fun onError()
    }
}

open class ImageLoaderRequest(
    private val imageLoader: ImageLoader,
    var url: String,
    @DrawableRes var placeholder: Int = 0,
    val isRound: Boolean = false,
    val isCenterCrop: Boolean = true,
    val isFade: Boolean = true,
    val size: Size? = null
) {
    fun into(imageView: ImageView, callback: ImageLoader.Callback? = null) {
        imageLoader.into(this, imageView, callback)
    }

    fun into(target: Target<Bitmap>) {
        imageLoader.into(this, target)
    }
}