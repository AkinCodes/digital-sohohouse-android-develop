package com.sohohouse.seven.home.completeyourprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter

class SetUpAppPromptsCarouselAdapter(
    private val layoutRes: Int, items: List<SetUpAppPromptItem>,
    private val clickFunction: (item: SetUpAppPromptItem) -> Unit
) : BaseRecyclerDiffAdapter<SetUpAppPromptCarouselItemViewHolder, SetUpAppPromptItem>() {

    init {
        submitList(items)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SetUpAppPromptCarouselItemViewHolder {
        return SetUpAppPromptCarouselItemViewHolder(
            LayoutInflater.from(p0.context).inflate(layoutRes, p0, false)
        )
    }

    override fun onBindViewHolder(holder: SetUpAppPromptCarouselItemViewHolder, index: Int) {
        holder.bind(currentItems[index], clickFunction)
    }

}