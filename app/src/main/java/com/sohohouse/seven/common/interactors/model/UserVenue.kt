package com.sohohouse.seven.common.interactors.model

import java.io.Serializable

data class UserVenue(
    private val _id: String? = null,
    private val _name: String? = null,
    private val _venueIconURL: String? = null,
    private val _venueColor: String? = null
) : Serializable {
    val id: String
        get() = _id ?: ""
    val name: String
        get() = _name ?: ""
    val venueIconURL: String
        get() = _venueIconURL ?: ""
    val venueColor: String
        get() = _venueColor ?: ""
}