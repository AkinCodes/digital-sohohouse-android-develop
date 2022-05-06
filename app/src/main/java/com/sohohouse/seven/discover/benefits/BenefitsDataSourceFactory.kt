package com.sohohouse.seven.discover.benefits

import androidx.paging.DataSource
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.discover.benefits.adapter.PerksItem
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager

class BenefitsDataSourceFactory(
    private val regionFilterManager: RegionFilterManager,
    private val benefitsFilterManager: BenefitsFilterManager,
    private val venueRepo: VenueRepo,
    private val exploreFactory: ExploreListFactory,
    private val repo: BenefitsRepo,
    private val featureFlags: FeatureFlags
) : DataSource.Factory<Int, PerksItem>(),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private var dataSource: DataSource<Int, PerksItem>? = null

    override fun create(): DataSource<Int, PerksItem> {
        return BenefitsDataSource(
            regionFilterManager = regionFilterManager,
            citiesFilterManager = benefitsFilterManager,
            venueRepo = venueRepo,
            exploreFactory = exploreFactory,
            repo = repo,
            featureFlags = featureFlags,
            loadable = this,
            errorable = this
        ).also {
            dataSource = it
        }
    }

    fun invalidate() {
        dataSource?.invalidate()
    }
}