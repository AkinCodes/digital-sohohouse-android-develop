package com.sohohouse.seven.housepay.discounts

import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.housepay.HousePayConstants
import com.sohohouse.seven.network.core.models.housepay.Check
import javax.inject.Inject

interface DiscountManager {
    fun useCheck(check: Check)
    val shouldShowU27DiscountBanner: Boolean
    val shouldApplyDiscount: Boolean
}

class DiscountManagerImpl @Inject constructor(
    private val userManager: UserManager
) : DiscountManager {

    private lateinit var check: Check

    override fun useCheck(check: Check) {
        this.check = check
    }

    override val shouldShowU27DiscountBanner: Boolean
        get() {
            return isU27member && isU27DiscountAvailableInLocation
        }

    private val isU27member: Boolean
        get() {
            return userManager.membershipType == MembershipType.U27.name
        }

    private val isU27DiscountAvailableInLocation: Boolean
        get() {
            val revenueCenterId = check.revenueCenterId?.nullIfBlank() ?: return false
            return check
                .location
                ?.discountPriorities
                ?.get(revenueCenterId)
                ?.contains("U27") ?: false
        }

    /// We want to prevent the U27 discount from being applied when the covers are more than 4
    override val shouldApplyDiscount: Boolean
        get() {
            return (!isU27member || !isU27DiscountAvailableInLocation || isU27DiscountAvailable)
                    && check.discountsTotal == 0
        }

    private val isU27DiscountAvailable: Boolean
        get() {
            return isU27member
                    && isCheckEligibleForU27Discount
                    && isU27DiscountAvailableInLocation
        }

    private val isCheckEligibleForU27Discount: Boolean
        get() {
            return (check.covers?.toIntOrNull() ?: 0) <= HousePayConstants.u27MaxNumberOfCovers
        }
}