package com.sohohouse.seven.common.refresh

import com.sohohouse.seven.common.utils.Quadruple
import com.sohohouse.seven.common.utils.Quintuple
import com.sohohouse.seven.common.utils.Sextuple
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.request.CoreAPIRequest
import io.reactivex.Single
import io.reactivex.functions.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ZipRequestsUtil @Inject constructor(private val coreRequestFactory: CoreRequestFactory) {

    fun <ONE, TWO, THREE, FOUR, FIVE, SIX> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>,
        five: CoreAPIRequest<FIVE>,
        six: CoreAPIRequest<SIX>
    )
            : Single<Either<ServerError, Sextuple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>,
            Either<ServerError, FIVE>,
            Either<ServerError, SIX>>>> {
        return Single.zip(
            coreRequestFactory.create(one),
            coreRequestFactory.create(two),
            coreRequestFactory.create(three),
            coreRequestFactory.create(four),
            coreRequestFactory.create(five),
            coreRequestFactory.create(six),
            Function6 { t1: Either<ServerError, ONE>,
                        t2: Either<ServerError, TWO>,
                        t3: Either<ServerError, THREE>,
                        t4: Either<ServerError, FOUR>,
                        t5: Either<ServerError, FIVE>,
                        t6: Either<ServerError, SIX> ->
                value(Sextuple(t1, t2, t3, t4, t5, t6))
            })

    }

    fun <ONE, TWO, THREE, FOUR, FIVE> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>,
        five: CoreAPIRequest<FIVE>
    )
            : Single<Either<ServerError, Quintuple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>,
            Either<ServerError, FIVE>>>> {
        return Single.zip(
            coreRequestFactory.create(one),
            coreRequestFactory.create(two),
            coreRequestFactory.create(three),
            coreRequestFactory.create(four),
            coreRequestFactory.create(five),
            Function5 { t1: Either<ServerError, ONE>,
                        t2: Either<ServerError, TWO>,
                        t3: Either<ServerError, THREE>,
                        t4: Either<ServerError, FOUR>,
                        t5: Either<ServerError, FIVE> ->
                value(Quintuple(t1, t2, t3, t4, t5))
            })
    }

    fun <ONE, TWO, THREE, FOUR> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>,
        four: CoreAPIRequest<FOUR>
    )
            : Single<Either<ServerError, Quadruple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>,
            Either<ServerError, FOUR>>>> {
        return Single.zip(
            coreRequestFactory.create(one),
            coreRequestFactory.create(two),
            coreRequestFactory.create(three),
            coreRequestFactory.create(four),
            Function4 { t1: Either<ServerError, ONE>,
                        t2: Either<ServerError, TWO>,
                        t3: Either<ServerError, THREE>,
                        t4: Either<ServerError, FOUR> ->
                value(Quadruple(t1, t2, t3, t4))
            })
    }

    fun <ONE, TWO, THREE> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>,
        three: CoreAPIRequest<THREE>
    )
            : Single<Either<ServerError, Triple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>>>> {
        return Single.zip(
            coreRequestFactory.create(one),
            coreRequestFactory.create(two),
            coreRequestFactory.create(three),
            Function3 { t1: Either<ServerError, ONE>,
                        t2: Either<ServerError, TWO>,
                        t3: Either<ServerError, THREE> ->
                value(Triple(t1, t2, t3))
            })

    }

    fun <ONE, TWO> zipSingles(
        one: Single<Either<ServerError, ONE>>,
        two: Single<Either<ServerError, TWO>>
    )
            : Single<Either<ServerError, Pair<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>>>> {
        return Single.zip<Either<ServerError, ONE>, Either<ServerError, TWO>, Either<ServerError,
                Pair<Either<ServerError, ONE>,
                        Either<ServerError, TWO>>>>(
            one,
            two,
            BiFunction { t1: Either<ServerError, ONE>,
                         t2: Either<ServerError, TWO> ->
                value(Pair(t1, t2))
            })

    }

    fun <ONE, TWO, THREE> zipSingles(
        one: Single<Either<ServerError, ONE>>,
        two: Single<Either<ServerError, TWO>>,
        three: Single<Either<ServerError, THREE>>
    )
            : Single<Either<ServerError, Triple<
            Either<ServerError, ONE>,
            Either<ServerError, TWO>,
            Either<ServerError, THREE>>>> {
        return Single.zip<Either<ServerError, ONE>, Either<ServerError, TWO>, Either<ServerError, THREE>, Either<ServerError,
                Triple<Either<ServerError, ONE>,
                        Either<ServerError, TWO>,
                        Either<ServerError, THREE>>>>(
            one,
            two,
            three,
            Function3 { t1: Either<ServerError, ONE>,
                        t2: Either<ServerError, TWO>,
                        t3: Either<ServerError, THREE> ->
                value(Triple(t1, t2, t3))
            })

    }

    fun <ONE, TWO> issueApiCall(
        one: CoreAPIRequest<ONE>,
        two: CoreAPIRequest<TWO>
    )
            : Single<Either<ServerError, Pair<Either<ServerError, ONE>, Either<ServerError, TWO>>>> {

        return Single.zip(
            coreRequestFactory.create(one),
            coreRequestFactory.create(two),
            BiFunction { t1: Either<ServerError, ONE>,
                         t2: Either<ServerError, TWO> ->
                value(Pair(t1, t2))
            })
    }

    @Deprecated("Use issueApiCallV2 with Kotlin Coroutine")
    fun <FIRST> issueApiCall(request: CoreAPIRequest<FIRST>) = coreRequestFactory.create(request)

    fun <FIRST> issueApiCallV2(request: CoreAPIRequest<FIRST>) =
        coreRequestFactory.createV2(request)

}