package com.sohohouse.seven.common.views.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.databinding.BottomMenuItemViewBinding


class BottomNavigationView : RecyclerView {

    interface NavigationItemListener {
        fun onNavigationItemReselected(item: NavigationItem, pos: Int)
        fun onNavigationItemSelected(item: NavigationItem, pos: Int)
    }

    private val MAX_MENU_ITEM = 5

    private var listener: NavigationItemListener? = null

    private var items: List<NavigationItem> = emptyList()
    private var colorSelected: Int = 0
    private var colorNotSelected: Int = 0
    private var menuItemLayout = R.layout.bottom_menu_item_view
    private var selectedItemIndex: Int = -1
    private var bottomNavIconColorWhileLoading: Int = -1

    var selectedItemId: Int?
        get() = currentItem?.id
        set(value) {
            setCurrentItem(items.indexOfFirst { it.id == value })
        }

    fun indicateCurrentItem(id: Int) {
        indicateCurrentItemInternal(items.indexOfFirst { it.id == id })
    }

    private val currentItem: NavigationItem?
        get() {
            return try {
                items[selectedItemIndex]
            } catch (e: IndexOutOfBoundsException) {
                null
            }
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var spanCount: Int

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationView, defStyleAttr, 0)
        val menuResId = ta.getResourceId(R.styleable.BottomNavigationView_menu, 0)
        menuItemLayout = ta.getResourceId(
            R.styleable.BottomNavigationView_menu_item_layout,
            R.layout.bottom_menu_item_view
        )
        spanCount = ta.getInt(R.styleable.BottomNavigationView_max_menu, MAX_MENU_ITEM)
        colorSelected = ta.getColor(
            R.styleable.BottomNavigationView_color_selected,
            ContextCompat.getColor(context, R.color.colorAccent)
        )
        colorNotSelected = ta.getColor(
            R.styleable.BottomNavigationView_color_not_selected,
            ContextCompat.getColor(context, android.R.color.darker_gray)
        )
        ta.recycle()

        bottomNavIconColorWhileLoading =
            context.getAttributeColor(R.attr.bottomNavIconColorWhileLoading)

        if (menuResId != 0) {
            inflateMenu(menuResId)
        }
    }

    private fun setUpRv(context: Context) {
        layoutManager = object : GridLayoutManager(context, spanCount) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        adapter = BottomMenuAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun inflateMenu(menuResId: Int) {
        items = BottomMenuInflator(context).inflate(menuResId)
        if (items.size in 1 until spanCount) {
            spanCount = items.size
            selectedItemIndex = items.indexOf(items.firstOrNull { it.selected })
        }
        setUpRv(context)
        adapter?.notifyDataSetChanged()
    }

    fun setNavigationItemListener(listener: NavigationItemListener) {
        this.listener = listener
    }

    private fun findItemById(id: Int): NavigationItem? {
        return items.firstOrNull { it.id == id }
    }

    private fun setCurrentItem(position: Int) {
        post {
            val holder = findViewHolderForAdapterPosition(position)
            holder?.itemView?.performClick()
        }
    }

    private fun indicateCurrentItemInternal(position: Int) {
        post {
            val holder =
                findViewHolderForAdapterPosition(position) as? BottomMenuAdapter.MenuViewHolder
            holder?.setSelected()
        }
    }

    fun setIcon(id: Int, drawable: Drawable?) {
        val item = findItemById(id)?.also { it.icon = drawable } ?: return
        items.indexOf(item).let { adapter?.notifyItemChanged(it) }
    }

    fun updateLoadingState(loadingState: LoadingState) {
        changeBottomNavIconsColor(loadingState)
    }

    private fun changeBottomNavIconsColor(loadingState: LoadingState) {
        (layoutManager as? GridLayoutManager)?.apply {
            val from = findFirstVisibleItemPosition()
            val to = findLastVisibleItemPosition()
            for (i in from until to) { //last one is profile icon, so we do not need profile icon to be white
                (getChildAt(i) as? FrameLayout)?.let {
                    changeDrawableColor(loadingState, it.getChildAt(0) as? ImageView)
                }
            }
        }
    }

    private fun changeDrawableColor(loadingState: LoadingState, imageView: ImageView?) {
        imageView?.drawable?.apply {
            colorFilter =
                if (loadingState == LoadingState.Loading) BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    bottomNavIconColorWhileLoading,
                    BlendModeCompat.SRC_IN
                )
                else null
        }
    }

    /**
     * BottomMenuAdapter
     */
    inner class BottomMenuAdapter : RecyclerView.Adapter<BottomMenuAdapter.MenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            return MenuViewHolder(
                BottomMenuItemViewBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        /**
         * MenuViewHolder
         */
        inner class MenuViewHolder(private val binding: BottomMenuItemViewBinding) :
            ViewHolder(binding.root), OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                if (selectedItemIndex == adapterPosition) {
                    listener?.onNavigationItemReselected(
                        items[selectedItemIndex],
                        selectedItemIndex
                    )
                    return
                }

                setSelected()

                listener?.onNavigationItemSelected(items[adapterPosition], adapterPosition)
            }

            fun setSelected() {
                (findViewHolderForAdapterPosition(selectedItemIndex) as? MenuViewHolder)?.run {
                    setSelected(false)
                }
                setSelected(true)
                selectedItemIndex = adapterPosition
            }

            fun bind(item: NavigationItem) {
                setSelected(selectedItemIndex == adapterPosition)
                binding.icon.setImageDrawable(item.icon)
            }

            private fun setSelected(isSelected: Boolean) {
                items[adapterPosition].selected = isSelected
                binding.icon.isSelected = isSelected
            }
        }
    }
}
