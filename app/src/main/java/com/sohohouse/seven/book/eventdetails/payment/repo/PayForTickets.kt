package com.sohohouse.seven.book.eventdetails.payment.repo

data class PayForTickets(
    val eventId: String,
    val cardId: String,
    val ticketsCount: Int,
    val bookingId: String
)
