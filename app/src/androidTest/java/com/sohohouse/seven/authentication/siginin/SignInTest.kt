package com.sohohouse.seven.authentication.siginin

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.sohohouse.seven.BaseUITest
import com.sohohouse.seven.R
import com.sohohouse.seven.welcome.WelcomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInTest : BaseUITest() {

    companion object {
        const val LOGIN_EMAIL = "software-devs@sohohouse.com.test"
        const val LOGIN_PWD = "wampum-witty-brioche"
    }

    @get:Rule
    val welcomeActivityRule = ActivityTestRule(WelcomeActivity::class.java)

    @Test
    fun testLogin() {
        //welcome screen
        onView(withId(R.id.sign_in)).perform(click())

        //login screen
        onView(withId(R.id.email_input)).perform(typeText(LOGIN_EMAIL))
        onView(withId(R.id.password_input)).perform(typeText(LOGIN_PWD), closeSoftKeyboard())
        onView(withId(R.id.sign_in_btn)).perform(click())

        //test onboarding screen shows
        onView(withText("Welcome to Soho House")).check(ViewAssertions.matches(isDisplayed()))
    }

}