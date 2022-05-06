package com.sohohouse.seven.housenotes.detail.sitecore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.databinding.*
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailSitecoreItemType.*

class HouseNoteDetailsAdapter(private val backAction: () -> Unit) :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, HouseNoteDetailItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (values()[viewType]) {
            HEADER -> HeaderViewHolder(
                HouseNoteDetailHeaderCardLayoutBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            TEXT_BLOCK -> TextBlockViewHolder(
                ItemHouseNoteDetailBodyTextblockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            TITLE_BLOCK -> TitleBlockViewHolder(
                ItemHouseNoteDetailBodyTitleblockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            BODY_IMAGE_BLOCK -> BodyImageBlockViewHolder(
                ItemHouseNoteDetailsBodyImageblockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            CAPTION_BLOCK -> CaptionBlockViewHolder(
                ItemHouseNoteDetailsImageCaptionBlockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            QUOTE_BLOCK -> QuoteBlockViewHolder(
                ItemHouseNoteDetailQuoteblockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            FULL_BLEED_YT_VIDEO, HEADER_VIDEO -> YoutubeVideoBlockViewHolder(
                HouseNoteDetailHeaderVideoLayoutBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            CROPPED_YT_VIDEO -> YoutubeVideoBlockViewHolder(
                HouseNoteDetailCroppedVideoLayoutBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            IMAGE_CAROUSEL_BLOCK -> ImageCarouselBlockViewHolder(
                CarouselContainerLayoutBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            GRID_IMAGE_BLOCK -> GridImageBlockViewHolder(
                ItemHouseNoteDetailsGridImageBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            INFORMATION -> InformationViewHolder(
                ItemHouseNoteInformationBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            CREDIT -> CreditViewHolder(
                ItemHouseNoteDetailCreditBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            HEADER_IMAGE -> HeaderImageViewHolder(
                HouseNoteDetailHeaderImageLayoutBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            HEADER_TEXT_BLOCK -> HeaderTextBlockViewHolder(
                ItemHouseNoteDetailsHeaderTextblockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
            CROPPED_VIMEO_VIDEO -> {
                VimeoVideoViewHolder(
                    ItemHouseNoteDetailCroppedVimeoBlockBinding.inflate(
                        getLayoutInflater(parent),
                        parent,
                        false
                    )
                )
            }
            FULL_BLEED_VIMEO_VIDEO -> VimeoVideoViewHolder(
                ItemHouseNoteDetailFullbleedVimeoBlockBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                )
            )
        }
    }

    private fun getLayoutInflater(parent: ViewGroup) = LayoutInflater.from(parent.context)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (values()[viewHolder.itemViewType]) {
            HEADER -> (viewHolder as HeaderViewHolder).bind(item as HouseNoteDetailHeaderItem)
            TEXT_BLOCK -> (viewHolder as TextBlockViewHolder).bind(item as HouseNoteDetailTextBlockItem)
            TITLE_BLOCK -> (viewHolder as TitleBlockViewHolder).bind(item as HouseNoteDetailTitleBlockItem)
            BODY_IMAGE_BLOCK -> (viewHolder as BodyImageBlockViewHolder).bind(item as HouseNoteDetailIBodyImageBlockItem)
            CAPTION_BLOCK -> (viewHolder as CaptionBlockViewHolder).bind(item as HouseNoteDetailImageCaptionBlockItem)
            QUOTE_BLOCK -> (viewHolder as QuoteBlockViewHolder).bind(item as HouseNoteDetailQuoteBlockItem)
            FULL_BLEED_YT_VIDEO,
            HEADER_VIDEO,
            CROPPED_YT_VIDEO -> (viewHolder as YoutubeVideoBlockViewHolder).bind(
                item as HouseNoteDetailYoutubeVideoBlockItem,
                back = if (position == 0) backAction else null
            )
            IMAGE_CAROUSEL_BLOCK -> (viewHolder as ImageCarouselBlockViewHolder).bind(item as HouseNoteDetailImageCarouselBlockItem)
            GRID_IMAGE_BLOCK -> (viewHolder as GridImageBlockViewHolder).bind(item as HouseNoteDetailGridImageBlockItem)
            INFORMATION -> (viewHolder as InformationViewHolder).bind(item as HouseNoteDetailInformationItem)
            CREDIT -> (viewHolder as CreditViewHolder).bind(item as HouseNoteDetailCreditItem)
            HEADER_IMAGE -> (viewHolder as HeaderImageViewHolder).bind(item as HouseNoteDetailHeaderImageItem)
            HEADER_TEXT_BLOCK -> (viewHolder as HeaderTextBlockViewHolder).bind(item as HouseNoteDetailHeaderTextBlockItem)
            FULL_BLEED_VIMEO_VIDEO,
            CROPPED_VIMEO_VIDEO -> (viewHolder as VimeoVideoViewHolder).bind(item as HouseNoteDetailVimeoVideoBlockItem)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is YoutubeVideoBlockViewHolder) {
            holder.pauseListener.invoke()
        }
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return currentItems[position].itemType.ordinal
    }
}