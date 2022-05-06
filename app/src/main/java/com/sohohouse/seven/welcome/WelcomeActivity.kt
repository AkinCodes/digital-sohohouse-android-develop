package com.sohohouse.seven.welcome

import android.os.Bundle
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.databinding.ActivityWelcomeBinding


class WelcomeActivity : BaseMVVMActivity<WelcomeViewModel>() {

    override val viewModelClass: Class<WelcomeViewModel> = WelcomeViewModel::class.java

    override fun getContentLayout() = R.layout.activity_welcome

    private lateinit var player: ExoPlayer

    private val binding by viewBinding(ActivityWelcomeBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupViews()
        setupViewModel()
        setupVideo()

        viewModel.setScreenName(name= AnalyticsManager.Screens.Welcome.name)
    }

    override fun setBrandingTheme() {
        setTheme(themeManager.baseTheme)
    }

    override fun onResume() {
        super.onResume()
        player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }

    private fun setupViews() {
        binding.signIn.setOnClickListener { viewModel.onClickSignIn(this) }
    }

    private fun setupViewModel() {
        viewModel.navigation.observe(this, Observer { intent -> startActivity(intent) })
    }

    private fun setupVideo() {
        player = ExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
        val uri = RawResourceDataSource.buildRawResourceUri(R.raw.welcome)
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .build()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "SohoHouse"))
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)
            .createMediaSource(mediaItem)

        player.setMediaSource(videoSource)
        player.prepare()
        binding.exoPlayerView.player = player
        player.playWhenReady = true
    }
}