package com.sohohouse.seven.connect.message.chat.fullscreen

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.os.bundleOf
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.FragmentMediaFullScreenBinding
import javax.inject.Inject

class MediaFullScreenFragment : BaseFragment(), Injectable {

    var binding: FragmentMediaFullScreenBinding? = null
    val listener = ViewTreeObserver.OnWindowFocusChangeListener {}

    private val imageUrl by lazy {
        arguments?.getString(IMAGE_URL)
    }
    private val videoUrl by lazy {
        arguments?.getString(VIDEO_URL)
    }

    @Inject
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory

    private val player by lazy {
        SimpleExoPlayer.Builder(requireContext()).build().apply {
            playWhenReady = true
            addListener(object : Player.EventListener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> showLoader()
                        else -> showLoader(false)
                    }
                }
            })
        }
    }

    private fun showLoader(show: Boolean = true) = binding?.loader?.setVisible(show)

    override val contentLayoutId: Int
        get() {
            return R.layout.fragment_media_full_screen
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMediaFullScreenBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        requireActivity().enterToImmersiveMode(listener)

        when {
            imageUrl != null -> showImage()
            videoUrl != null -> playVideo(videoUrl!!)
            else -> dismiss()
        }
        binding?.close?.setOnClickListener {
            dismiss()
        }
    }

    private fun playVideo(videoUrl: String) {

        val mediaSource =
            DefaultMediaSourceFactory(cacheDataSourceFactory, DefaultExtractorsFactory())
                .createMediaSource(MediaItem.fromUri(videoUrl))

        player.setMediaSource(mediaSource)
        player.prepare()
        binding?.apply {
            videoView.player?.release()
            videoView.setVisible()
            videoView.player = player
        }
    }

    private fun showImage() {
        binding?.image?.setVisible()
        binding?.image?.setImageFromUrl(imageUrl, R.drawable.placeholder, false, false,
            onSuccess = {
                showLoader(false)
            },
            onError = {
                showLoader(false)
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding?.videoView?.player = null
        binding = null
        player.stop(true)
        player.release()
        requireActivity().exitImmersiveModeIfNeeded(listener)
    }

    companion object {
        fun getInstance(imageUrl: String?, videoUrl: String?) = MediaFullScreenFragment()
            .apply {
                arguments = bundleOf(
                    IMAGE_URL to imageUrl,
                    VIDEO_URL to videoUrl
                )
            }

        const val TAG = "MediaFullScreenFragment"
        const val IMAGE_URL = "imageUrl"
        const val VIDEO_URL = "videoUrl"
    }
}