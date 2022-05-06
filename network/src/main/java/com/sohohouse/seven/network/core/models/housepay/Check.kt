package com.sohohouse.seven.network.core.models.housepay

import com.sohohouse.seven.network.core.models.Venue
import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "checks")
data class Check(
    @field:Json(name = "line_items") val lineItems: List<LineItemDTO> = emptyList(),
    @field:Json(name = "check_number") val checkNumber: String? = null,
    @field:Json(name = "waiter_id") val waiterId: String? = null,
    @field:Json(name = "currency") val currency: String? = null,
    @field:Json(name = "remaining_cents") val _remainingCents: Int? = null,
    @field:Json(name = "total_cents") val totalCents: Int = 0,
    @field:Json(name = "net_cents") val netCents: Int? = null,
    @field:Json(name = "line_items_tax_cents") val lineItemsTaxCents: Int? = null,
    @field:Json(name = "vat_items") val vatItems: List<VATitem>? = emptyList(),
    @field:Json(name = "extra_tax_cents") val extraTaxCents: Int? = null,
    @field:Json(name = "gratuities_cents") private val _gratuitiesCents: Int? = null,
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "discounts") val discounts: List<Discount>? = emptyList(),
    @field:Json(name = "other_payments") private val _otherPayments: List<Payment>? = emptyList(),
    @field:Json(name = "created_at") val createdAt: Date? = null,
    @field:Json(name = "updated_at") val updatedAt: Date? = null,
    @field:Json(name = "paid_at") val paidAt: Date? = null,
    @field:Json(name = "location_name") val locationName: String? = null,
    @field:Json(name = "location_code") val locationCode: String? = null,
    @field:Json(name = "variable_tips_enabled") private val _variableTipsEnabled: Boolean? = null,
    @field:Json(name = "subtotal") private val _subtotal: Int? = null,
    @field:Json(name = "credit_cents") private val _creditCents: Int? = null,
    @field:Json(name = "global_id") val globalId: String? = null,
    @field:Json(name = "covers") val covers: String? = null,
    @field:Json(name = "revenue_center_id") val revenueCenterId: String? = null,
    @field:Json(name = "reason") val reason: String? = null,
    @field:Json(name = "location") private val _location: HasOne<Location>? = null,
    @field:Json(name = "venue") private val _venue: HasOne<Venue>? = null,
    @field:Json(name = "payments") private val _payments: HasMany<Payment>? = null,
) : Resource(), Serializable {

    companion object {
        const val STATUS_OPEN = "open"
        const val STATUS_CLOSED = "closed"
        const val STATUS_PAID = "paid"
        const val STATUS_MERGED = "merged"
        const val STATUS_TRANSFERRED = "transferred"
    }

    val variableTipsEnabled: Boolean
        get() = _variableTipsEnabled ?: false

    val gratuitiesCents: Int
        get() = _gratuitiesCents ?: 0

    val discountsTotal: Int
        get() = discounts
            ?.sumOf { it.cents }
            ?: 0

    val subtotal: Int
        get() = _subtotal ?: 0

    val groupedItems: List<RevenuCenterItems>
        get() {

            val items = mutableListOf<LineItemDTO>()

            this.lineItems.forEach { item ->
                for (i in 0 until (item.quantity ?: 0)) {
                    val itemCopy = LineItemDTO(
                        name = item.name,
                        cents = item.cents?.div(item.quantity ?: 1),
                        quantity = 1,
                        revenueCenter = item.revenueCenter,
                        category = item.category
                    )
                    items.add(itemCopy)
                }
            }

            val groupedItems = mutableListOf<RevenuCenterItems>()
            val revenueCenterGroups: Map<String?, List<LineItemDTO>> =
                items.groupBy { it.revenueCenter }

            revenueCenterGroups.forEach {
                val (revenueCenter, lineItems) = it
                val itemCounts = lineItems
                    .groupingBy { lineItem -> lineItem }
                    .eachCount()

                val updatedItems = itemCounts.map { (item, count) ->
                    item.copy(quantity = count)
                }.sortedBy { lineItem -> lineItem.name }

                val totalItem = RevenuCenterItems(revenueCenter ?: "", updatedItems)
                groupedItems.add(totalItem)
            }

            return groupedItems.sortedBy { it.name }
        }

    val foodSubtotal: Int
        get() {
            return lineItems
                .filter { it.category == LineItemDTO.CATEGORY_FOOD }
                .map { it.cents }
                .reduce { lhs, rhs ->
                    return@reduce (lhs ?: 0) + (rhs ?: 0)
                } ?: 0
        }

    val beverageSubtotal: Int
        get() {
            return lineItems
                .filter { it.category == LineItemDTO.CATEGORY_BEVERAGE }
                .map { it.cents }
                .reduce { lhs, rhs ->
                    return@reduce (lhs ?: 0) + (rhs ?: 0)
                } ?: 0
        }

    val isEmpty: Boolean
        get() = totalCents == 0 ||    //TODO uncomment
                lineItems.isEmpty()

    val location: Location?
        get() = _location?.get(document)

    val amountPaidByOthers: Int
        get() {
            return otherPayments.sumOf { return@sumOf it.cents }
        }

    val remainingCents: Int get() = _remainingCents ?: 0

    val payments: List<Payment>
        get() = _payments?.get(document) ?: emptyList()

    val venue: Venue?
        get() = _venue?.get(document)

    val paymentsTipsTotal: Int
        get() = payments.sumOf { it.tipCents ?: 0 }

    val creditCents: Int get() = _creditCents ?: 0

    val otherPayments: List<Payment>
        get() = _otherPayments ?: emptyList()
}

data class RevenuCenterItems(
    val name: String,
    val items: List<LineItemDTO>,
)

data class LineItemDTO(
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "cents") val cents: Int? = null,
    @field:Json(name = "category") val category: String? = null,
    @field:Json(name = "revenue_center") val revenueCenter: String? = null,
    @field:Json(name = "quantity") val quantity: Int? = null,
) : Serializable {

    companion object {
        const val CATEGORY_FOOD = "food"
        const val CATEGORY_BEVERAGE = "beverage"
    }

    //exclude quantity from equation
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LineItemDTO

        if (name != other.name) return false
        if (cents != other.cents) return false
        if (category != other.category) return false
        if (revenueCenter != other.revenueCenter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (cents ?: 0)
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (revenueCenter?.hashCode() ?: 0)
        return result
    }
}

class Discount(
    @field:Json(name = "type") val type: String,
    @field:Json(name = "cents") val cents: Int,
) : Serializable


class VATitem(
    @field:Json(name = "percentage") val percentage: Float? = null,
    @field:Json(name = "cents") val cents: Int? = null,
) : Serializable

@JsonApi(type = "payments")
class Payment(
    @field:Json(name = "cents") private val _cents: Int? = null,
    @field:Json(name = "check_id") val checkId: String? = null,
    @field:Json(name = "card_id") val cardId: String? = null,
    @field:Json(name = "tip_cents") val tipCents: Int? = null,
    @field:Json(name = "last_four") val lastFour: String? = null,
    @field:Json(name = "card_type") val cardType: String? = null,
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "transaction_id") val transactionId: String? = null,
    @field:Json(name = "error_message") val errorMessage: String? = null,
    @field:Json(name = "created_at") val createdAt: String? = null,
    @field:Json(name = "updated_at") val updatedAt: String? = null,
    @field:Json(name = "payment_type") val paymentType: String? = null,
    @field:Json(name = "walkout") val walkout: Boolean? = null,
    @field:Json(name = "check") val checkResource: HasOne<Check>? = null,
) : Resource(), Serializable {

    companion object {
        const val TYPE_CARD = "card"
        const val TYPE_HOUSE_CREDIT = "credit"
        const val STATUS_PAID = "paid"
    }

    val check: Check?
        get() = checkResource?.get(document)

    val cents get() = _cents ?: 0
}



