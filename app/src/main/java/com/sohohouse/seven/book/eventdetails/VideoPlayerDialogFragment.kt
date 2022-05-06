package com.sohohouse.seven.book.eventdetails

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.ui.PlayerView
import com.sohohouse.seven.R

class VideoPlayerDialogFragment : DialogFragment() {

    var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            playerView?.let {
                addContentView(
                    it,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                it.findViewById<ImageButton>(R.id.exo_fullscreen_button)?.apply {
                    this.setImageResource(R.drawable.ic_fullscreen_exit)
                    this.setOnClickListener { dismiss() }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        (requireActivity() as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
        playerView = null
        super.onDismiss(dialog)
    }

    companion object {
        const val TAG = "video_view"
    }
}