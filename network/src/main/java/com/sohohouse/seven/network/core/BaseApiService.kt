package com.sohohouse.seven.network.core

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import retrofit2.HttpException
import java.io.IOException
import java.io.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

abstract class BaseApiService(
    private val errorReporter: NetworkErrorReporter,
    private val ioDispatcher: CoroutineDispatcher,
) {

    protected suspend fun <T> apiCall(tag: String = "", apiCall: suspend () -> T): ApiResponse<T> {
        return withContext(ioDispatcher) {
            try {
                ApiResponse.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ApiResponse.Error(-1, "Error")
                    is HttpException -> ApiResponse.Error(
                        throwable.code(),
                        throwable.response()?.message(),
                        parseError(throwable, tag)
                    )
                    else -> ApiResponse.Error()
                }
            }
        }
    }

    private fun parseError(httpException: HttpException, tag: String): ErrorResponse? {
        httpException.response()?.errorBody()?.source()?.let {
            return try {
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                    .adapter(ErrorResponse::class.java)
                    .fromJson(it)
            } catch (exception: Throwable) {
                errorReporter.logException(
                    Exception(
                        """
                        Error parsing network error:
                            ErrorBodySource: ${
                            httpException.response()?.errorBody()?.source()?.toString()
                        }
                            ErrorBody: ${httpException.response()?.errorBody()?.string()}
                            ErrorMessage: ${httpException.message()}
                            ErrorCode: ${httpException.code()}
                            Tag: $tag
                        """,
                        exception
                    )
                )
                ErrorResponse(
                    listOf(
                        Error.FALLBACK
                    )
                )
            }
        }
        return null
    }

    interface NetworkErrorReporter {
        fun logException(exception: Throwable)
    }

}

@JsonApi(type = "error")
data class ErrorResponse(@Json(name = "errors") var errors: List<Error>? = emptyList()) :
    Resource(), Serializable

data class Error(
    @Json(name = "code") var code: String? = "",
    @Json(name = "title") var title: String? = "",
    @Json(name = "detail") var detail: String? = "",
) {
    companion object {
        val FALLBACK = Error(
            code = "",
            title = "Network error",
            detail = "Something went wrong"
        )
    }
}

sealed class ApiResponse<out T> {
    data class Success<out T>(val response: T) : ApiResponse<T>()
    data class Error(
        val code: Int? = null,
        val message: String? = null,
        val response: ErrorResponse? = null,
    ) : ApiResponse<Nothing>() {
        fun firstErrorCode(): String? {
            return response?.errors?.firstOrNull()?.code
        }

        fun allErrorCodes(): Array<String> {
            return response?.errors?.mapNotNull { it.code }?.toTypedArray()
                ?: emptyArray()
        }
    }
}

@OptIn(ExperimentalContracts::class)
fun <T> ApiResponse<T>.isSuccessful(): Boolean {
    contract {
        returns(true) implies (this@isSuccessful is ApiResponse.Success)
        returns(false) implies (this@isSuccessful is ApiResponse.Error)
    }
    return this is ApiResponse.Success
}

@OptIn(ExperimentalContracts::class)
fun <T> ApiResponse<T>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is ApiResponse.Success)
        returns(true) implies (this@isFailure is ApiResponse.Error)
    }
    return this is ApiResponse.Error
}

inline fun <T, R> ApiResponse<T>.split(
    ifSuccess: (T) -> R,
    ifError: (ApiResponse.Error) -> R,
): R {
    return if (this is ApiResponse.Success<T>) {
        ifSuccess(this.response)
    } else {
        ifError(this as ApiResponse.Error)
    }
}

inline fun <T> ApiResponse<T>.doOnComplete(
    ifSuccess: (T) -> Unit,
    ifError: (ApiResponse.Error) -> Unit,
): ApiResponse<T> {
    if (this is ApiResponse.Success<T>) {
        ifSuccess(this.response)
    } else {
        ifError(this as ApiResponse.Error)
    }
    return this
}

inline fun <T> ApiResponse<T>.ifError(
    ifError: (ApiResponse.Error) -> Unit,
): ApiResponse<T> {
    if (this is ApiResponse.Error) {
        ifError(this)
    }
    return this
}

fun <T, R> ApiResponse<T>.map(
    ifSuccess: (T) -> ApiResponse<R>,
    ifError: (ApiResponse.Error) -> ApiResponse<R>,
): ApiResponse<R> {
    return if (this is ApiResponse.Success<T>) {
        ifSuccess(this.response)
    } else {
        ifError(this as ApiResponse.Error)
    }
}

fun <T, R> ApiResponse<T>.map(
    mapper: (T) -> R,
): ApiResponse<R> {
    return if (this is ApiResponse.Success<T>) {
        ApiResponse.Success(mapper.invoke(this.response))
    } else {
        this as ApiResponse.Error
    }
}
