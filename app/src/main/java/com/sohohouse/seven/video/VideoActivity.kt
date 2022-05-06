package com.sohohouse.seven.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.databinding.VideoFullScreenLayoutBinding

class VideoActivity : BaseActivity() {

    private lateinit var player: YouTubePlayer
    private var tracker: YouTubePlayerTracker = YouTubePlayerTracker()

    private var videoID: String = ""
    private var currentTime: Float = 0F

    override fun getContentLayout(): Int = R.layout.video_full_screen_layout

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoFullScreenLayoutBinding.bind(findViewById(R.id.root)).setupViews()
    }

    private fun VideoFullScreenLayoutBinding.setupViews() {

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val orientationEventListener =
            object :
                OrientationEventListener(this@VideoActivity,
                    SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    if ((orientation in 60..120) || (orientation in 240..300)) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                        this.disable()
                    }
                }

            }
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        }

        videoID = intent.getStringExtra(VIDEO_ID_KEY) ?: ""
        currentTime = intent.getFloatExtra(START_TIME_KEY, 0F)

        val thumbnailUrl =
            intent.getStringExtra(THUMBNAIL_ID_KEY).takeIf { it.isNullOrEmpty().not() }
                ?: YouTubePlayerHelper.YOUTUBE_THUMBNAIL_URL.replaceBraces(videoID)

        App.appComponent.imageLoader.load(thumbnailUrl).apply {
            placeholder = android.R.color.black
        }.into(thumbnail)

        spinner.visibility = View.VISIBLE
        if (videoID.isNotEmpty()) {
            val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()
            playerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.setVolume(0)
                    player = youTubePlayer
                    player.addListener(tracker)
                    val defaultPlayerUiController =
                        DefaultPlayerUiController(playerView, player)
                    playerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                    youTubePlayer.loadVideo(videoID, currentTime)
                    clickableLayout.clicks {
                        clickableLayout.visibility = View.GONE
                        youTubePlayer.play()
                    }
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState,
                ) {
                    when (state) {
                        PlayerConstants.PlayerState.ENDED -> {
                            onBackPressed()
                        }
                        PlayerConstants.PlayerState.VIDEO_CUED -> {
                            spinner.visibility = View.GONE
                            clickableLayout.visibility = View.VISIBLE
                        }
                        PlayerConstants.PlayerState.PLAYING, PlayerConstants.PlayerState.PAUSED -> {
                            youTubePlayer.setVolume(100)
                            playerView.visibility = View.VISIBLE
                            currentTime = tracker.currentSecond
                            spinner.visibility = View.GONE
                            clickableLayout.visibility = View.GONE
                            thumbnailConstraint.visibility = View.GONE
                        }
                        else -> currentTime = tracker.currentSecond
                    }
                }
            }, true, options)
        }

        playerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerExitFullScreen() {
                onBackPressed()
            }

            override fun onYouTubePlayerEnterFullScreen() {
            }
        })
        playerView.enterFullScreen()
    }


    override fun onBackPressed() {
        player.pause()
        val resultIntent = Intent()
        resultIntent.action = videoID
        resultIntent.putExtra(START_TIME_KEY, currentTime)
        setResult(Activity.RESULT_OK, resultIntent)
        findViewById<YouTubePlayerView>(R.id.player_view)?.release()
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    companion object {
        const val FULL_SCREEN_REQUEST_ID = 99
        const val VIDEO_ID_KEY = "video_id_key"
        const val THUMBNAIL_ID_KEY = "thumbnail_id_key"
        const val START_TIME_KEY = "start_time_key"
    }
}
