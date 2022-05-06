package com.sohohouse.seven.housenotes.detail.sitecore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ItemHouseNoteDetailYoutubeVideoblockContentBinding
import com.sohohouse.seven.video.YouTubePlayerHelper
import timber.log.Timber

class YoutubeVideoBlockViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var innerBinding = ItemHouseNoteDetailYoutubeVideoblockContentBinding.bind(binding.root)

    private var fullScreenListener: YouTubePlayerFullScreenListener? = null
    private var tracker: YouTubePlayerTracker = YouTubePlayerTracker()
    val pauseListener: (() -> Unit) = {
        if (::myPlayer.isInitialized) {
            myPlayer.pause()
        }
        if (autoStart) {
            with(innerBinding) {
                thumbnailConstraint.setGone()
                clickableLayout.setGone()
                spinner.setGone()
                playerView.setVisible()
            }
        }
        autoStart = false
    }
    private var autoStart = false

    lateinit var myPlayer: YouTubePlayer
    lateinit var defaultPlayerUiController: DefaultPlayerUiController

    init {
        innerBinding.playerView.enableAutomaticInitialization = false
    }

    interface ExitFullScreenListener {
        fun onExitFullScreen(videoTime: Float)
    }

    private fun doInitializePlayer(item: HouseNoteDetailYoutubeVideoBlockItem) =
        with(innerBinding) {
            val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()

            playerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    onPlayerReady(item, youTubePlayer)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    when (state) {
                        PlayerConstants.PlayerState.ENDED -> {
                            item.videoTime = item.startTime
                            if (item.isSoundlessLoop) {
                                youTubePlayer.seekTo(item.startTime.toFloat())
                                youTubePlayer.play()
                            } else {
                                thumbnailConstraint.visibility = View.VISIBLE
                                clickableLayout.visibility = View.VISIBLE
                                item.isStarted = false
                            }
                            autoStart = false
                        }
                        PlayerConstants.PlayerState.VIDEO_CUED -> {
                            onVideoCued(item, youTubePlayer)
                        }
                        PlayerConstants.PlayerState.PLAYING -> {
                            youTubePlayer.setVolume(if (item.isSoundlessLoop) 0 else 100) // if ad played before

                            spinner.setGone()
                            thumbnailConstraint.setGone()

                            item.isStarted = true
                        }
                        else -> {
                            item.videoTime = tracker.currentSecond.toInt()
                        }
                    }
                }
            }, options)
        }

    private fun onVideoCued(
        item: HouseNoteDetailYoutubeVideoBlockItem,
        youTubePlayer: YouTubePlayer
    ) {
        if (autoStart || (item.isSoundlessLoop && !item.isStarted)) youTubePlayer.play()
        innerBinding.playerView.setVisible()
        defaultPlayerUiController.showPlayPauseButton(true)
    }

    private fun onPlayerReady(
        item: HouseNoteDetailYoutubeVideoBlockItem,
        youTubePlayer: YouTubePlayer
    ) {
        setupTracker(item, youTubePlayer)
        youTubePlayer.setVolume(0)

        if (item.showControls) {
            makeFullScreenListener(item, youTubePlayer)
        }
        myPlayer = youTubePlayer

        defaultPlayerUiController = DefaultPlayerUiController(innerBinding.playerView, myPlayer)
        innerBinding.playerView.setCustomPlayerUi(defaultPlayerUiController.rootView)

        setupController(item)
        youTubePlayer.cueVideo(item.youtubeVideoId, item.videoTime.toFloat())
    }

    private fun setupController(item: HouseNoteDetailYoutubeVideoBlockItem) {
        with(defaultPlayerUiController) {
            showVideoTitle(item.showControls)
            showPlayPauseButton(item.isStarted) //hide pause btn until video is playing
            showMenuButton(false)
            showCurrentTime(item.showControls)
            showDuration(item.showControls)
            showSeekBar(item.showControls)
            showBufferingProgress(item.showControls)
            showYouTubeButton(item.showControls)
            showFullscreenButton(item.showControls)
        }
    }

    fun bind(item: HouseNoteDetailYoutubeVideoBlockItem, back: (() -> Unit)? = null) =
        with(innerBinding) {
            val lifecycle = (itemView.context as? BaseActivity)?.lifecycle
            lifecycle?.addObserver(playerView)

            when {
                item.isSoundlessLoop || item.isStarted -> {
                    thumbnailConstraint.setGone()
                    clickableLayout.setGone()
                    spinner.setGone()
                    playerView.setVisible()

                    initializePlayerView(item)
                }
                else -> {
                    if (item.thumbnail.isNullOrBlank()) {
                        item.thumbnail =
                            YouTubePlayerHelper.YOUTUBE_THUMBNAIL_URL.replaceBraces(item.youtubeVideoId)
                    }
                    thumbnail.setImageFromUrl(item.thumbnail, android.R.color.black)
                    thumbnailConstraint.setVisible()
                    clickableLayout.setVisible()
                    spinner.setGone()
                }
            }

            clickableLayout.clicks {
                onClickClickableLayout(item)
            }

            with(backButton) {
                if (back != null) {
                    setVisible()
                    clicks { back() }
                } else setGone()
            }

        }

    private fun onClickClickableLayout(item: HouseNoteDetailYoutubeVideoBlockItem) =
        with(innerBinding) {
            if (clickableLayout.visibility == View.VISIBLE) {
                clickableLayout.setGone()
                spinner.setVisible()

                initializePlayerView(item)
                autoStart = true
            }
        }

    private fun initializePlayerView(item: HouseNoteDetailYoutubeVideoBlockItem) {
        if (::myPlayer.isInitialized) {
            cueVideo(item)
        } else {
            try {
                doInitializePlayer(item)
            } catch (e: IllegalStateException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.e("Player already initialized")
            }
        }
    }

    private fun cueVideo(item: HouseNoteDetailYoutubeVideoBlockItem) {
        setupTracker(item, myPlayer)
        myPlayer.setVolume(0) // for when ads play

        if (item.showControls) {
            makeFullScreenListener(item, myPlayer)
        }
        myPlayer.cueVideo(item.youtubeVideoId, item.videoTime.toFloat())
    }

    private fun makeFullScreenListener(
        item: HouseNoteDetailYoutubeVideoBlockItem,
        player: YouTubePlayer
    ) = with(innerBinding.playerView) {
        fullScreenListener?.let { removeFullScreenListener(it) }
        fullScreenListener = object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerExitFullScreen() {
            }

            override fun onYouTubePlayerEnterFullScreen() {
                player.pause()
                (itemView.context as HouseNoteDetailsActivity).startFullScreenVideo(
                    item.youtubeVideoId, item.thumbnail, item.tracker.currentSecond,
                    object : ExitFullScreenListener {
                        override fun onExitFullScreen(videoTime: Float) {
                            player.seekTo(videoTime)
                            player.play()
                        }
                    })
                exitFullScreen()
            }
        }
        fullScreenListener?.let { addFullScreenListener(it) }
    }

    private fun setupTracker(item: HouseNoteDetailYoutubeVideoBlockItem, player: YouTubePlayer) {
        player.removeListener(tracker)
        tracker = item.tracker
        player.addListener(tracker)
    }
}