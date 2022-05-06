package com.sohohouse.seven.browsehouses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.browsehouses.recycler.ZoomItemLayoutManager
import com.sohohouse.seven.browsehouses.recycler.snaphelper.OnSnapPositionChangeListener
import com.sohohouse.seven.browsehouses.recycler.snaphelper.SnapOnScrollListener
import com.sohohouse.seven.browsehouses.recycler.snaphelper.StartSnapHelper
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.KickoutType.HOUSES
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.ActivityBrowseAllHouseBinding
import com.sohohouse.seven.home.browsehouses.BrowseHousesViewSizeListener
import com.sohohouse.seven.network.core.models.Venue


class BrowseAllHouseActivity : BaseViewControllerActivity<BrowseAllHousePresenter>(),
    BrowseAllHouseViewController, OnSnapPositionChangeListener, BrowseHousesViewSizeListener {

    private val binding by viewBinding(ActivityBrowseAllHouseBinding::bind)

    private lateinit var localLayoutManager: ZoomItemLayoutManager

    private var snappedPosition: Int = -1

    private val topPadding by lazy { resources.getDimension(R.dimen.browse_house_horizontal_margin) }

    override fun createPresenter(): BrowseAllHousePresenter {
        return App.appComponent.browseAllHousesPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_browse_all_house

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.browseHousesBackButton.clicks { onBackPressed() }
        binding.setUpRv()
    }

    override fun onSnapScrollPositionChange(position: Int) {
        if (position != RecyclerView.NO_POSITION && binding.recyclerView.adapter != null) {
            val adapter = binding.recyclerView.adapter as BrowseAllHouseAdapter

            if (snappedPosition != position && snappedPosition != RecyclerView.NO_POSITION) {
                adapter.iconVisibility(snappedPosition, false)
            }
        }
    }

    override fun onSnapIdlePositionChange(position: Int) = with(binding) {

        if (recyclerView.adapter != null) {

            val adapter = recyclerView.adapter as BrowseAllHouseAdapter

            if (position != RecyclerView.NO_POSITION) {
                adapter.getBackgroundForPosition(position)
                adapter.iconVisibility(position, true)
                snappedPosition = position
                return
            }

            adapter.iconVisibility(position, false)
        }
    }

    override fun setBackgroundImage(imageUrl: String?) {
        binding.browseHousesFadingBackground.setBackgroundImage(imageUrl)
    }

    override fun onHomeClicked(venue: Venue, position: Int) {
        Handler().postDelayed({
            localLayoutManager.scrollToPositionWithOffset(
                position,
                resources.getDimensionPixelOffset(R.dimen.xlarge)
            )
        }, 0)
        onSnapIdlePositionChange(position)

        if (venue.slug.isNotEmpty()) {
            WebViewBottomSheetFragment.withKickoutType(type = HOUSES, id = venue.slug)
                .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
        }
    }

    override fun onDataReady(
        dataItems: List<BaseAdapterItem.BrowseHousesItem>,
        selectedPosition: Int,
    ) {
        (binding.recyclerView.adapter as BrowseAllHouseAdapter).setItems(dataItems)
        scrollToPosition(selectedPosition)
    }

    //triggers the snap to position
    private fun scrollToPosition(selectedPosition: Int) = with(binding.recyclerView) {
        scrollToPosition(selectedPosition)
        smoothScrollBy(0, -1)
    }

    private fun ActivityBrowseAllHouseBinding.setUpRv() {
        recyclerView.apply {

            localLayoutManager = ZoomItemLayoutManager(context, topPadding) {
                recyclerView.setPadding(
                    recyclerView.paddingLeft, 0, 0, recyclerView.height - it
                )
            }
            layoutManager = localLayoutManager

            adapter = BrowseAllHouseAdapter(this@BrowseAllHouseActivity)

            val startSnapHelper = StartSnapHelper(topPadding)
            startSnapHelper.attachToRecyclerView(this)

            val snapOnScrollListener =
                SnapOnScrollListener(startSnapHelper, this@BrowseAllHouseActivity)
            addOnScrollListener(snapOnScrollListener)
        }
    }

    override fun onDestroy() {
        binding.recyclerView.adapter = null
        super.onDestroy()
    }

    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override val loadingView: LoadingView
        get() = binding.activityBrowseAllHouseLoadingView

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BrowseAllHouseActivity::class.java)
            context.startActivity(intent)
        }
    }
}
