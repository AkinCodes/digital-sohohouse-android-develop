package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.sohohouse.seven.R
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.databinding.ReloadableErrorStateLayoutBinding
import javax.inject.Inject

class ReloadableErrorStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs, R.attr.reloadableErrorStateStyle) {

    private val binding = ReloadableErrorStateLayoutBinding
        .inflate(LayoutInflater.from(context), this)

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    init {
        appComponent.inject(this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ReloadableErrorStateView)
        val drawable = ta.getResourceId(
            R.styleable.ReloadableErrorStateView_android_src,
            R.drawable.icon_no_network_detected
        )
        val title = ta.getString(R.styleable.ReloadableErrorStateView_title)
            ?: context.getString(R.string.server_error_header)
        val subtitle = ta.getString(R.styleable.ReloadableErrorStateView_subtitle)
            ?: context.getString(R.string.server_error_supporting)
        val buttonText = ta.getString(R.styleable.ReloadableErrorStateView_buttonText)
            ?: context.getString(R.string.server_error_reload_cta)
        ta.recycle()

        isVisible = isInEditMode

        setImageResource(drawable)
        setTitle(title)
        setSubtitle(subtitle)
        setButtonText(buttonText)
    }

    fun minimizeView() = with(binding) {
        wrenchImage.setGone()
        reloadButton.setGone()
    }

    fun reloadClicks(onNext: (Any) -> Unit) {
        analyticsManager.logEventAction(AnalyticsManager.Action.DidTapTryAgainCTAOffline)
        binding.reloadButton.clicks(onNext)
    }

    fun setImageResource(resId: Int) {
        binding.wrenchImage.setImageResource(resId)
    }

    fun setTitle(text: String?) {
        binding.title.text = text
    }

    fun setSubtitle(text: String?) {
        binding.supporting.text = text
    }

    fun setButtonText(text: String?) {
        binding.reloadButton.text = text
    }

    fun getReloadButton() = binding.reloadButton

}