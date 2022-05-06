package com.sohohouse.seven

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.After
import org.junit.Before

open class BaseUITest {

    private lateinit var idlingResource: IdlingResource

    @Before
    fun registerIdlingResource() {
        idlingResource = App.appComponent.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}