package com.sohohouse.seven.home.repo

import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.split
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface HousePayBannerDelegate {
    fun housePayBannerFlow(): Flow<List<Banner>>
    suspend fun getHousePayBanner(): Banner?
}

class HousePayBannerDelegateImpl @Inject constructor(
    private val checkRepo: CheckRepo,
    private val featureFlags: FeatureFlags,
    private val userManager: UserManager,
    private val stringProvider: StringProvider,
    private val prefsManager: PrefsManager
) : HousePayBannerDelegate {

    companion object {
        const val TYPE_OPEN_CHECK = "TYPE_OPEN_CHECK"
        const val TYPE_TS_AND_CS_NOT_ACCEPTED = "TYPE_TS_AND_CS_NOT_ACCEPTED"
        const val TYPE_CARD_NOT_ADDED = "TYPE_CARD_NOT_ADDED"
        const val TYPE_WALLET_NOT_CREATED = "TYPE_WALLET_NOT_CREATED"
        const val TYPE_RECENTLY_COSED_CHECK = "TYPE_RECENTLY_COSED_CHECK"
        const val TYPE_MULTIPLE_OPEN_CHECKS = "TYPE_MULTIPLE_OPEN_CHECKS"
    }

    override fun housePayBannerFlow(): Flow<List<Banner>> {
        return flow {
            emit(emptyList())
            val banner = getHousePayBanner()
            emit(listOfNotNull(banner))
        }
    }

    override suspend fun getHousePayBanner(): Banner? {
        val checks: List<Check> = checkRepo.getChecks(
            page = 1,
            status = CheckRepo.OPEN_CHECKS_STATUS_FILTER,
        ).split(
            ifSuccess = { checks ->
                checks
            },
            ifError = {
                emptyList()
            }
        )

        val housePayEnabled = featureFlags.housePay

        val banner = when {
            housePayEnabled && !userManager.didConsentTermsConditions ->
                buildTermsNotAcceptedItem()

            //TODO housePayEnabled and membership card not added to Google Pay

            //TODO housePayEnabled and Payment method not added or 3C Wallet not created

            //TODO house pay enabled and Payment method added but 3C Wallet not created

            housePayEnabled && checks.isNotEmpty()
                    && checks.any {
                it.status == Check.STATUS_OPEN
            } -> {
                buildOpenChecksItem(checks)
            }

            housePayEnabled
                    && checks.isNotEmpty()
                    && checks.any {
                it.closedAndNotDismissed
            } -> {
                buildRecentlyClosedCheckItem(checks.first {
                    it.closedAndNotDismissed
                })
            }

            else -> null
        }
        return banner
    }

    private val Check.closedAndNotDismissed: Boolean
        get() {
            return status in arrayOf(Check.STATUS_CLOSED, Check.STATUS_PAID)
                    && prefsManager.dismissedCheckClosedIds.contains(id).not()
        }

    private fun buildRecentlyClosedCheckItem(closedCheck: Check): Banner {
        return Banner(
            id = TYPE_RECENTLY_COSED_CHECK,
            title = stringProvider.getString(R.string.label_house_pay),
            subtitle = stringProvider.getString(R.string.house_pay_banner_closed_check),
            cta = stringProvider.getString(R.string.view_cta),
            checkId = closedCheck.id,
            isSwipeable = true
        )
    }

    private fun buildOpenChecksItem(openChecks: List<Check>): Banner {
        val multiple = openChecks.size > 1
        //TODO uncomment when implementing view multiple checks
//        if (multiple) {
//            return Banner (
//                id = TYPE_MULTIPLE_OPEN_CHECKS,
//                title = stringProvider.getString(R.string.label_house_pay),
//                subtitle = stringProvider.getString(R.string.house_pay_banner_multiple_open_checks),
//                cta = stringProvider.getString(R.string.view_cta),
//            )
//        }
//        else {

        val openCheck = openChecks.first()

        return Banner(
            id = TYPE_OPEN_CHECK,
            title = stringProvider.getString(R.string.label_house_pay),
            subtitle = stringProvider.getString(
                R.string.house_pay_banner_open_check,
                openCheck.locationName ?: ""
            ),
            cta = stringProvider.getString(R.string.view_cta),
            checkId = openCheck.id,
            isSwipeable = false
        )
//        }
    }

    private fun buildTermsNotAcceptedItem(): Banner {
        TODO("Not yet implemented")
    }

}