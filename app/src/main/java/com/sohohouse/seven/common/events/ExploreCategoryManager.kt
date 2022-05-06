package com.sohohouse.seven.common.events

import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetEventsCategoriesRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreCategoryManager @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val zipRequestsUtilV2: com.sohohouse.seven.common.utils.ZipRequestsUtil
) {

    private var categories: List<EventCategory>? = null

    fun getCategories(): Single<Either<ServerError, List<EventCategory>>> {
        categories?.let { return getCategoriesInternal(it).subscribeOn(Schedulers.io()) }
        return zipRequestsUtil.issueApiCall(GetEventsCategoriesRequest())
            .flatMap {
                if (it is Either.Value) {
                    categories = it.value
                }
                return@flatMap Single.just(it)
            }
    }

    fun getCategoriesV2(): Either<ServerError, List<EventCategory>> {
        categories?.let { return value(it) }
        return zipRequestsUtilV2.issueApiCall(GetEventsCategoriesRequest())
            .apply {
                fold(ifValue = { this@ExploreCategoryManager.categories = it }, ifEmptyOrError = {})
            }

    }

    private fun getCategoriesInternal(categoryList: List<EventCategory>): Single<Either<ServerError, List<EventCategory>>> {
        return Single.just(value(categoryList))
    }

    fun clearData() {
        categories = null
    }
}