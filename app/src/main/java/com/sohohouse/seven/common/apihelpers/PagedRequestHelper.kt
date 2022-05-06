package com.sohohouse.seven.common.apihelpers

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.request.CoreAPIRequestPagable
import io.reactivex.Single

/**
 * The helper class will only associate with one request.  If you need to track multiple requests,
 * use distinct instances.
 */
class PagedRequestHelper<SUCCESS, REQUEST : CoreAPIRequestPagable<SUCCESS>> {

    companion object {
        private const val FIRST_PAGE = 1
        private const val PER_PAGE = 10
    }

    private var lastPage: Int = FIRST_PAGE

    var areMorePageAvailable = true
        private set

    lateinit var request: REQUEST
        private set

    val canFetchMorePages: Boolean
        get() = areMorePageAvailable && ::request.isInitialized

    fun prepareForPageOne(request: REQUEST, perPage: Int? = PER_PAGE): REQUEST {
        this.request = request
        this.request.page = FIRST_PAGE
        this.request.perPage = perPage
        return this.request
    }

    fun prepareNextRequest(request: REQUEST, response: SUCCESS) {
        val meta = request.getMeta(response) ?: kotlin.run {
            areMorePageAvailable = false
            return
        }

        lastPage = meta.totalPages
        val newPage = meta.page + 1
        this.request.page = newPage
        areMorePageAvailable = newPage <= lastPage
    }


    fun trackPaging(stream: Single<Either<ServerError, SUCCESS>>): Single<Either<ServerError, SUCCESS>> {
        return stream.map {
            when (it) {
                is Either.Value -> {
                    prepareNextRequest(request, it.value)
                }
            }
            return@map it
        }
    }

    fun trackPagingV2(either: Either<ServerError, SUCCESS>): Either<ServerError, SUCCESS> {
        if (either is Either.Value) prepareNextRequest(request, either.value)
        return either
    }
}