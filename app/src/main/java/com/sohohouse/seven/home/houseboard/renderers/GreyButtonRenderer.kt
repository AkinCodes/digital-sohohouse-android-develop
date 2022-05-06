package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.databinding.ItemButtonDarkBinding
import com.sohohouse.seven.databinding.ItemButtonGreyBinding
import com.sohohouse.seven.databinding.ItemButtonSecondaryBinding
import com.sohohouse.seven.home.houseboard.items.ButtonItem
import com.sohohouse.seven.home.houseboard.items.DarkButtonItem
import com.sohohouse.seven.home.houseboard.items.SecondaryButtonItem
import com.sohohouse.seven.home.houseboard.viewholders.*

class GreyButtonRenderer(private val clickListener: (action: String?) -> Unit) :
    BaseRenderer<ButtonItem, GreyButtonViewHolder>(ButtonItem::class.java) {

    override fun bindViewHolder(item: ButtonItem?, holder: GreyButtonViewHolder?) {
        holder?.run {
            setButtonText(item?.text ?: "")
            setOnClickListener {
                if (item != null) {
                    clickListener(item.action)
                }
            }
        }
    }

    override fun getLayoutResId(): Int = GREY_BUTTON_LAYOUT

    override fun createViewHolder(view: View): GreyButtonViewHolder =
        GreyButtonViewHolder(ItemButtonGreyBinding.bind(view))

}

class DarkButtonRenderer(private val clickListener: (action: String?) -> Unit) :
    BaseRenderer<DarkButtonItem, DarkButtonViewHolder>(DarkButtonItem::class.java) {
    override fun bindViewHolder(item: DarkButtonItem?, holder: DarkButtonViewHolder?) {
        holder?.run {
            setButtonText(item?.text ?: "")
            setOnClickListener {
                if (item != null) {
                    clickListener(item.action)
                }
            }
        }
    }

    override fun getLayoutResId(): Int = DARK_BUTTON_LAYOUT

    override fun createViewHolder(view: View): DarkButtonViewHolder =
        DarkButtonViewHolder(ItemButtonDarkBinding.bind(view))
}

class SecondaryButtonRenderer(private val clickListener: (action: String?) -> Unit) :
    BaseRenderer<SecondaryButtonItem, SecondaryButtonViewHolder>(SecondaryButtonItem::class.java) {
    override fun bindViewHolder(item: SecondaryButtonItem?, holder: SecondaryButtonViewHolder?) {
        holder?.run {
            setButtonText(item?.text ?: "")
            setOnClickListener {
                if (item != null) {
                    clickListener(item.action)
                }
            }
        }
    }

    override fun getLayoutResId(): Int = SECONDARY_BUTTON_LAYOUT

    override fun createViewHolder(view: View): SecondaryButtonViewHolder =
        SecondaryButtonViewHolder(ItemButtonSecondaryBinding.bind(view))
}