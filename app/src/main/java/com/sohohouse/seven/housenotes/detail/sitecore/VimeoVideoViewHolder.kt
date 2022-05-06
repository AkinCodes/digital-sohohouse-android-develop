package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.databinding.ItemHouseNoteDetailVimeoBlockContentBinding
import timber.log.Timber

class VimeoVideoViewHolder(private val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        ItemHouseNoteDetailVimeoBlockContentBinding.bind(binding.root).playerView.setup()
    }

    private var item: HouseNoteDetailVimeoVideoBlockItem? = null


    fun bind(item: HouseNoteDetailVimeoVideoBlockItem) {
        if (item != this.item) {
            Timber.d("new item, loading video")
            ItemHouseNoteDetailVimeoBlockContentBinding.bind(binding.root).playerView.loadUrl(item.videoUrl)
        } else {
            Timber.d("item already bound, not loading video")
        }
        this.item = item
    }

}