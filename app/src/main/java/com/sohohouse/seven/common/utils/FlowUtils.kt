package com.sohohouse.seven.common.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


/**
 * This extension function can be used to observe SharedFlow considering Activity or Fragment's lifecycle state.
 */
fun <T> SharedFlow<T>.collectLatest(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    observeOn: CoroutineContext = Dispatchers.Default,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collectLatest.collectLatest {
                    if (observeOn != collectOn) {
                        withContext(collectOn) {
                            action(it)
                        }
                    } else action(it)
                }
            }
        }
    }
}


/**
 * This extension function can be used to observe StateFlow considering Activity or Fragment's lifecycle state.
 */
fun <T> StateFlow<T>.collectLatest(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    observeOn: CoroutineContext = Dispatchers.Default,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collectLatest.collectLatest {
                    if (observeOn != collectOn) {
                        withContext(collectOn) {
                            action(it)
                        }
                    } else action(it)
                }
            }
        }
    }
}

fun <T> SharedFlow<T>.collect(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observeOn: CoroutineContext = Dispatchers.IO,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collect.collect {
                    if (observeOn != collectOn) {
                        withContext(collectOn) {
                            action(it)
                        }
                    } else action(it)
                }
            }
        }
    }
}

fun <T> StateFlow<T>.collect(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observeOn: CoroutineContext = Dispatchers.IO,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collect.collect {
                    if (observeOn != collectOn) {
                        withContext(collectOn) {
                            action(it)
                        }
                    } else action(it)
                }
            }
        }
    }
}