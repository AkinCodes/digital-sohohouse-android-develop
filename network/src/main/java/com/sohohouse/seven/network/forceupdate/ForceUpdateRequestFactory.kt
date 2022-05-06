package com.sohohouse.seven.network.forceupdate

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.request.APIRequest
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException

class ForceUpdateRequestFactory(private val forceUpdateApi: ForceUpdateApi) {
    fun create(request: APIRequest<ForceUpdateApi, Void>): Either<ServerError, Any> {
        return try {
            val call = request.createCall(forceUpdateApi)
            val response = call.execute()
            if (response.isSuccessful) {
                Either.Value(Any())
            } else {
                response.errorBody()
                    ?.let { error(request.mapError(response.code(), it.string())) }
                    ?: error(ServerError.INVALID_RESPONSE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is UnknownHostException -> Either.Error(ServerError.NO_INTERNET)
                is SocketTimeoutException -> Either.Error(ServerError.TIMEOUT)
                is SSLPeerUnverifiedException -> Either.Error(ServerError.INVALID_CERT)
                else -> Either.Error(ServerError.COMPLETE_MELTDOWN)
            }
        }
    }
}