package com.sohohouse.seven.book

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sohohouse.seven.book.bedrooms.BookBedroomFragment
import com.sohohouse.seven.book.cinema.CinemaFragment
import com.sohohouse.seven.book.electriccinema.ElectricCinemaFragment
import com.sohohouse.seven.book.events.EventsFragment
import com.sohohouse.seven.book.fitness.FitnessFragment
import com.sohohouse.seven.book.table.BookATableFragment
import com.sohohouse.seven.housevisit.HouseVisitFragment

class BookViewPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    private val tabs = mutableListOf<BookTab>()

    var currentFragment: Fragment? = null
        private set

    fun addTabs(tabs: List<BookTab>) {
        this.tabs.addAll(tabs)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: Any) {
        currentFragment = fragment as Fragment
        super.setPrimaryItem(container, position, fragment)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.getString(tabs[position].resId)
    }

    override fun getItem(position: Int): Fragment {
        return when (tabs[position]) {
            BookTab.BOOK_A_TABLE -> BookATableFragment()
            BookTab.HOUSE_VISIT -> HouseVisitFragment()
            BookTab.EVENTS -> EventsFragment()
            BookTab.SCREENING -> CinemaFragment()
            BookTab.GYM -> FitnessFragment()
            BookTab.BEDROOMS -> BookBedroomFragment()
            BookTab.ELECTRIC_CINEMA -> ElectricCinemaFragment()
        }
    }

    override fun getCount(): Int {
        return tabs.size
    }
}