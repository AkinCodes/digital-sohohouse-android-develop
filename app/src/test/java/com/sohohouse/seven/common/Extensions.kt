package com.sohohouse.seven.common

import io.mockk.MockK
import io.mockk.MockKDsl
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import kotlin.reflect.KClass

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

inline fun <reified T> captor(): ArgumentCaptor<T> =
    ArgumentCaptor.forClass(T::class.java) as ArgumentCaptor<T>

inline fun <reified T : Any> relaxedMockk(
    name: String? = null,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean = false,
    block: T.() -> Unit = {}
): T = MockK.useImpl {
    MockKDsl.internalMockk(
        name,
        true,
        *moreInterfaces,
        relaxUnitFun = relaxUnitFun,
        block = block
    )
}