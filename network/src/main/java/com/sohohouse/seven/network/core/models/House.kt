package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "houses")
class House : Resource() {
    @field:Json(name = "house_image_set")
    var houseImageSet: ImageSet? = null

    @field:Json(name = "eat_and_drink_image_set")
    var eatAndDrinkImageSet: EADImageSet? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as House

        if (houseImageSet != other.houseImageSet) return false
        if (eatAndDrinkImageSet != other.eatAndDrinkImageSet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (houseImageSet?.hashCode() ?: 0)
        result = 31 * result + (eatAndDrinkImageSet?.hashCode() ?: 0)
        return result
    }


}