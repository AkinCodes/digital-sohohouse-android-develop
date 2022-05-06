package com.sohohouse.seven.perks.details

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.R.string
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem.DiscoverPerks.PerksItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.discover.benefits.BenefitsRepo
import com.sohohouse.seven.network.core.models.Body
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetPerkDetailsRequest
import com.sohohouse.seven.network.core.request.GetPerksRequest
import com.sohohouse.seven.perks.details.adapter.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class PerksDetailViewModel @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val venueRepo: VenueRepo,
    private val stringProvider: StringProvider,
    private val benefitsRepo: BenefitsRepo,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private var venues: List<Venue> = listOf()

    private val _items = MutableLiveData<List<PerksDetailItem>>()
    val items: LiveData<List<PerksDetailItem>>
        get() = _items

    private val _clipboard = MutableLiveData<PerkDetailClipboardItem>()
    val clipboard: LiveData<PerkDetailClipboardItem>
        get() = _clipboard

    @SuppressLint("CheckResult")
    fun fetchData(perksId: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            venues = venueRepo.venues()

            val perk = fetchPerkDetails(perksId) ?: return@launch
            val morePerks = getMorePerks(perk.city)

            if (perk.promotionCode?.isNotBlank() == true || perk.perkUrl?.isNotBlank() == true) {
                _clipboard.postValue(PerkDetailClipboardItem(perk))
            }
            _items.postValue(mutableListOf<PerksDetailItem>().apply {
                addAll(createPerkAdapterList(perk))
                if (morePerks.isNotEmpty()) {
                    addAll(createMoreBenefits(morePerks, perksId))
                }
            })
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun createMoreBenefits(perks: List<Perk>, perksId: String): List<PerksDetailItem> {
        return listOf(MorePerks(perks.filter { it.id != perksId }.take(10).map { PerksItem(it) }))
    }

    private fun fetchPerkDetails(id: String): Perk? {
        return zipRequestsUtil.issueApiCall(GetPerkDetailsRequest(id)).fold(
            ifError = { onError(Throwable(it.toString())); null },
            ifEmpty = { null },
            ifValue = { it }
        )
    }

    private fun getMorePerks(city: String?): List<Perk> {
        return benefitsRepo.getPerks(perPage = GetPerksRequest.PERKS_DETAIL_PER_PAGE, cities = city)
            .fold(
                ifError = { onError(Throwable(it.toString())); emptyList() },
                ifEmpty = { emptyList() },
                ifValue = { it }
            )
    }

    private fun createPerkAdapterList(item: Perk): List<PerksDetailItem> {
        val items = mutableListOf<PerksDetailItem>()

        items.add(PerksDetailHeaderImage(item.headerImageLarge ?: ""))
        items.add(
            PerksDetailHeader(
                item.id,
                item.city,
                item.contentPillar,
                item.onlineOnly,
                item.title ?: "",
                item.summary ?: "",
                item.promotionCode,
                item.bodyImageLarge,
                item.shortDescription,
                item.expiresOn,
                datePlaceholder = R.string.perks_expires_label
            )
        )

        items.add(
            PerksDetailBody(
                Body(
                    item.body ?: "",
                    item.bodyTitle ?: "",
                    item.bodyImageSmall,
                    item.bodyImageMedium,
                    item.bodyImageXlarge,
                    item.bodyImageAltText ?: "",
                    item.bodyImageCaption ?: "",
                    ""
                )
            )
        )

        if (!item.termsAndConditions.isNullOrEmpty()) {
            items.add(
                PerksDetailBody(
                    Body(
                        item.termsAndConditions ?: "",
                        stringProvider.getString(string.perks_terms_label),
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                )
            )
        }

        return items
    }

    fun logPerksVisitSite(id: String, name: String, url: String) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PerksVisitSite,
            AnalyticsManager.Perks.getVisitEventParams(id, name, url)
        )
    }

    fun logPerksCopyCode(id: String, name: String, promoCode: String) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PerksCopyCode,
            AnalyticsManager.Perks.getPromoCodeParams(id, name, promoCode)
        )
    }

}