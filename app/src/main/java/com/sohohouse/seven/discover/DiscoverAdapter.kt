package com.sohohouse.seven.discover

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.discover.housenotes.HouseNotesFragment
import com.sohohouse.seven.discover.houses.HousesFragment
import com.sohohouse.seven.discover.benefits.BenefitsFragment
import java.lang.ref.WeakReference

class DiscoverAdapter(fragment: Fragment, private val tags: List<String>) :
    FragmentStateAdapter(fragment) {

    private val fragments: SparseArray<WeakReference<Fragment>> = SparseArray()

    override fun getItemCount(): Int = tags.size

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (tags[position]) {
            HouseNotesFragment.TAG -> HouseNotesFragment()
            HousesFragment.TAG -> HousesFragment()
            BenefitsFragment.TAG -> BenefitsFragment()
            StudioSpacesFragment.TAG -> StudioSpacesFragment()
            else -> throw IndexOutOfBoundsException()
        }
        fragments.append(position, WeakReference(fragment))
        return fragment
    }

    fun getFragmentAt(position: Int): Fragment? = fragments.get(position)?.get()

    fun indexOf(tag: String): Int = tags.indexOf(tag)
}