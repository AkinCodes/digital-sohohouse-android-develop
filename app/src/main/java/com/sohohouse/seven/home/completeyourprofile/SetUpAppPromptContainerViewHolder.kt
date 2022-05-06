package com.sohohouse.seven.home.completeyourprofile

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemSetUpAppPromptsContainerBinding

const val SET_UP_APP_PROMPTS_CONTAINER_LAYOUT = R.layout.item_set_up_app_prompts_container

class SetUpAppPromptContainerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSetUpAppPromptsContainerBinding.bind(view)

    fun bind(carouselAdapter: SetUpAppPromptsCarouselAdapter) {
        binding.setUpAppPromptCarouselRv.apply {
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = carouselAdapter
        }
    }
}