package com.sohohouse.seven.common.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class ImageRandomizer {

    companion object {
        val IMAGE_CHANGE_INTERVAL = TimeUnit.SECONDS.toMillis(2)
        val IMAGE_FADE_ANIM_DURATION = TimeUnit.MILLISECONDS.toMillis(200)
    }

    private var imageViewRef: WeakReference<ImageView>? = null
    private var imageResIds: MutableList<Int> = mutableListOf()
    private var stop = true
    private var currentImageIndex: Int = 0
    private val timerRunnable = TimerRunnable()

    fun attach(imageView: ImageView) {
        imageViewRef = WeakReference(imageView)
    }

    fun setImageResIds(urls: MutableList<Int>) {
        imageResIds = urls
    }

    fun start() {
        if (stop) {
            stop = false
            beginImageRandomization()
        }
    }

    private fun beginImageRandomization() {
        imageResIds.shuffle()
        timerRunnable.run()
    }

    inner class TimerRunnable : Runnable {
        override fun run() {
            if (!stop) {
                val nextImageIndex = getNextImageIndex()
                val nextImageRes = imageResIds[nextImageIndex]
                imageViewRef?.get()?.let { imageView ->
                    val oldImage = imageView.drawable
                    val newImage = ContextCompat.getDrawable(imageView.context, nextImageRes)

                    changeImage(imageView, oldImage, newImage)
                    imageView.postDelayed(this, IMAGE_CHANGE_INTERVAL)
                }
            }
        }
    }

    private fun getNextImageIndex(): Int {
        if (currentImageIndex < imageResIds.lastIndex) {
            currentImageIndex++
        } else {
            currentImageIndex = 0
        }
        return currentImageIndex
    }

    fun stop() {
        stop = true
    }

    fun changeImage(imageView: ImageView, oldImage: Drawable?, newImage: Drawable?) {
        if (oldImage == null) {
            imageView.setImageDrawable(newImage)
        } else {
            val td = TransitionDrawable(
                arrayOf(
                    oldImage,
                    newImage
                )
            )
            imageView.setImageDrawable(td)
            td.startTransition(IMAGE_FADE_ANIM_DURATION.toInt())
        }
    }

    fun onDetached() {
        stop()
        imageViewRef?.get()?.removeCallbacks(timerRunnable)
    }

}