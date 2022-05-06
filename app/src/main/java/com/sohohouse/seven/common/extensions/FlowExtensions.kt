package com.sohohouse.seven.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

inline fun <reified T> MutableStateFlow<in T>.editAndEmit(edit: (T) -> T) {
    (this.value as? T?)?.let {
        this.value = edit(it)
    }
}

fun <T> flow(
    initialValue: T,
    block: suspend FlowCollector<T>.() -> Unit
): Flow<T> {
    return flow {
        emit(initialValue)
        block()
    }
}

fun <T, M> StateFlow<T>.mapState(
    coroutineScope: CoroutineScope = UnconfinedScope(),
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)

class UnconfinedScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Unconfined
}

fun MutableStateFlow<Int>.increment() {
    value += 1
}

fun MutableStateFlow<Int>.decrement() {
    value -= 1
}