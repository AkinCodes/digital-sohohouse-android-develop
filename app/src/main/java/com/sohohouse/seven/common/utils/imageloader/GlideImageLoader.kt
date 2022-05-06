package com.sohohouse.seven.common.utils.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sohohouse.seven.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GlideImageLoader @Inject constructor(private val context: Context) : ImageLoader() {

    override fun into(request: ImageLoaderRequest, imageView: ImageView, callback: Callback?) {

        if (imageView.measuredHeight == 0 && imageView.measuredWidth == 0) {
            imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            imageView.post { load(request, imageView, callback) }
        } else {
            load(request, imageView, callback)
        }
    }

    override fun into(request: ImageLoaderRequest, target: Target<Bitmap>) {
        val options = createRequestOptions(request)

        Glide.with(context)
            .asBitmap()
            .load(request.url)
            .apply(options)
            .into(target)
    }

    private fun load(request: ImageLoaderRequest, imageView: ImageView, callback: Callback?) {
        val options = createRequestOptions(request).run {
            this.override(imageView.measuredWidth, imageView.measuredHeight)
        }

        Glide.with(context).clear(imageView)
        Glide.with(context)
            .load(request.url)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e(e?.message ?: "")
                    callback?.onError()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (request.isFade) {
                        val animation =
                            AnimationUtils.loadAnimation(context, R.anim.fade_in_long_anim_time)
                        imageView.startAnimation(animation)
                    }
                    callback?.onSuccess()
                    return false
                }
            })
            .into(imageView)
    }

    private fun createRequestOptions(request: ImageLoaderRequest): RequestOptions {
        var options = if (request.placeholder != 0) {
            RequestOptions.placeholderOf(request.placeholder).error(request.placeholder)
        } else {
            RequestOptions().error(ColorDrawable(Color.TRANSPARENT))
        }

        if (request.isCenterCrop) {
            options = options.fitCenter().optionalCenterCrop()
        } else {
            options = options.fitCenter()
        }

        if (request.isRound) {
            options = options.optionalCircleCrop()
        }
        return options
    }
}