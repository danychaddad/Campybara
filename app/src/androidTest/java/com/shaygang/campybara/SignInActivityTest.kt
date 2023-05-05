package com.shaygang.campybara

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.shaygang.campybara.Activity.SignInActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {

    @Rule
    @JvmField
    var mActivityRule: ActivityScenarioRule<SignInActivity> =
        ActivityScenarioRule(SignInActivity::class.java)

    @Before
    fun setUp() {
        // Make sure the user is signed out before running the tests
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun testSignInWithVerifiedEmail() {
        val activityScenario = ActivityScenario.launch(SignInActivity::class.java)
        // Type in valid email and password
        onView(withId(R.id.emailEt)).perform(
            typeText("fareswhb@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passET)).perform(typeText("fares123"), closeSoftKeyboard())

        // Click the sign-in button
        onView(withId(R.id.signInBtn)).perform(click())

        // Check that the user is navigated to the MainActivity
        Thread.sleep(2000)
        onView(withId(R.id.main_layout)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun shouldLaunchSignUpActivity() {
        onView(withId(R.id.signUpTxt)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.sign_up_layout)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testSignInWithIncorrectEmailOrPassword() {
        val activityScenario = ActivityScenario.launch(SignInActivity::class.java)
        // Type in incorrect email and/or password
        onView(withId(R.id.emailEt)).perform(
            typeText("invalid_email@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passET)).perform(typeText("wrong_password"), closeSoftKeyboard())

        // Click the sign-in button
        onView(withId(R.id.signInBtn)).perform(click())

        onView(withId(R.id.sign_in_layout)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testResetPasswordWithValidEmail() {
        val activityScenario = ActivityScenario.launch(SignInActivity::class.java)
        // Type in a valid email
        onView(withId(R.id.emailEt)).perform(
            typeText("fareswhb@gmail.com"),
            closeSoftKeyboard()
        )

        // Click the "Reset Password" button
        onView(withId(R.id.resetPassBtn)).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.sign_in_layout)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testResetPasswordWithInvalidEmail() {
        val activityScenario = ActivityScenario.launch(SignInActivity::class.java)
        // Type in an invalid email
        onView(withId(R.id.emailEt)).perform(typeText("invalid_email"), closeSoftKeyboard())

        // Click the "Reset Password" button
        onView(withId(R.id.resetPassBtn)).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.sign_in_layout)).check(matches(isCompletelyDisplayed()))
    }
}