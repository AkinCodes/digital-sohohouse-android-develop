package com.sohohouse.seven.housenotes.detail.sitecore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.utils.AppBarStateChangeListener
import com.sohohouse.seven.common.utils.AppBarStateChangeListener.State.*
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityHouseNoteDetailsBinding
import com.sohohouse.seven.databinding.HouseNoteDetailHeaderImageNoparentBinding
import com.sohohouse.seven.databinding.HouseNoteDetailHeaderVideoNoparentBinding
import com.sohohouse.seven.databinding.ItemHouseNoteDetailFullbleedVimeoBlockNoparentBinding
import com.sohohouse.seven.video.VideoActivity
import timber.log.Timber

class HouseNoteDetailsActivity
    : BaseMVVMActivity<HouseNoteDetailsViewModel>(), ErrorViewStateViewController, Loadable.View {

    companion object {
        fun startHouseNoteDetailActivity(context: Context, noteID: String) {
            context.startActivity(startIntent(context, noteID))
        }

        fun startIntent(context: Context, id: String): Intent {
            return Intent(context, HouseNoteDetailsActivity::class.java).apply {
                this.putExtra(HOUSE_NOTE_ID_KEY, id)
            }
        }

        const val HOUSE_NOTE_ID_KEY = "HOUSE_NOTE_ID_KEY"
    }

    val binding by viewBinding(ActivityHouseNoteDetailsBinding::bind)

    override val viewModelClass = HouseNoteDetailsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.houseNoteDetailsLoadingview

    override fun getContentLayout() = R.layout.activity_house_note_details

    private val adapter = HouseNoteDetailsAdapter { onBackPressed() }

    private var exitFullScreenListener: YoutubeVideoBlockViewHolder.ExitFullScreenListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()
        setUpRv()
        observeErrorViewEvents()
        observeLoadingState(this)
        observeAdapterItems()
        observeHeader()
        initViewModel()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.houseNoteDetailsToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setUpCollapsingToolbarTransition() {
        with(binding) {
            houseNoteDetailsToolbar.setBackgroundColor(getColor(R.color.transparent))
            houseNoteAppbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                    when (state) {
                        IDLE, EXPANDED -> houseNoteDetailsToolbar.setBackgroundColor(getColor(R.color.transparent))
                        COLLAPSED -> houseNoteDetailsToolbar.setBackgroundColor(getColor(R.color.cod_gray))
                        null -> Timber.d("appbar state null")
                    }
                }
            })
        }
    }

    private fun observeHeader() {
        viewModel.header.observe(this) {
            setUpCollapsingToolbarTransition()
            with(binding) {
                when (it) {
                    is HouseNoteDetailVimeoVideoBlockItem -> {
                        VimeoVideoViewHolder(
                            ItemHouseNoteDetailFullbleedVimeoBlockNoparentBinding.inflate(
                                LayoutInflater.from(this@HouseNoteDetailsActivity),
                                houseNoteCtl
                            )
                        ).bind(it)
                    }
                    is HouseNoteDetailYoutubeVideoBlockItem -> {
                        YoutubeVideoBlockViewHolder(
                            HouseNoteDetailHeaderVideoNoparentBinding.inflate(
                                LayoutInflater.from(this@HouseNoteDetailsActivity),
                                houseNoteCtl,
                            )
                        ).bind(it)
                        houseNoteDetailsToolbar.bringToFront()
                    }
                    is HouseNoteDetailHeaderImageItem -> HeaderImageViewHolder(
                        HouseNoteDetailHeaderImageNoparentBinding.inflate(
                            layoutInflater,
                            houseNoteDetailsHeaderContainer,
                        ),
                        merge = true
                    ).apply {
                        bind(it)
                    }
                    else -> {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("Unknown HouseNoteDetailItem"))
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.init(intent.getStringExtra(HOUSE_NOTE_ID_KEY) ?: "")
    }

    private fun setUpRv() {
        with(binding.houseNoteDetailsRv) {
            this.adapter = this@HouseNoteDetailsActivity.adapter
            layoutManager = LinearLayoutManager(this@HouseNoteDetailsActivity)
        }
    }

    private fun observeAdapterItems() {
        viewModel.items.observe(this) {
            adapter.submitList(it)
        }
    }

    fun startFullScreenVideo(
        videoID: String,
        thumbnail: String?,
        startTime: Float,
        exitFullScreenListener: YoutubeVideoBlockViewHolder.ExitFullScreenListener
    ) {
        this.exitFullScreenListener = exitFullScreenListener

        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra(VideoActivity.VIDEO_ID_KEY, videoID)
        intent.putExtra(VideoActivity.START_TIME_KEY, startTime)
        if (thumbnail != "") {
            intent.putExtra(VideoActivity.THUMBNAIL_ID_KEY, thumbnail)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivityForResult(intent, VideoActivity.FULL_SCREEN_REQUEST_ID)
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.houseNoteDetailsErrorview
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VideoActivity.FULL_SCREEN_REQUEST_ID && resultCode == Activity.RESULT_OK && data != null) {
            val time = data.getFloatExtra(VideoActivity.START_TIME_KEY, 0F)
            exitFullScreenListener?.onExitFullScreen(time)
            exitFullScreenListener = null
        }
    }


}