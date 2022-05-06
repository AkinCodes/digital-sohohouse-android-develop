package com.sohohouse.seven.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sohohouse.seven.book.BookFragment
import com.sohohouse.seven.connect.ConnectTabFragment
import com.sohohouse.seven.discover.DiscoverFragment
import com.sohohouse.seven.home.HomeFragment
import com.sohohouse.seven.more.AccountFragment

class MainFlowManager(private val fragmentManager: FragmentManager) {

    private var savedState: HashMap<String, Fragment.SavedState?> = hashMapOf()

    private var currentItem: Fragment? = null

    fun saveState(outState: Bundle) {
        outState.putSerializable(KEY_SAVED_STATE, savedState)
    }

    @Suppress("UNCHECKED_CAST")
    fun restoreSavedState(savedInstanceState: Bundle?) {
        (savedInstanceState?.getSerializable(KEY_SAVED_STATE) as? HashMap<String, Fragment.SavedState?>)?.let {
            savedState = it
        }
    }

    fun transitionTo(tag: String): Fragment {
        saveState()
        return createFragment(tag).also { restoreState(it) }
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            FRAG_HOME -> HomeFragment()
            FRAG_DISCOVER -> DiscoverFragment()
            FRAG_BOOK -> BookFragment()
            FRAG_ACCOUNT -> AccountFragment()
            FRAG_CONNECT -> ConnectTabFragment()
            else -> throw IllegalStateException("Unexpected main navigation: $tag")
        }.also { currentItem = it }
    }

    private fun saveState() {
        currentItem?.let {
            if (it.isAdded) savedState[it::class.java.simpleName] =
                fragmentManager.saveFragmentInstanceState(it)
        }
    }

    private fun restoreState(fragment: Fragment) {
        fragment.setInitialSavedState(savedState[fragment::class.java.simpleName])
        savedState.remove(fragment::class.java.simpleName)
    }

    companion object {
        private const val KEY_SAVED_STATE = "saved_state"

        const val FRAG_HOME = "home_frag"
        const val FRAG_BOOK = "explore_frag"
        const val FRAG_DISCOVER = "discover_frag"
        const val FRAG_ACCOUNT = "more_frag"
        const val FRAG_CONNECT = "connect-frag"
    }
}