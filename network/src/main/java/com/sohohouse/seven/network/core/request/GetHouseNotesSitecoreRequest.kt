package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.sitecore.SitecoreApi
import com.sohohouse.seven.network.sitecore.SitecoreApiRequest
import com.sohohouse.seven.network.sitecore.models.HouseNotesResponse
import com.sohohouse.seven.network.sitecore.models.template.Template.Companion.TEMPLATE_EDITORIAL_STORY_PAGE_ID
import com.sohohouse.seven.network.sitecore.models.template.Template.Companion.TEMPLATE_HOUSE_NOTE_PAGE_ID
import retrofit2.Call

class GetHouseNotesSitecoreRequest(
    private val filter: String = "contains(Path, '/sitecore/content/digital-house/home') and (TemplateId eq $TEMPLATE_EDITORIAL_STORY_PAGE_ID OR TemplateId eq $TEMPLATE_HOUSE_NOTE_PAGE_ID)",
    private val top: Int = MAX_HOUSE_NOTES_PER_PAGE,
    private val skip: Int = 0,
    private val orderBy: String = "created desc",
    private val count: Boolean = true,
    private val expand: String = "FieldValues",
    private val apiKey: String = "4F2D00E8-A2CB-4B6A-87F3-956AB752F41B",
) : SitecoreApiRequest<HouseNotesResponse> {
    override fun createCall(api: SitecoreApi): Call<out HouseNotesResponse> {
        return api.getHouseNotes(filter, top, skip, count, expand, orderBy, apiKey)
    }

    companion object {
        const val MAX_HOUSE_NOTES_PER_PAGE = 10
        const val MAX_RECENT_HOUSE_NOTES_HOME = 6
        const val MAX_RECENT_HOUSE_NOTES = 3
    }
}