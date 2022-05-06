package com.sohohouse.seven.connect.noticeboard.post_details.adapter

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemNoticeboardPostReplyBinding

class NoticeboardPostReplyRenderer() :
    BaseRenderer<NoticeboardPostReply, NoticeboardPostReplyViewHolder>(NoticeboardPostReply::class.java) {

    override fun bindViewHolder(
        item: NoticeboardPostReply,
        holder: NoticeboardPostReplyViewHolder,
    ) {
        holder.bind(item)
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_noticeboard_post_reply
    }

    override fun createViewHolder(view: View): NoticeboardPostReplyViewHolder {
        val binding = ItemNoticeboardPostReplyBinding.bind(view)
        return NoticeboardPostReplyViewHolder(
            binding,
        )
    }
}