package com.sohohouse.seven.perks.landing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.book.adapter.renderer.FilterStateRenderer
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityPerksLandingBinding
import com.sohohouse.seven.perks.details.PerksDetailActivity
import com.sohohouse.seven.perks.filter.PerksFilterActivity
import com.sohohouse.seven.perks.landing.adapter.PerksErrorItemRenderer
import com.sohohouse.seven.perks.landing.adapter.PerksItemRenderer
import com.sohohouse.seven.perks.landing.adapter.PerksLandingAdapter
import javax.inject.Inject

class PerksLandingActivity : BaseViewControllerActivity<PerksLandingPresenter>(),
    PerksLandingViewController,
    Injectable {

    val binding by viewBinding(ActivityPerksLandingBinding::bind)

    private val adapter: PerksLandingAdapter = PerksLandingAdapter(::onLastAllEventItem).apply {
        registerRenderers(
            PerksItemRenderer(::onItemClicked),
            PerksErrorItemRenderer(),
            FilterStateRenderer()
        )
    }

    companion object {
        private const val FILTER_REQUEST_CODE = 12345

        fun start(context: Context) {
            val starter = Intent(context, PerksLandingActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Inject
    lateinit var themeManager: ThemeManager

    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }

    override fun createPresenter(): PerksLandingPresenter {
        return App.appComponent.perksPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_perks_landing

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding.perksToolbar) {
            toolbarTitle.text = getString(R.string.perks_title)
            toolbarBackBtn.clicks { onBackPressed() }
            filter.setVisible()
            filter.clicks { onFilterButtonClicked() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            presenter.onRegionPreferencesUpdated()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showErrorState() {
        binding.perksLandingRv.setGone()
    }

    override fun onDataReady(data: MutableList<DiffItem>) {
        with(binding) {
            errorState.setGone()
            perksLandingRv.apply {
                setVisible()
                this.adapter = this@PerksLandingActivity.adapter
            }
        }
        adapter.setItems(data)
    }

    override fun startFilterActivity() {
        val intent = Intent(this, PerksFilterActivity::class.java)
        startActivityForResult(intent, FILTER_REQUEST_CODE)
    }

    override fun addToEndOfAdapter(value: MutableList<DiffItem>) {
        binding.perksLandingRv.post { adapter.addAllEvents(value) }
    }

    override val loadingView: LoadingView
        get() = binding.activityPerksLandingLoadingView

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState.apply {
            setImageResource(R.drawable.icon_no_network_detected)
        }
    }

    override fun loadStarted() {
        binding.perksLandingRv.post { adapter.loadMore() }
    }

    override fun loadFinished() {
        binding.perksLandingRv.post { adapter.loadFinished() }
    }

    private fun onItemClicked(eventId: String, sharedImageView: ImageView) {
        PerksDetailActivity.start(this, eventId)
    }

    private fun onLastAllEventItem() {
        presenter.fetchPerksOfNextPage()
    }

    private fun onFilterButtonClicked() {
        presenter.onFilterClicked()
    }

}