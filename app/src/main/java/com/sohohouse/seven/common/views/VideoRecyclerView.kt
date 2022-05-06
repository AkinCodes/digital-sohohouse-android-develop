package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setInvisible
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.connect.message.chat.content.ChatContentViewHolder
import com.sohohouse.seven.network.chat.model.message.Message

class VideoRecyclerView @JvmOverloads constructor(
    viewContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(viewContext, attrs, defStyleAttr) {

    private var appContext = context.applicationContext

    // ui
    private var thumbnailView: AsyncImageView? = null
    private var progressBarView: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var frameLayout: FrameLayout? = null
    private var playBtn: ImageView? = null
    private var exoVideoView: PlayerView? = null
    private var videoPlayer: ExoPlayer? = SimpleExoPlayer.Builder(appContext).build()

    // vars
    private var playPosition = 0
    private var isVideoViewAdded = false

    lateinit var cacheDataSourceFactory: CacheDataSource.Factory

    init {
        exoVideoView = PlayerView(appContext).apply {
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            useController = false
        }

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    thumbnailView?.visibility = VISIBLE
                }
            }
        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                if (view.tag == null) return

                try {
                    val holder = view.tag as ChatContentViewHolder.MessageHolder
                    if (holder.dataItem?.messageContents?.get(0) !is Message.Video)
                        return

                    holder.videoMsgBinding?.apply {
                        val clickedPos = holder.absoluteAdapterPosition
                        playVideoIcon.setOnClickListener {
                            if (videoPlayer?.isPlaying == true && clickedPos == playPosition) {
                                videoPlayer?.pause()
                                playVideoIcon.setVisible()
                            } else {
                                playPosition = clickedPos
                                playVideo(holder)
                            }
                        }
                    }
                } catch (ex: ClassCastException) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent!! == view) {
                    resetVideoView()
                }
            }
        })

        videoPlayer?.addListener(object : Player.EventListener {
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        progressBarView?.setVisible()
                    }
                    Player.STATE_ENDED -> {
                        videoPlayer?.pause()
                        videoPlayer?.seekTo(0)
                        playBtn?.setVisible()
                        thumbnailView?.setVisible()
                    }
                    Player.STATE_READY -> {
                        progressBarView?.setGone()
                        if (!isVideoViewAdded)
                            addVideoView()
                    }
                    else -> {
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
    }

    fun playVideo(holder: ChatContentViewHolder.MessageHolder) {

        if (exoVideoView == null) return

        // remove any old surface views from previously playing videos
        exoVideoView?.setGone()
        removeVideoView(exoVideoView)

        holder.videoMsgBinding?.let { binding ->
            thumbnailView = binding.thumbnailImage
            progressBarView = binding.progressBar
            viewHolderParent = holder.boundView.root
            frameLayout = binding.card
            exoVideoView?.player = videoPlayer
            playBtn = binding.playVideoIcon

            val videoMsgData = holder.dataItem?.messageContents?.get(0) as Message.Video

            val mediaItem = MediaItem.Builder()
                .setUri(videoMsgData.messageVideoUrl)
                .setMediaId(videoMsgData.id)
                .build()

            val mediaSource =
                DefaultMediaSourceFactory(cacheDataSourceFactory, DefaultExtractorsFactory())
                    .createMediaSource(mediaItem)

            videoPlayer?.apply {
                playWhenReady = true
                setMediaSource(mediaSource)
                prepare()
            }
        }
    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView?.parent as? ViewGroup ?: return
        val index = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            playBtn?.setVisible()
            thumbnailView?.setVisible()
        }
    }

    private fun addVideoView() {
        frameLayout?.addView(exoVideoView)
        isVideoViewAdded = true
        exoVideoView?.requestFocus()
        exoVideoView?.setVisible()
        exoVideoView?.alpha = 1f
        thumbnailView?.setGone()
        playBtn?.setGone()
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(exoVideoView)
            playPosition = -1
            videoPlayer?.pause()
            exoVideoView?.setInvisible()
            thumbnailView?.setVisible()
        }
    }

    fun releasePlayer() {
        videoPlayer?.release()
        videoPlayer = null
        viewHolderParent = null
    }

}