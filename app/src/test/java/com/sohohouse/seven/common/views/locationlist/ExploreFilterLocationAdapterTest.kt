package com.sohohouse.seven.common.views.locationlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.base.filter.types.location.ExploreFilterLocationAdapter
import com.sohohouse.seven.common.house.HouseRegion
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class ExploreFilterLocationAdapterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var data: MutableList<LocationRecyclerParentItem>
    lateinit var childData: MutableList<LocationRecyclerChildItem>

    @Before
    fun setUp() {
        data = mutableListOf()
        childData = mutableListOf()
        childData.add(
            LocationRecyclerChildItem(
                "0",
                "Dumbo House",
                "https://placebear.com/24/24",
                true
            )
        )
        childData.add(
            LocationRecyclerChildItem(
                "1",
                "Ludlow House",
                "https://placebear.com/24/24",
                false
            )
        )
        childData.add(
            LocationRecyclerChildItem(
                "2",
                "Soho House Chicago",
                "https://placebear.com/24/24",
                true
            )
        )
        val childData2 = ArrayList<LocationRecyclerChildItem>()
        childData2.add(
            LocationRecyclerChildItem(
                "3",
                "Soho House West Hollywood",
                "https://placebear.com/24/24",
                true
            )
        )
        childData2.add(
            LocationRecyclerChildItem(
                "4",
                "Soho Beach House",
                "https://placebear.com/24/24",
                false
            )
        )
        childData2.add(
            LocationRecyclerChildItem(
                "5",
                "Little Beach House Malibu",
                "https://placebear.com/24/24",
                false
            )
        )
        data.add(LocationRecyclerParentItem(HouseRegion.EUROPE, childData, false))
        data.add(LocationRecyclerParentItem(HouseRegion.NORTH_AMERICA, childData2, false))
        data.add(LocationRecyclerParentItem(HouseRegion.CWH, mutableListOf(), false))
    }

    @Test
    fun `adapter has correct number of items when collapsed`() {
        // GIVEN the adapter has a list of 3 parent items for all houses and no houses for user
        // WHEN the adapter is created
        val adapter = createAdapter(data, mutableListOf())

        // THEN the adapter has 5 items: 2 headers, and 3 regions
        assertEquals(5, adapter.itemCount)
    }

    @Test
    fun `adapter has correct number of items when some parents are expanded`() {
        // GIVEN the adapter has a list of 3 parent items, the first parent is expanded and no houses for user
        data[0].expanded = true

        // WHEN the adapter is created
        val adapter = createAdapter(data, mutableListOf())

        // THEN the adapter has 8 items: 2 headers, 3 regions, 3 houses
        assertEquals(8, adapter.itemCount)
    }

    @Test
    fun `adapter uses correct item types`() {
        // GIVEN the adapter has a list of 3 parent items and the first parent is expanded
        data[0].expanded = true

        // WHEN the adapter is created
        val adapter = createAdapter(data, mutableListOf())

        // THEN the 1st item is of type HEADER_CARD, 3rd is of type PARENT, 4th is of type CHILD
        val headerItemType = adapter.getItemViewType(0)
        assertEquals(FilterItemType.HEADER.ordinal, headerItemType)
        val parentItemType = adapter.getItemViewType(2)
        assertEquals(FilterItemType.PARENT.ordinal, parentItemType)
        val childItemType = adapter.getItemViewType(3)
        assertEquals(FilterItemType.CHILD.ordinal, childItemType)
    }

    @Test
    fun `adapter has correct number of selected items`() {
        // GIVEN the adapter has a list of 3 parent items for all houses and 3 houses for user,
        // 2 of the user's houses were selected and 1 in all houses is selected
        // WHEN the adapter is created
        val adapter = createAdapter(data, childData)

        // THEN the adapter has 3 selected houses
        assertEquals(3, adapter.selectedItemList.size)
    }

    private fun createAdapter(
        data: MutableList<LocationRecyclerParentItem>,
        childData: MutableList<LocationRecyclerChildItem>
    ): ExploreFilterLocationAdapter {
        return ExploreFilterLocationAdapter(data, childData, object : FilterListener {
            override fun getSelectedLocations(): List<String> = listOf()

            override fun onCategorySelectionUpdated(selectedItems: List<String>) {
                // do nothing
            }

            override fun getSelectedCategories(): List<String> = listOf()

            override fun getAllCategories(): List<CategoryDataItem> = listOf()

            override fun onSelectedDateChanged(date: Date?, isStart: Boolean) {
                // do nothing
            }

            override fun getSelectedStartDate(): Date? = null

            override fun getSelectedEndDate(): Date? = null

            override fun getFavouriteHousesData(): MutableList<LocationRecyclerChildItem> =
                mutableListOf()

            override fun getAllHousesData(): MutableList<LocationRecyclerParentItem> =
                mutableListOf()

            override fun onSelectedLocationsChanged(locationList: List<String>) {
                // do nothing
            }
        })
    }
}