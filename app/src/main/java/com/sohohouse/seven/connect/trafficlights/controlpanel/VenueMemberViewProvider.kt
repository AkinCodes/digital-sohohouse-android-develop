package com.sohohouse.seven.connect.trafficlights.controlpanel

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.fastBlur
import com.sohohouse.seven.common.views.AsyncImageView

interface VenueMemberViewProvider {

    fun inflate(parent: ViewGroup): View

    class Blurred(
        private val imageUrl: String
    ) : VenueMemberViewProvider {
        override fun inflate(parent: ViewGroup): View = AsyncImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
            )
            circleCrop = true
            setImageUrl(imageUrl, R.drawable.ic_member_placeholder_light) { it.fastBlur(0.1F, 20) }
            parent.addView(this)
        }

    }

    class ShowMore(val more: Int, val layoutRes: Int = R.layout.text_view_body_14) :
        VenueMemberViewProvider {
        override fun inflate(parent: ViewGroup): View = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, true)
            .also {
                it.findViewById<TextView>(R.id.body14)?.text = parent.context.getString(R.string.plus_more, more.toString())
            }
    }

    class NotBlurred(
        private val imageUrl: String = "",
        private val imageRes: Int = R.drawable.ic_member_placeholder_light
    ) : VenueMemberViewProvider {
        override fun inflate(parent: ViewGroup): View = AsyncImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
            )
            circleCrop = true
            if (imageUrl.isNotBlank())
                setImageUrl(imageUrl, imageRes)
            else
                setImageResource(imageRes)
            parent.addView(this)
        }

    }

    class PlaceHolder : VenueMemberViewProvider {
        override fun inflate(parent: ViewGroup): View = ImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
                context.resources.getDimensionPixelOffset(R.dimen.dp_40),
            )

            setBackgroundResource(R.drawable.bkg_oval)
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(parent.context, R.color.white)
            )

            parent.addView(this)
        }

    }

}
