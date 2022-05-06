package com.sohohouse.seven.apponboarding.optinrecommendations

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ConnectWithMoreMembersBinding
import com.sohohouse.seven.databinding.PersonalizedRecommendationsItemBinding

class LandingOptInPagerAdapter(
    private val list: List<LandingOptInPageData>
) : RecyclerView.Adapter<LandingOptInPagerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        parent.clipChildren = false
        parent.clipToPadding = false
        return ViewHolder(
            when (viewType) {
                UPDATE_YOUR_PROFILE ->
                    layoutInflater.inflate(R.layout.update_your_profile_item, parent, false)
                PERSONALIZED_RECOMMENDATIONS ->
                    layoutInflater.inflate(
                        R.layout.personalized_recommendations_item,
                        parent,
                        false
                    )
                CONNECT_WITH_MORE_MEMBERS ->
                    layoutInflater.inflate(R.layout.connect_with_more_members, parent, false)
                else -> error("Unhandled ViewType: $viewType")
            }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = list[position]) {
            is LandingOptInPageData.PersonalizedRecommendations -> {
                val boundView = PersonalizedRecommendationsItemBinding.bind(holder.itemView)
                boundView.optInSwitch.switchLabel.text =
                    holder.getString(R.string.opt_in_switch_label)
                boundView.optInSwitch.switchLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                boundView.optInSwitch.root.setPadding(0)
                boundView.optInSwitch.switchView.setOnCheckedChangeListener { _, isChecked ->
                    item.onOptIn(
                        isChecked
                    )
                }
            }
            is LandingOptInPageData.ContinueWithMoreMembers -> {
                val boundView = ConnectWithMoreMembersBinding.bind(holder.itemView)
                boundView.close.setOnClickListener { item.onClose() }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is LandingOptInPageData.ContinueWithMoreMembers -> CONNECT_WITH_MORE_MEMBERS
            is LandingOptInPageData.PersonalizedRecommendations -> PERSONALIZED_RECOMMENDATIONS
            LandingOptInPageData.UpdateYourProfile -> UPDATE_YOUR_PROFILE
        }
    }

    companion object {
        private const val UPDATE_YOUR_PROFILE = 0
        private const val PERSONALIZED_RECOMMENDATIONS = 1
        private const val CONNECT_WITH_MORE_MEMBERS = 2
    }

}
