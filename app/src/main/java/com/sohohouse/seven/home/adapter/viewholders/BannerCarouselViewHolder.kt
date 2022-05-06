package com.sohohouse.seven.home.adapter.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.CircleOutlineProvider
import com.sohohouse.seven.databinding.ItemBannerShortcutBinding
import com.sohohouse.seven.databinding.ItemHomeCarouselBinding

const val BANNER_CAROUSEL_LAYOUT = R.layout.item_home_carousel

class BannerCarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeCarouselBinding.bind(view)

    fun bind(shortcuts: Array<BannerShortcut>, shortcutListener: ((BannerShortcut) -> Unit)?) {
        binding.recyclerView.adapter = BannerShortcutAdapter(shortcuts)
            .apply { listener = shortcutListener }
    }
}

class BannerShortcutAdapter(private val items: Array<BannerShortcut>) :
    RecyclerView.Adapter<BannerShortcutHolder>() {
    var listener: ((BannerShortcut) -> Unit)? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerShortcutHolder =
        BannerShortcutHolder(
            ItemBannerShortcutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BannerShortcutHolder, position: Int) {
        holder.bind(items[position], listener)
    }
}

class BannerShortcutHolder(
    val binding: ItemBannerShortcutBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.circleImage.let {
            it.outlineProvider = CircleOutlineProvider()
            it.clipToOutline = true
        }
    }

    fun bind(item: BannerShortcut, listener: ((BannerShortcut) -> Unit)?) {
        binding.circleImage.setImageResource(item.image)
        binding.title.setText(item.title)
        itemView.setOnClickListener {
            listener?.invoke(item)
        }
    }
}

enum class BannerShortcut(val image: Int, val title: Int) {
    GUEST_INVITE(R.drawable.shortcut_invite_a_guest, R.string.shortcut_guest_invite),
    HOUSE_VISIT(R.drawable.shortcut_visit_a_house, R.string.shortcut_house_visit),
    BEDROOMS(R.drawable.shortcut_stay_with_us, R.string.shortcut_stay_with_us),
    HOUSE_PAY(R.drawable.house_pay_landing, R.string.shortcut_house_pay),
    BOOK_EVENT(R.drawable.shortcut_book_an_event, R.string.shortcut_book_event),
    BOOK_GYM(R.drawable.shortcut_book_a_gym, R.string.shortcut_gym),
    BOOK_SCREENING(R.drawable.shortcut_book_a_screening, R.string.shortcut_screening),
    BENEFITS(R.drawable.shortcut_discover_member_benefits, R.string.shortcut_benefits),
    RESTAURANT(R.drawable.shortcut_restourant, R.string.shortcut_visit_our_restaurants),
    OUR_SPACES(R.drawable.shortcut_explore_our_spaces, R.string.shortcut_explore_our_spaces);

    companion object {
        val SHORTCUTS_FOR_FRIENDS = arrayOf(
            BEDROOMS,
            OUR_SPACES,
            GUEST_INVITE,
            RESTAURANT,
            BENEFITS,
            BOOK_SCREENING
        )
        val SHORTCUTS_FOR_MEMBERS = arrayOf(
            GUEST_INVITE,
            HOUSE_VISIT,
            RESTAURANT,
            HOUSE_PAY,
            BEDROOMS,
            BOOK_EVENT,
            BOOK_GYM,
            BOOK_SCREENING,
            BENEFITS
        )
    }
}