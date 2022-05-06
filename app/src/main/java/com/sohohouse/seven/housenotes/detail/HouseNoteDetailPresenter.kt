package com.sohohouse.seven.housenotes.detail

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.book.eventdetails.EventDetailsPresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.housenotes.detail.model.*
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.GetHouseNoteDetailsRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import moe.banana.jsonapi2.HasOne
import timber.log.Timber
import javax.inject.Inject


open class HouseNoteDetailPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val houseManager: HouseManager,
    private val venueRepo: VenueRepo,
    private val analyticsManager: AnalyticsManager
) :
    BasePresenter<HouseNoteDetailViewController>(),
    PresenterLoadable<HouseNoteDetailViewController>,
    ErrorViewStatePresenter<HouseNoteDetailViewController> {
    var isCityGuide: Boolean = false
    lateinit var noteID: String
    private var venuesList = VenueList.empty()

    override fun onAttach(
        view: HouseNoteDetailViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)

        if (isCityGuide) view.setScreenName(AnalyticsManager.Screens.CityGuide.name) else view.setScreenName(
            AnalyticsManager.Screens.HouseNotesSingleContent.name
        )

        if (isFirstAttach) {
            fetchData()
        }
    }

    @SuppressLint("CheckResult")
    open fun fetchData() {
        Single.just(venueRepo.venues())
            .flatMap {
                venuesList = it
                return@flatMap zipRequestsUtil.issueApiCall(GetHouseNoteDetailsRequest(noteID))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        Timber.tag(EventDetailsPresenter.TAG).d(it.error.toString())
                    }

                    is Either.Value -> {
                        executeWhenAvailable { view, _, _ ->
                            view.onDataReady(
                                createAdapterList(
                                    it.value,
                                    venuesList
                                )
                            )
                        }
                    }
                }
            })

    }

    private fun createAdapterList(
        houseNote: HouseNotes,
        venueList: VenueList
    ): List<HouseNoteDetailBaseItem> {
        val data = mutableListOf<HouseNoteDetailBaseItem>()
        val venue = venueList.findById(houseNote.venues.get().firstOrNull()?.id)
        val venueRegionName = if (houseNote.venues.get().size > 1) houseNote.region else venue?.name

        data.add(HouseNoteDetailHeaderImageItem(houseNote.headerImageLargePng))

        data.add(
            HouseNoteDetailHeaderCardItem(
                venueRegionName ?: "",
                houseNote.title,
                houseNote.headerLine,
                houseNote.author,
                houseNote.publishDate
            )
        )

        houseNote.sections.forEach {
            when (it.type) {
                HOUSE_NOTES_SECTION -> {
                    val houseNotesSection = HasOne<HouseNotesSection>(it).get(houseNote.document)
                    data.add(
                        HouseNoteDetailBodyItem(
                            Body(
                                houseNotesSection.textBlock,
                                houseNotesSection.imageTitle,
                                houseNotesSection.imageUrlSmallPng,
                                houseNotesSection.imageUrlMediumPng,
                                houseNotesSection.imageUrlLargePng,
                                houseNotesSection.imageAltText,
                                houseNotesSection.imageCaption,
                                houseNotesSection.pullQuote
                            )
                        )
                    )
                }
            }
        }

        return data
    }

    override fun reloadDataAfterError() {
        fetchData()
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
