package com.sohohouse.seven.common.utils

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import javax.inject.Inject

class ErrorInteractor @Inject constructor() {

    fun <A> oneError(request: Either<ServerError, A>): Either<ServerError, A> {
        return request
    }

    fun <A, B> pairError(request: Either<ServerError, Pair<Either<ServerError, A>, Either<ServerError, B>>>): Either<ServerError, Pair<A, B>> {
        return request.fold(
            ifError = { error(it) },
            ifEmpty = { empty() },
            ifValue = { result ->
                if (result.first is Either.Value && result.second is Either.Value) {
                    value(
                        Pair(
                            (result.first as Either.Value<A>).value,
                            (result.second as Either.Value<B>).value
                        )
                    )
                } else if (result.first is Either.Error) {
                    error((result.first as Either.Error<ServerError>).error)
                } else if (result.second is Either.Error) {
                    error((result.second as Either.Error<ServerError>).error)
                } else {
                    error(ServerError.COMPLETE_MELTDOWN)
                }
            }
        )
    }

    fun <A, B, C> tripError(request: Either<ServerError, Triple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>>>): Either<ServerError, Triple<A, B, C>> {
        return request.fold(
            ifError = { com.sohohouse.seven.network.base.model.error(it) },
            ifEmpty = { empty() },
            ifValue = { result ->
                if (result.first is Either.Value && result.second is Either.Value && result.third is Either.Value) {
                    value(
                        Triple(
                            (result.first as Either.Value<A>).value,
                            (result.second as Either.Value<B>).value,
                            (result.third as Either.Value<C>).value
                        )
                    )
                } else if (result.first is Either.Error) {
                    error((result.first as Either.Error<ServerError>).error)
                } else if (result.second is Either.Error) {
                    error((result.second as Either.Error<ServerError>).error)
                } else if (result.third is Either.Error) {
                    error((result.third as Either.Error<ServerError>).error)
                } else {
                    error(ServerError.COMPLETE_MELTDOWN)
                }
            }
        )
    }


    fun <A, B, C, D> quadError(request: Either<ServerError, Quadruple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>>>): Either<ServerError, Quadruple<A, B, C, D>> {
        return request.fold(
            ifError = { error(it) },
            ifEmpty = { empty() },
            ifValue = { result ->
                if (result.first is Either.Value && result.second is Either.Value && result.third is Either.Value && result.fourth is Either.Value) {
                    value(
                        Quadruple(
                            result.first.value,
                            result.second.value,
                            result.third.value,
                            result.fourth.value
                        )
                    )
                } else if (result.first is Either.Error) {
                    error(result.first.error)
                } else if (result.second is Either.Error) {
                    error(result.second.error)
                } else if (result.third is Either.Error) {
                    error(result.third.error)
                } else if (result.fourth is Either.Error) {
                    error(result.fourth.error)
                } else {
                    error(ServerError.COMPLETE_MELTDOWN)
                }
            }
        )
    }

    fun <A, B, C, D, E> quintError(request: Either<ServerError, Quintuple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>, Either<ServerError, E>>>): Either<ServerError, Quintuple<A, B, C, D, E>> {
        return request.fold(
            ifError = { error(it) },
            ifEmpty = { empty() },
            ifValue = { result ->
                if (result.first is Either.Value && result.second is Either.Value && result.third is Either.Value && result.fourth is Either.Value && result.fifth is Either.Value) {
                    value(
                        Quintuple(
                            result.first.value,
                            result.second.value,
                            result.third.value,
                            result.fourth.value,
                            result.fifth.value
                        )
                    )
                } else if (result.first is Either.Error) {
                    error(result.first.error)
                } else if (result.second is Either.Error) {
                    error(result.second.error)
                } else if (result.third is Either.Error) {
                    error(result.third.error)
                } else if (result.fourth is Either.Error) {
                    error(result.fourth.error)
                } else if (result.fifth is Either.Error) {
                    error(result.fifth.error)
                } else {
                    error(ServerError.COMPLETE_MELTDOWN)
                }
            }
        )
    }

    fun <A, B, C, D, E, F> sexError(request: Either<ServerError, Sextuple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>, Either<ServerError, E>, Either<ServerError, F>>>): Either<ServerError, Sextuple<A, B, C, D, E, F>> {
        return request.fold(
            ifError = { error(it) },
            ifEmpty = { empty() },
            ifValue = { result ->
                if (result.first is Either.Value && result.second is Either.Value && result.third is Either.Value && result.fourth is Either.Value && result.fifth is Either.Value && result.sixth is Either.Value) {
                    value(
                        Sextuple(
                            result.first.value,
                            result.second.value,
                            result.third.value,
                            result.fourth.value,
                            result.fifth.value,
                            result.sixth.value
                        )
                    )
                } else if (result.first is Either.Error) {
                    kotlin.error(result.first.error)
                } else if (result.second is Either.Error) {
                    kotlin.error(result.second.error)
                } else if (result.third is Either.Error) {
                    kotlin.error(result.third.error)
                } else if (result.fourth is Either.Error) {
                    kotlin.error(result.fourth.error)
                } else if (result.fifth is Either.Error) {
                    kotlin.error(result.fifth.error)
                } else if (result.sixth is Either.Error) {
                    kotlin.error(result.sixth.error)
                } else {
                    error(ServerError.COMPLETE_MELTDOWN)
                }
            }
        )
    }
}