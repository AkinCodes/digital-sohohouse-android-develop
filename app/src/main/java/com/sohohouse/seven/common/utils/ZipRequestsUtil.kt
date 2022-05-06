package com.sohohouse.seven.common.utils

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.request.CoreAPIRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
open class ZipRequestsUtil @Inject constructor(private val coreRequestFactory: CoreRequestFactory) {

    suspend fun <ONE, TWO, THREE, FOUR, FIVE, SIX> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>,
        five: CoreAPIRequest<FIVE>,
        six: CoreAPIRequest<SIX>,
    )
            : Either<ServerError, Sextuple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>,
            Either<ServerError, FIVE>,
            Either<ServerError, SIX>>> {
        return withContext(Dispatchers.Default) {
            val results = awaitAll(
                async { coreRequestFactory.createV2(one) },
                async { coreRequestFactory.createV2(two) },
                async { coreRequestFactory.createV2(three) },
                async { coreRequestFactory.createV2(four) },
                async { coreRequestFactory.createV2(five) },
                async { coreRequestFactory.createV2(six) }
            )
            value(
                Sextuple(
                    results[0] as Either<ServerError, ONE>,
                    results[1] as Either<ServerError, TWO>,
                    results[2] as Either<ServerError, THREE>,
                    results[3] as Either<ServerError, FOUR>,
                    results[4] as Either<ServerError, FIVE>,
                    results[5] as Either<ServerError, SIX>
                )
            )
        }
    }

    suspend fun <ONE, TWO, THREE, FOUR, FIVE> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>,
        five: CoreAPIRequest<FIVE>,
    )
            : Either<ServerError, Quintuple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>,
            Either<ServerError, FIVE>>> {
        return withContext(Dispatchers.Default) {
            val results = awaitAll(
                async { coreRequestFactory.createV2(one) },
                async { coreRequestFactory.createV2(two) },
                async { coreRequestFactory.createV2(three) },
                async { coreRequestFactory.createV2(four) },
                async { coreRequestFactory.createV2(five) }
            )
            value(
                Quintuple(
                    results[0] as Either<ServerError, ONE>,
                    results[1] as Either<ServerError, TWO>,
                    results[2] as Either<ServerError, THREE>,
                    results[3] as Either<ServerError, FOUR>,
                    results[4] as Either<ServerError, FIVE>
                )
            )
        }
    }

    suspend fun <ONE, TWO, THREE, FOUR> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>,
    )
            : Either<ServerError, Quadruple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>>> {
        return withContext(Dispatchers.Default) {
            val results = awaitAll(
                async { coreRequestFactory.createV2(one) },
                async { coreRequestFactory.createV2(two) },
                async { coreRequestFactory.createV2(three) },
                async { coreRequestFactory.createV2(four) }
            )
            value(
                Quadruple(
                    results[0] as Either<ServerError, ONE>,
                    results[1] as Either<ServerError, TWO>,
                    results[2] as Either<ServerError, THREE>,
                    results[3] as Either<ServerError, FOUR>
                )
            )
        }
    }

    suspend fun <ONE, TWO, THREE> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
    )
            : Either<ServerError, Triple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>>> {
        return withContext(Dispatchers.Default) {
            val results = awaitAll(
                async { coreRequestFactory.createV2(one) },
                async { coreRequestFactory.createV2(two) },
                async { coreRequestFactory.createV2(three) }
            )
            value(
                Triple(
                    results[0] as Either<ServerError, ONE>,
                    results[1] as Either<ServerError, TWO>,
                    results[2] as Either<ServerError, THREE>
                )
            )
        }

    }

    suspend fun <ONE, TWO> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
    )
            : Either<ServerError, Pair<Either<ServerError, ONE>, Either<ServerError, TWO>>> {
        return withContext(Dispatchers.Default) {
            val results = awaitAll(
                async { coreRequestFactory.createV2(one) },
                async { coreRequestFactory.createV2(two) }
            )
            value(
                Pair(
                    results[0] as Either<ServerError, ONE>,
                    results[1] as Either<ServerError, TWO>
                )
            )
        }
    }

    fun <FIRST> issueApiCall(request: CoreAPIRequest<FIRST>) = coreRequestFactory.createV2(request)

}