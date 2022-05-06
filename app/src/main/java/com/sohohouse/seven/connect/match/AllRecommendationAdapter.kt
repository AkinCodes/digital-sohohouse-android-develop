package com.sohohouse.seven.connect.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setImageWithPlaceHolder
import com.sohohouse.seven.databinding.SuggestedPeopleItemHorisontalBinding
import com.sohohouse.seven.databinding.SuggestedPeopleWildcardLayoutBinding
import com.sohohouse.seven.home.suggested_people.SuggestedPeopleAdapterItem
import eightbitlab.com.blurview.RenderScriptBlur

class AllRecommendationAdapter(private val onMemberClick: (id: String) -> Unit) :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, SuggestedPeopleAdapterItem>() {

    inner class RecommendationViewHolder(val binding: SuggestedPeopleItemHorisontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SuggestedPeopleAdapterItem.NormalItem) {
            with(binding) {
                suggestedUserName.text = item.name
                suggestedUserAbout.text = item.description
                suggestionReason.text = getString(item.reason.stringRes)
                suggestedUserAvatar.setImageWithPlaceHolder(item.avatarUrl, R.drawable.placeholder)
                root.setOnClickListener {
                    onMemberClick(item.id)
                }
            }
        }
    }

    inner class RecommendationWildCardViewHolder(val binding: SuggestedPeopleWildcardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SuggestedPeopleAdapterItem.WildCard) {
            with(binding) {
                bluerForeground.setupWith(root)
                    .setFrameClearDrawable(root.background)
                    .setBlurAlgorithm(RenderScriptBlur(itemView.context))
                    .setBlurRadius(4f)
                    .setHasFixedTransformationMatrix(true)
                    .setBlurEnabled(false)
                suggestedUserAvatar.setImageWithPlaceHolder(
                    item.avatarUrl,
                    R.drawable.placeholder
                ) {
                    bluerForeground.setBlurEnabled(true)
                }
                root.setOnClickListener {
                    onMemberClick(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SuggestedPeopleAdapterItem.NORMAL_ITEM -> RecommendationViewHolder(
                SuggestedPeopleItemHorisontalBinding.inflate(inflater, parent, false)
            )
            SuggestedPeopleAdapterItem.WILD_CARD -> RecommendationWildCardViewHolder(
                SuggestedPeopleWildcardLayoutBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecommendationViewHolder ->
                holder.bind(getItem(position) as SuggestedPeopleAdapterItem.NormalItem)
            is RecommendationWildCardViewHolder ->
                holder.bind(getItem(position) as SuggestedPeopleAdapterItem.WildCard)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }
}