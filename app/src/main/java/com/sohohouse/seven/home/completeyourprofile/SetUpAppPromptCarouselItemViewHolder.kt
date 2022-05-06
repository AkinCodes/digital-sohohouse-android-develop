package com.sohohouse.seven.home.completeyourprofile

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemSetUpAppPromptBinding

const val SET_UP_APP_PROMPT_ITEM_LAYOUT = R.layout.item_set_up_app_prompt

class SetUpAppPromptCarouselItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSetUpAppPromptBinding.bind(view)

    fun bind(item: SetUpAppPromptItem, clickFunction: (item: SetUpAppPromptItem) -> Unit) =
        with(binding) {
            setUpAppPromptItemImg.setImageFromUrl(
                item.image,
                placeholder = item.placeholder,
                isRound = true
            )
            setUpAppPromptItemTitle.setText(item.title)
            setUpAppPromptItemSubtext.setText(item.subtitle)

            itemView.clicks {
                clickFunction.invoke(item)
            }
        }

}