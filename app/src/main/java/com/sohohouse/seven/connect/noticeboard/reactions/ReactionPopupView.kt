package com.sohohouse.seven.connect.noticeboard.reactions

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ReactionPopupViewBinding
import com.sohohouse.seven.network.core.models.Reaction

class ReactionPopupView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyleAttrs) {

    private val binding = ReactionPopupViewBinding.inflate(LayoutInflater.from(context), this)
    private var callback: ((Reaction) -> Unit)? = null

    init {
        binding.apply {
            root.setBackgroundResource(R.drawable.reaction_popup_shape_background)
            celebrate.setOnClickListener { callback?.invoke(Reaction.CELEBRATE) }
            pepper.setOnClickListener { callback?.invoke(Reaction.SPICY) }
            thumbsUp.setOnClickListener { callback?.invoke(Reaction.THUMBS_UP) }
            heart.setOnClickListener { callback?.invoke(Reaction.HEART) }
        }
    }

    fun setReactions(possibleReactions: Map<Reaction, String>) = with(binding) {
        possibleReactions.iterator().forEach {
            when (it.key) {
                Reaction.CELEBRATE -> {
                    loadImage(it, celebrate)
                }
                Reaction.SPICY -> loadImage(it, pepper)
                Reaction.THUMBS_UP -> loadImage(it, thumbsUp)
                Reaction.HEART -> loadImage(it, heart)
            }
        }
    }

    fun onReactionClick(callback: ((Reaction) -> Unit)) {
        this.callback = callback
    }

    private fun loadImage(
        it: Map.Entry<Reaction, String>,
        imageView: ImageView,
    ) {
        Glide.with(context)
            .load(it.value)
            .into(imageView)
    }

}