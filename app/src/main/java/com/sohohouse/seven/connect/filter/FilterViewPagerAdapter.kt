package com.sohohouse.seven.connect.filter

import android.os.Bundle
import android.util.SparseArray
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterFragment
import com.sohohouse.seven.connect.filter.base.FilterMode
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.city.CityFilterFragment
import com.sohohouse.seven.connect.filter.house.HouseFilterFragment
import com.sohohouse.seven.connect.filter.industry.IndustryFilterFragment
import com.sohohouse.seven.connect.filter.topic.TopicFilterFragment
import java.lang.ref.WeakReference

class FilterViewPagerAdapter(
    fragment: Fragment,
    private val mode: FilterMode,
    private val types: Array<FilterType>,
    private val filters: Array<Filter>? = null
) : FragmentStateAdapter(fragment) {

    private val fragments: SparseArray<WeakReference<FilterFragment<*>>> = SparseArray()

    override fun getItemCount(): Int = types.size

    override fun createFragment(position: Int): Fragment {
        return when (types[position]) {
            FilterType.HOUSE_FILTER -> HouseFilterFragment()
            FilterType.CITY_FILTER -> CityFilterFragment()
            FilterType.TOPIC_FILTER -> TopicFilterFragment()
            FilterType.INDUSTRY_FILTER -> IndustryFilterFragment()
        }.also {
            it.arguments = Bundle().apply {
                putString(BundleKeys.FILTER_MODE, mode.name)
                putParcelableArray(BundleKeys.FILTERS, filters)
            }
            fragments.append(position, WeakReference(it))
        }
    }

    @StringRes
    fun getTabTitle(position: Int): Int {
        return when (types[position]) {
            FilterType.HOUSE_FILTER -> R.string.noticeboard_filter_house
            FilterType.CITY_FILTER -> R.string.noticeboard_filter_city
            FilterType.TOPIC_FILTER -> R.string.noticeboard_filter_topic
            FilterType.INDUSTRY_FILTER -> R.string.noticeboard_filter_industry
        }
    }

    fun getFragment(position: Int): FilterFragment<*>? = fragments[position]?.get()

}