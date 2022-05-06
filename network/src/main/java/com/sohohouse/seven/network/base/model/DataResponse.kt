package com.sohohouse.seven.network.base.model

import com.sohohouse.seven.network.base.error.ServerError
import io.reactivex.Single

/**
 * Refer Scala - https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Either.scala
 */
@Deprecated("This class is deprecated. Try to use ApiResponse with SohoApiService")
sealed class Either<out E, out V> {

    /**
     * Returns `true` if this is a [Value], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isValue: Boolean

    /**
     * Returns `true` if this is a [Error], `false` otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isError: Boolean

    /**
     * Applies `ifError` if this is a [Error] or `ifValue` if this is a [Value].
     *
     * Example:
     * ```
     * val result: Either<Exception, Value> = possiblyFailingOperation()
     * result.fold(
     *      { log("operation failed with $it") },
     *      { log("operation succeeded with $it") }
     * )
     * ```
     *
     * @param ifError the function to apply if this is a [Error]
     * @param ifValue the function to apply if this is a [Value]
     * @return the results of applying the function
     */
    inline fun <C> fold(ifError: (E) -> C, ifValue: (V) -> C, ifEmpty: () -> C): C = when (this) {
        is Value -> ifValue(value)
        is Error -> ifError(error)
        is Empty -> ifEmpty()
    }

    inline fun <C> fold(ifValue: (V) -> C, ifEmptyOrError: (E?) -> C): C = when (this) {
        is Value -> ifValue(value)
        is Error -> ifEmptyOrError(error)
        is Empty -> ifEmptyOrError(null)
    }

    inline fun ifValue(ifValue: (V) -> Unit): Either<E, V> {
        if (this is Value) {
            ifValue(this.value)
        }
        return this
    }

    inline fun ifError(ifError: (E) -> Unit): Either<E, V> {
        if (this is Error) {
            ifError(this.error)
        }
        return this
    }

    inline fun ifEmpty(ifEmpty: () -> Unit): Either<E, V> {
        if (this is Empty) {
            ifEmpty()
        }
        return this
    }

    data class Error<out E>(
        val error: E,
        override val isValue: Boolean = false,
        override val isError: Boolean = true,
        val detail: String = "",
    ) : Either<E, Nothing>()

    data class Value<out V>(
        val value: V,
        override val isValue: Boolean = true,
        override val isError: Boolean = false,
    ) : Either<Nothing, V>()

    class Empty(override val isValue: Boolean = false, override val isError: Boolean = false) :
        Either<Nothing, Nothing>()
}

fun empty(): Either<Nothing, Nothing> = Either.Empty()
fun <V> value(value: V): Either<ServerError, V> = Either.Value(value)
fun <E> error(value: E): Either<E, Nothing> = Either.Error(value)

fun <Input, Output, Error> Single<Either<Error, Input>>.flatMapValue(
    ifValue: (Input) -> Single<Either<Error, Output>>,
): Single<Either<Error, Output>> = flatMap {
    when (it) {
        is Either.Empty -> Single.just(empty())
        is Either.Error -> Single.just(error(it.error))
        is Either.Value -> ifValue(it.value)
    }
}

fun <Input, Output> Single<Either<ServerError, Input>>.mapValue(
    ifValue: (Input) -> Output,
): Single<Either<ServerError, Output>> = map {
    when (it) {
        is Either.Empty -> empty()
        is Either.Error -> error(it.error)
        is Either.Value -> value(ifValue(it.value))
    }
}