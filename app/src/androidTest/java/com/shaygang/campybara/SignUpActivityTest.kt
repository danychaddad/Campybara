package com.shaygang.campybara

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaygang.campybara.Activity.SignUpActivity
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    @Rule
    @JvmField
    var mActivityRule: ActivityScenarioRule<SignUpActivity> =
        ActivityScenarioRule(SignUpActivity::class.java)

    @Before
    fun setUp() {
        // Set up any necessary dependencies or test data before each test
    }

    @After
    fun tearDown() {
        // Clean up any resources or test data after each test
    }

    @Test
    fun signInTextButton_shouldLaunchSignInActivity() {
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.signInTxt)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.sign_in_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun signUpButton_withValidInput_shouldCreateUserAndRedirectToSignIn() {
        // Enter valid input values
        onView(withId(R.id.firstName)).perform(typeText("John"), closeSoftKeyboard())
        onView(withId(R.id.lastName)).perform(typeText("Doe"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("1234567890"), closeSoftKeyboard())
        onView(withId(R.id.dateOfBirth)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2000, 0, 1))
        onView(withText(android.R.string.ok)).perform(click())
        onView(withId(R.id.emailEt)).perform(typeText("test2@example.com"), closeSoftKeyboard())
        onView(withId(R.id.passET)).perform(typeText("test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassEt)).perform(typeText("test1234"), closeSoftKeyboard())
        onView(withId(R.id.signUnBtn)).perform(click())

        // Verify that the user is created and redirected to the sign-in screen
        Thread.sleep(2000)
        onView(withId(R.id.sign_in_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun signUpButton_withInValidInput_shouldCreateUserAndRedirectToSignIn() {
        // Enter valid input values
        onView(withId(R.id.firstName)).perform(typeText("John"), closeSoftKeyboard())
        onView(withId(R.id.lastName)).perform(typeText("Doe"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("1234567890"), closeSoftKeyboard())
        onView(withId(R.id.dateOfBirth)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2000, 0, 1))
        onView(withText(android.R.string.ok)).perform(click())
        onView(withId(R.id.emailEt)).perform(typeText("fareswhb@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passET)).perform(typeText("test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassEt)).perform(typeText("test1234"), closeSoftKeyboard())
        onView(withId(R.id.signUnBtn)).perform(click())

        // Verify that the user is created and redirected to the sign-in screen
        Thread.sleep(2000)
        onView(withId(R.id.sign_up_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun signUpButton_withMismatchedPasswords_shouldShowErrorMessage() {
        // Enter input values with mismatched passwords
        onView(withId(R.id.firstName)).perform(typeText("John"), closeSoftKeyboard())
        onView(withId(R.id.lastName)).perform(typeText("Doe"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("1234567890"), closeSoftKeyboard())
        onView(withId(R.id.dateOfBirth)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2000, 0, 1))
        onView(withText(android.R.string.ok)).perform(click())
        onView(withId(R.id.emailEt)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.passET)).perform(typeText("test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassEt)).perform(typeText("test5678"), closeSoftKeyboard())
        onView(withId(R.id.signUnBtn)).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.sign_up_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun signUpButton_withMissingFields_shouldShowErrorMessage() {
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())

        // Click the sign-up button with missing fields
        onView(withId(R.id.signUnBtn)).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.sign_up_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }
}