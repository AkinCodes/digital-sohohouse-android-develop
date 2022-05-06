package com.sohohouse.seven.connect.message.chat.content.binder

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.databinding.ChatImageMessageItemBinding
import com.sohohouse.seven.databinding.ChatMessageItemBinding
import com.sohohouse.seven.databinding.ChatMessageLinkItemBinding
import com.sohohouse.seven.databinding.ChatVideoMessageItemBinding

open class BaseMsgBinder {

    fun <T : ViewBinding> adjustConstraints(
        msgItemBinding: T,
        isSentByMe: Boolean
    ) {

        when (msgItemBinding) {
            is ChatVideoMessageItemBinding ->
                setConstraints(
                    msgItemBinding.root,
                    msgItemBinding.date.id,
                    msgItemBinding.card.id,
                    isSentByMe
                )
            is ChatImageMessageItemBinding ->
                setConstraints(
                    msgItemBinding.root,
                    msgItemBinding.date.id,
                    msgItemBinding.card.id,
                    isSentByMe
                )
            is ChatMessageLinkItemBinding ->
                setConstraints(
                    msgItemBinding.root,
                    msgItemBinding.date.id,
                    msgItemBinding.card.id,
                    isSentByMe
                )
            is ChatMessageItemBinding ->
                setConstraints(
                    msgItemBinding.root,
                    msgItemBinding.date.id,
                    msgItemBinding.message.id,
                    isSentByMe
                )
            else -> return
        }
    }

    private fun setConstraints(
        rootLayout: ConstraintLayout,
        dateID: Int,
        msgPlaceID: Int,
        isSentByMe: Boolean
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(rootLayout)

        constraintSet.clear(dateID, ConstraintSet.END)
        constraintSet.clear(dateID, ConstraintSet.START)

        if (isSentByMe) {
            constraintSet.setHorizontalBias(msgPlaceID, 1F)
            constraintSet.connect(
                dateID,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
        } else {
            constraintSet.setHorizontalBias(msgPlaceID, 0F)
            constraintSet.connect(
                dateID,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
        }
        constraintSet.applyTo(rootLayout)
    }
}