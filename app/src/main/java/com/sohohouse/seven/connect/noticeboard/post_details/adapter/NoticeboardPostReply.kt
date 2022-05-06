package com.sohohouse.seven.connect.noticeboard.post_details.adapter

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.connect.noticeboard.NoticeboardPost

data class NoticeboardPostReply(
    val postID: String,
    val timeStamp: String,
    val message: String,
    val isStaff: Boolean,
    val showDeleteButton: Boolean,
    val profileUrl: String,
    val authorName: String,
    val onDeleteClick: () -> Unit,
    val onProfileClick: () -> Unit,
) : DiffItem {

    constructor(
        reply: NoticeboardPost,
        showDeleteButton: Boolean,
        onDeleteClick: () -> Unit,
        onProfileClick: () -> Unit,
    ) : this(
        postID = reply.postId,
        timeStamp = reply.timeStamp,
        message = reply.messageContent,
        isStaff = reply.profile.isStaff,
        showDeleteButton = showDeleteButton,
        profileUrl = reply.profile.imageUrl ?: "",
        authorName = reply.profile.fullName,
        onDeleteClick = onDeleteClick,
        onProfileClick = onProfileClick
    )

    override val key: Any
        get() = postID
}