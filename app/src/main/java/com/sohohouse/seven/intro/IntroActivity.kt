package com.sohohouse.seven.intro

import android.os.Bundle
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.view.View.MeasureSpec
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.setInvisible
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.startActivityAndFinish
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityIntroBinding
import com.sohohouse.seven.intro.adapter.IntroAdapter
import com.sohohouse.seven.intro.adapter.IntroGuide
import com.sohohouse.seven.intro.adapter.IntroItem
import kotlin.math.min

class IntroActivity : BaseMVVMActivity<IntroViewModel>(), Loadable.View, Errorable.View {

    override lateinit var loadingView: LoadingView

    override lateinit var errorStateView: ReloadableErrorStateView

    override val viewModelClass = IntroViewModel::class.java

    override fun getContentLayout() = R.layout.activity_intro

    private val binding by viewBinding(ActivityIntroBinding::bind)

    init {
        lifecycleScope.launchWhenStarted { viewModel.getUserProfile() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding) {
            loadingView = activityIntroLoadingView
            errorStateView = errorView
            IntroAdapter().let {
                setupViews(it)
                binding.setupViewModel(it)
            }
        }
        viewModel.setScreenName(name= AnalyticsManager.Screens.Intro.name)
    }

    private fun ActivityIntroBinding.setupViews(adapter: IntroAdapter) {
        with(viewPager) {
            this.adapter = adapter
            setPageTransformer(PageTransformer())

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                private var lastPosition: Int = -1

                override fun onPageSelected(position: Int) {
                    if (position < adapter.itemCount - 1) {
                        button.setText(R.string.intro_next_cta)
                    } else {
                        button.setText(R.string.intro_let_me_in_cta)
                    }
                    pageIndicator.position = position

                    viewModel.logTimeSpent(lastPosition)
                    lastPosition = position
                }
            })

            button.setOnClickListener {
                if (currentItem == adapter.itemCount - 1) {
                    viewModel.logTimeSpent(currentItem, true)
                    viewModel.onCompleteIntro(this@IntroActivity)
                } else {
                    currentItem = min(adapter.itemCount - 1, currentItem + 1)
                }
            }
        }

        with(errorView) {
            reloadClicks {
                setInvisible()
                button.setVisible()
                viewModel.onReload(this@IntroActivity)
            }
        }

    }

    private fun ActivityIntroBinding.setupViewModel(adapter: IntroAdapter) {
        viewModel.items.observe(this@IntroActivity) { items ->
            pageIndicator.pageCount = items.size
            pageIndicator.setVisible(items.size > 1)
            button.setVisible()

            if (viewPager.measuredWidth == 0) {
                viewPager.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                viewPager.post { setItems(adapter, items) }
            } else {
                setItems(adapter, items)
            }
        }
        viewModel.intent.observe(this@IntroActivity) { startActivityAndFinish(it) }
        viewModel.error.observe(this@IntroActivity) {
            errorView.setVisible()
            button.setInvisible()
        }
        observeLoadingState(this@IntroActivity)
    }

    private fun setItems(adapter: IntroAdapter, items: List<IntroItem>) {
        adapter.lineCount = measureLines(items)
        adapter.items = items
    }

    private fun measureLines(items: List<IntroItem>): Int {
        val textSize = resources.getDimensionPixelSize(R.dimen.sp_16)
        val width =
            binding.viewPager.measuredWidth - resources.getDimensionPixelSize(R.dimen.dp_16) * 2

        val paint = TextPaint()
        paint.typeface = ResourcesCompat.getFont(this, R.font.faro_lucky_regular)
        paint.isAntiAlias = true
        paint.textSize = textSize.toFloat()

        return items.filterIsInstance<IntroGuide>().map { item ->
            val text = getString(item.description)
            if (!TextUtils.isEmpty(text)) {
                StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build().lineCount
            } else 0
        }.maxOrNull() ?: 0
    }

}