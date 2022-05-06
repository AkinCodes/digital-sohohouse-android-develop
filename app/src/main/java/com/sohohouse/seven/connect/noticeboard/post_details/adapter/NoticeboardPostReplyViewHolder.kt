package com.sohohouse.seven.connect.noticeboard.post_details.adapter

import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ItemNoticeboardPostReplyBinding

class NoticeboardPostReplyViewHolder(
    private val binding: ItemNoticeboardPostReplyBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        // required for createClickableSpannableForSubstring to work
        binding.authorName.movementMethod = LinkMovementMethod.getInstance()
    }

    fun bind(item: NoticeboardPostReply): Unit = with(binding) {
        setProfilePhoto(item)
        timestamp.text = item.timeStamp
        messageContent.setLinkableText(item.message)
        staffIndication.isVisible = item.isStaff

        if (item.showDeleteButton) {
            deletePostBtn.setVisible()
            deletePostBtn.setOnClickListener {
                item.onDeleteClick.invoke()
            }
        } else {
            deletePostBtn.setGone()
        }
        setSpannableAndClickListener(item.authorName) {
            item.onProfileClick.invoke()
        }
    }

    private fun ItemNoticeboardPostReplyBinding.setSpannableAndClickListener(
        name: String, callback: () -> Unit,
    ) {
        val rawString = getString(R.string.poster_replied, name)
        authorName.text = rawString.createClickableSpannableForSubstring(name, {
            callback.invoke()
        })
    }

    private fun setProfilePhoto(item: NoticeboardPostReply) {
        binding.profileImage.apply {
            setImageFromUrl(
                item.profileUrl,
                isRound = true,
                placeholder = R.drawable.ic_profile
            )
            setOnClickListener { item.onProfileClick.invoke() }
        }
    }

}