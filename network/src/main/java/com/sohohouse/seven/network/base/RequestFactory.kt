package com.sohohouse.seven.network.base

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.base.error.ErrorDetailExtractor
import com.sohohouse.seven.network.base.error.ErrorDetailExtractorImpl
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.base.request.APIRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException

abstract class RequestFactory<API>(
    private val api: API,
    private val idlingResource: CountingIdlingResource,
    private val errorDetailExtractor: ErrorDetailExtractor = ErrorDetailExtractorImpl(),
) {

    @Deprecated("Use createV2 with Kotlin Coroutine")
    fun <S> create(request: APIRequest<API, S>): Single<Either<ServerError, S>> {
        idlingResource.increment()
        return Single.fromCallable<Either<ServerError, S>> {
            return@fromCallable try {
                val call = request.createCall(api)
                val response = call.execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    value(body)
                } else if (response.isSuccessful && body == null) {
                    empty()
                } else {
                    response.errorBody()?.let {
                        val errorBody = it.string()
                        return@fromCallable Either.Error(
                            error = request.mapError(response.code(), errorBody),
                            detail = getErrorDetails(errorBody)
                        )
                    }
                    Either.Error(ServerError.INVALID_RESPONSE)
                }
            } catch (e: UnknownHostException) {
                Either.Error(ServerError.NO_INTERNET)
            } catch (e: SocketTimeoutException) {
                Either.Error(ServerError.TIMEOUT)
            } catch (e: SSLPeerUnverifiedException) {
                Either.Error(ServerError.INVALID_CERT)
            } catch (e: Exception) {
                e.printStackTrace()
                Either.Error(ServerError.COMPLETE_MELTDOWN)
            } finally {
                idlingResource.decrement()
            }
        }.subscribeOn(Schedulers.io())
    }

    //FIXME this should be a suspend function as it executes API call synchronously and is intended
    //for coroutine use
    fun <S> createV2(request: APIRequest<API, S>): Either<ServerError, S> {
        idlingResource.increment()
        return try {
            val call = request.createCall(api)
            val response = call.execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                value(body)
            } else if (response.isSuccessful && body == null) {
                empty()
            } else {
                response.errorBody()?.let { errorBody ->
                    return Either.Error(request.mapError(response.code(), errorBody.string()))
                        .also { Timber.tag(TAG).d(it.error.toString()) }
                }
                com.sohohouse.seven.network.base.model.error(ServerError.INVALID_RESPONSE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is UnknownHostException -> Either.Error(ServerError.NO_INTERNET)
                is SocketTimeoutException -> Either.Error(ServerError.TIMEOUT)
                is SSLPeerUnverifiedException -> Either.Error(ServerError.INVALID_CERT)
                else -> Either.Error(ServerError.COMPLETE_MELTDOWN)
            }.also { Timber.tag(TAG).d(it.error.toString()) }
        } finally {
            idlingResource.decrement()
        }
    }

    private fun getErrorDetails(body: String): String {
        return errorDetailExtractor.extractErrorDetails(body)
    }

    companion object {
        private const val TAG = "CoreRequestFactory"
    }
}

