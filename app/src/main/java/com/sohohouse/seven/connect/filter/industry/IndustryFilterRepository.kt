package com.sohohouse.seven.connect.filter.industry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterItem
import com.sohohouse.seven.connect.filter.base.FilterRepository
import com.sohohouse.seven.connect.filter.base.Filterable


abstract class IndustryFilterRepository : FilterRepository()

class IndustryFilterRepositoryImp(
    private val stringProvider: StringProvider
) : IndustryFilterRepository() {
    override val items: LiveData<List<Filterable>> = loadIndustries()

    private fun loadIndustries(): LiveData<List<Filterable>> {
        val items = getAvailableItems()
            .map {
                FilterItem(
                    Filter(id = it.id, title = stringProvider.getString(it.title)),
                    enabled = true,
                    tag = ""
                )
            }
        return MutableLiveData(items)
    }

    private fun getAvailableItems(): List<Industry> {
        return listOf(
            Industry.Architecture,
            Industry.Art,
            Industry.Aviation,
            Industry.CivilServices,
            Industry.Construction,
            Industry.Digital,
            Industry.Education,
            Industry.Energy,
            Industry.Environment,
            Industry.Fashion,
            Industry.Film,
            Industry.Finance,
            Industry.FoodAndBeverage,
            Industry.GraphicDesign,
            Industry.HealthAndWellness,
            Industry.Hospitality,
            Industry.Jewellery,
            Industry.Law,
            Industry.Literature,
            Industry.Manufacturing,
            Industry.MediaAndEntertainment,
            Industry.Music,
        )
    }
}