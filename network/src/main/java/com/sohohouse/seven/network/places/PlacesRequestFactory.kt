package com.sohohouse.seven.network.places

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.base.request.APIRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException

class PlacesRequestFactory(private val placesApi: PlacesApi) {

    fun create(request: APIRequest<PlacesApi, PlacesResponse>): Single<Either<ServerError, PlacesResponse>> {
        return Single.fromCallable {
            return@fromCallable try {
                val call = request.createCall(placesApi)
                val response = call.execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.status == PlacesResponse.Status.OK || body.status == PlacesResponse.Status.ZERO_RESULTS) {
                        value(body)
                    } else {
                        com.sohohouse.seven.network.base.model.error(ServerError.ApiError("${body.status.name}: ${body.errorMessage}"))
                    }
                } else if (response.isSuccessful && body == null) {
                    empty()
                } else {
                    response.errorBody()?.let {
                        return@fromCallable com.sohohouse.seven.network.base.model.error(request.mapError(
                            response.code(),
                            it.string()))
                    }
                    com.sohohouse.seven.network.base.model.error(ServerError.INVALID_RESPONSE)
                }
            } catch (uhe: UnknownHostException) {
                com.sohohouse.seven.network.base.model.error(ServerError.NO_INTERNET)
            } catch (ste: SocketTimeoutException) {
                com.sohohouse.seven.network.base.model.error(ServerError.TIMEOUT)
            } catch (sslpuve: SSLPeerUnverifiedException) {
                com.sohohouse.seven.network.base.model.error(ServerError.INVALID_CERT)
            } catch (e: Exception) {
                com.sohohouse.seven.network.base.model.error(ServerError.COMPLETE_MELTDOWN)
            }
        }.subscribeOn(Schedulers.io())
    }

}