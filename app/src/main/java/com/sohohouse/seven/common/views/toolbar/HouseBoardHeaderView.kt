package com.sohohouse.seven.common.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ViewHouseBoardHeaderTitlesBinding

class HouseBoardHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    private val binding = ViewHouseBoardHeaderTitlesBinding
        .inflate(LayoutInflater.from(context), this, true)

    private val rainbowGradientArray: IntArray = intArrayOf(
        Color.parseColor("#99de1717"),
        Color.parseColor("#99f6900d"),
        Color.parseColor("#99f9dc0d"),
        Color.parseColor("#994b9f4f"),
        Color.parseColor("#99226abc"),
        Color.parseColor("#99a534d9"),
        Color.parseColor("#99a534d9"),
        Color.parseColor("#99fe0606"),
        Color.parseColor("#99f9900a"),
        Color.parseColor("#99f9dc0d"),
        Color.parseColor("#994ba0b3")
    )

    private val prideEmoji = String(Character.toChars(0x1f3f3)) +
            String(Character.toChars(0xfe0f)) +
            String(Character.toChars(0x200d)) +
            String(Character.toChars(0x1f308))

    private val prideStyling: Boolean

    private val bannerAdapter = BannerAdapter()

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HouseBoardHeaderView)
        prideStyling = ta.getBoolean(R.styleable.HouseBoardHeaderView_prideStyling, false)
        ta.recycle()

        binding.banners.layoutManager = LinearLayoutManager(context)
        binding.banners.adapter = bannerAdapter
    }

    fun setTitle(title: String) = with(binding) {
        if (prideStyling) {
            val titleWithoutCommas = title.replace(",", "")
            titleShadow.text = titleWithoutCommas
            titleView.text = titleWithoutCommas
            addPrideColoring(titleView)
            addPrideEmoji(emojiView)
        } else {
            titleView.text = title
        }
    }

    fun setSubtitle(subtitle: String) {
        binding.subtitleView.text = subtitle
    }

    fun setImage(url: String?, @DrawableRes fallback: Int? = null) {
        binding.headerMenu.setImageUrl(url, fallback)
    }

    fun setEmoji(emoji: String?) {
        binding.emojiView.text = formatEmoji(emoji)
    }

    fun toggleMenu() {
        binding.headerMenu.showNext()
    }

    fun showCloseButton() {
        if (binding.headerMenu.nextView.id == R.id.close_button) {
            binding.headerMenu.showNext()
        }
        enableScrollFlags(true)
    }

    fun showOpenButton() {
        if (binding.headerMenu.nextView.id == R.id.open_button) {
            binding.headerMenu.showNext()
        }
        enableScrollFlags(false)
    }

    fun addBanner(banner: Banner) {
        if (!bannerAdapter.currentItems.contains(banner)) {
            bannerAdapter.add(banner)
        }
    }

    private fun enableScrollFlags(enable: Boolean) {
        val lp = layoutParams
        if (lp !is AppBarLayout.LayoutParams) return

        lp.scrollFlags = if (enable) {
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        } else {
            AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        }
        layoutParams = lp
    }

    fun showBadge(show: Boolean) {
        if (show) {
            binding.headerMenu.showBadge()
        } else {
            binding.headerMenu.clearBadge()
        }
    }

    private fun addPrideColoring(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        paint.shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            rainbowGradientArray,
            null,
            Shader.TileMode.CLAMP
        )
    }

    @SuppressLint("SetTextI18n")
    private fun addPrideEmoji(textView: TextView) {
        textView.text = formatEmoji(prideEmoji)
    }

    private fun formatEmoji(emoji: String?): String {
        if (emoji.isNullOrEmpty()) return ""
        return emoji.trim().let { emoji -> " $emoji," }
    }

    fun getHeaderContentView() = binding.headerContentView

}