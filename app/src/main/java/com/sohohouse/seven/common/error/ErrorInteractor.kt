package com.sohohouse.seven.common.error

import com.sohohouse.seven.common.utils.Quadruple
import com.sohohouse.seven.common.utils.Quintuple
import com.sohohouse.seven.common.utils.Sextuple
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import io.reactivex.Single
import javax.inject.Inject

class ErrorInteractor @Inject constructor() {

    fun <A> oneError(req: Single<Either<ServerError, A>>): Single<Either<ServerError, A>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    value(it.value)
                }
                is Either.Empty -> empty()
            }
        }
    }

    fun <A, B> pairError(req: Single<Either<ServerError, Pair<Either<ServerError, A>, Either<ServerError, B>>>>): Single<Either<ServerError, Pair<A, B>>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    val result = it.value
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
                is Either.Empty -> empty()
            }
        }
    }

    fun <A, B, C> tripError(req: Single<Either<ServerError, Triple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>>>>): Single<Either<ServerError, Triple<A, B, C>>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    val result = it.value
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
                is Either.Empty -> empty()
            }
        }
    }


    fun <A, B, C, D> quadError(req: Single<Either<ServerError, Quadruple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>>>>): Single<Either<ServerError, Quadruple<A, B, C, D>>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    val result = it.value
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
                is Either.Empty -> empty()
            }
        }
    }

    fun <A, B, C, D, E> quintError(req: Single<Either<ServerError, Quintuple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>, Either<ServerError, E>>>>): Single<Either<ServerError, Quintuple<A, B, C, D, E>>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    val result = it.value
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
                is Either.Empty -> empty()
            }
        }
    }

    fun <A, B, C, D, E, F> sexError(req: Single<Either<ServerError, Sextuple<Either<ServerError, A>, Either<ServerError, B>, Either<ServerError, C>, Either<ServerError, D>, Either<ServerError, E>, Either<ServerError, F>>>>): Single<Either<ServerError, Sextuple<A, B, C, D, E, F>>> {
        return req.map {
            when (it) {
                is Either.Error -> {
                    error(it.error)
                }
                is Either.Value -> {
                    val result = it.value
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
                        error(result.first.error)
                    } else if (result.second is Either.Error) {
                        error(result.second.error)
                    } else if (result.third is Either.Error) {
                        error(result.third.error)
                    } else if (result.fourth is Either.Error) {
                        error(result.fourth.error)
                    } else if (result.fifth is Either.Error) {
                        error(result.fifth.error)
                    } else if (result.sixth is Either.Error) {
                        error(result.sixth.error)
                    } else {
                        error(ServerError.COMPLETE_MELTDOWN)
                    }
                }
                is Either.Empty -> empty()
            }
        }
    }
}