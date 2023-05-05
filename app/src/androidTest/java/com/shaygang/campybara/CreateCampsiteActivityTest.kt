package com.shaygang.campybara

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaygang.campybara.Activity.CreateCampsiteActivity
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateCampsiteActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CreateCampsiteActivity::class.java)

    @Test
    fun testSaveLocationBtn() {
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        Thread.sleep(2000)
        onView(withId(R.id.saveLocationBtn)).perform(click())
        onView(withId(R.id.geolocationTextView)).check(matches(not(withText(""))))
        Thread.sleep(2000)
        onView(withId(R.id.create_campsite_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testConfirmBtn_withEmptyFields() {
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.confirmBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_campsite_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testConfirmBtn_withInvalidCapacity() {
        onView(withId(R.id.csNameET)).perform(typeText("Test Campsite"), closeSoftKeyboard())
        onView(withId(R.id.csDescET)).perform(typeText("Test Description"), closeSoftKeyboard())
        onView(withId(R.id.csCapET)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.saveLocationBtn)).perform(click())
        onView(withId(R.id.confirmBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_campsite_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testConfirmBtn_withValidCapacity() {
        onView(withId(R.id.csNameET)).perform(typeText("Test Campsite"), closeSoftKeyboard())
        onView(withId(R.id.csDescET)).perform(typeText("Test Description"), closeSoftKeyboard())
        onView(withId(R.id.csCapET)).perform(typeText("5"), closeSoftKeyboard())
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.saveLocationBtn)).perform(click())
        onView(withId(R.id.confirmBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_campsite_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testConfirmBtn_withEmptyAddress() {
        onView(withId(R.id.csNameET)).perform(typeText("Test Campsite"), closeSoftKeyboard())
        onView(withId(R.id.csDescET)).perform(typeText("Test Description"), closeSoftKeyboard())
        onView(withId(R.id.csCapET)).perform(typeText("2"), closeSoftKeyboard())
        onView(withId(R.id.create_campsite_layout)).perform(ViewActions.swipeUp())
        onView(withId(R.id.confirmBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_campsite_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }
}

