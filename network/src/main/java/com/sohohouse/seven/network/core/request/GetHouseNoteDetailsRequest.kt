package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.HouseNotes
import retrofit2.Call

class GetHouseNoteDetailsRequest(private val houseNoteID: String) : CoreAPIRequest<HouseNotes> {
    companion object {
        const val HOUSE_NOTES_SECTIONS_INCLUDE = "sections"
    }

    override fun createCall(api: CoreApi): Call<out HouseNotes> {
        return api.getHouseNoteDetails(houseNoteID, HOUSE_NOTES_SECTIONS_INCLUDE)
    }
}