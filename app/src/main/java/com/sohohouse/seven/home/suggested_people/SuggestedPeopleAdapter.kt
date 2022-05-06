package com.sohohouse.seven.home.suggested_people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.SuggestedPeopleCarouselWildcardLayoutBinding
import com.sohohouse.seven.databinding.SuggestedPeopleItemBinding
import com.sohohouse.seven.databinding.SuggestedPeoplePlaceholderBinding
import eightbitlab.com.blurview.RenderScriptBlur

class SuggestedPeopleAdapter(
    private val recyclerItemCallback: (userID: String) -> Unit,
    private val optInCallback: (View) -> Unit,
) : ListAdapter<SuggestedPeopleAdapterItem, RecyclerView.ViewHolder>(
    DefaultDiffItemCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SuggestedPeopleAdapterItem.PLACEHOLDER -> {
                Placeholder(
                    SuggestedPeoplePlaceholderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            SuggestedPeopleAdapterItem.NORMAL_ITEM -> {
                SuggestedMemberViewHolder(
                    SuggestedPeopleItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            SuggestedPeopleAdapterItem.WILD_CARD -> {
                SuggestedMemberWildCardViewHolder(
                    SuggestedPeopleCarouselWildcardLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> throw IllegalStateException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SuggestedMemberViewHolder ->
                holder.bind(getItem(position) as SuggestedPeopleAdapterItem.NormalItem)
            is Placeholder ->
                holder.bind(
                    getItem(position) as SuggestedPeopleAdapterItem.Placeholder,
                    optInCallback
                )
            is SuggestedMemberWildCardViewHolder ->
                holder.bind(getItem(position) as SuggestedPeopleAdapterItem.WildCard)
        }
    }

    inner class SuggestedMemberWildCardViewHolder(
        private val binding: SuggestedPeopleCarouselWildcardLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SuggestedPeopleAdapterItem.WildCard): Unit = with(binding) {
            binding.root.setOnClickListener {
                recyclerItemCallback.invoke(data.id)
            }
            Glide.with(itemView.context)
                .asBitmap()
                .centerCrop()
                .load(data.avatarUrl)
                .into(suggestedUserAvatar)
            bluerForeground.setupWith(root)
                .setFrameClearDrawable(root.background)
                .setBlurAlgorithm(RenderScriptBlur(itemView.context))
                .setBlurRadius(4f)
                .setHasFixedTransformationMatrix(true)
        }

    }

    inner class SuggestedMemberViewHolder(
        private val binding: SuggestedPeopleItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SuggestedPeopleAdapterItem.NormalItem) {
            with(binding) {
                binding.root.setOnClickListener {
                    recyclerItemCallback.invoke(data.id)
                }
                Glide.with(itemView.context)
                    .asBitmap()
                    .centerCrop()
                    .load(data.avatarUrl)
                    .into(suggestedUserAvatar)
                suggestedUserName.text = data.name
                suggestedUserAbout.text = data.description
                suggestionReason.text = getString(data.reason.stringRes)
            }
        }
    }

    class Placeholder(
        private val binding: SuggestedPeoplePlaceholderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestedPeopleAdapterItem.Placeholder, optInCallback: (View) -> Unit) {
            itemView.setOnClickListener(optInCallback)

            with(binding) {
                suggestedUserAvatar.setImageResource(item.imageResId)
                bluerForeground.setupWith(root)
                    .setFrameClearDrawable(root.background)
                    .setBlurAlgorithm(RenderScriptBlur(itemView.context))
                    .setBlurRadius(4f)
                    .setHasFixedTransformationMatrix(true)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

}