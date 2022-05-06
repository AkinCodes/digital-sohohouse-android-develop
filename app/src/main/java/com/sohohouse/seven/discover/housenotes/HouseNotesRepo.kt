package com.sohohouse.seven.discover.housenotes

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.sitecore.models.template.Template

interface HouseNotesRepo {
    fun getAll(top: Int = 10, skip: Int = 0): Either<ServerError, List<Template>>
}
