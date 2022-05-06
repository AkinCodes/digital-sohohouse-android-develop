package com.sohohouse.seven.common.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.lang.RuntimeException
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

suspend inline fun <T> polledSuspendCoroutine(
    maxTries: Int,
    delayTime: Long = 5000,
    crossinline trigger: suspend Continuation<T>.() -> FinishPolling
) = coroutineScope {
    suspendCoroutine<T> {
        var tries = 0
        launch {
            while (true) {
                if (it.trigger()) return@launch
                if (tries >= maxTries) {
                    it.resumeWith(
                        Result.failure(PollingException())
                    )
                    break
                } else {
                    tries++
                    delay(delayTime)
                }
            }
        }
    }
}

typealias FinishPolling = Boolean

fun interface PollingCondition<T> {
    operator fun invoke(data: T): Boolean
}

class PollingException : RuntimeException("Polling never met end condition")