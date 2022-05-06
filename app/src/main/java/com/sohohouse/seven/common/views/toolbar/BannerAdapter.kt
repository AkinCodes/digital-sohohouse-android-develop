package com.sohohouse.seven.common.views.toolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderBannerBinding

class BannerAdapter : BaseRecyclerDiffAdapter<BannerViewHolder, Banner>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            ViewHolderBannerBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            it.listener?.invoke()
        }
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BannerViewHolder(
    private val binding: ViewHolderBannerBinding,
    private val onBannerClick: (banner: Banner) -> Unit
) : ViewHolder(binding.root) {
    private var banner: Banner? = null

    init {
        itemView.setOnClickListener {
            banner?.let {
                onBannerClick(it)
            }
        }
    }

    fun bind(banner: Banner) {
        this.banner = banner
        with(binding) {
            title.text = banner.title
            subtitle.text = banner.subtitle
            button.text = banner.cta
        }
    }
}

class BannerRenderer(private val onBannerClick: (banner: Banner) -> Unit) :
    Renderer<Banner, BannerViewHolder> {
    override val type: Class<Banner>
        get() = Banner::class.java

    override fun createViewHolder(parent: ViewGroup): BannerViewHolder {
        return BannerViewHolder(
            ViewHolderBannerBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            onBannerClick(it)
        }
    }

    override fun bindViewHolder(holder: BannerViewHolder, item: Banner) {
        holder.bind(item)
    }

}