package com.shaygang.campybara

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaygang.campybara.Activity.CreateGroupActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateGroupActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(CreateGroupActivity::class.java)

    @Test
    fun testGroupNameAndDescEmpty() {
        // Test when group name and description are empty
        onView(withId(R.id.createGroupBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_group_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testNoMembersAdded() {
        // Test when no members are added to the group
        onView(withId(R.id.groupName)).perform(typeText("Test Group Name"), closeSoftKeyboard())
        onView(withId(R.id.groupDesc)).perform(typeText("Test Group Description"), closeSoftKeyboard())
        onView(withId(R.id.createGroupBtn)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_group_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun testMemberAlreadyAdded() {
        // Test when member is already added to the group
        onView(withId(R.id.groupName)).perform(typeText("Test Group Name"), closeSoftKeyboard())
        onView(withId(R.id.groupDesc)).perform(typeText("Test Group Description"), closeSoftKeyboard())
        onView(withId(R.id.newMemberEmail)).perform(typeText("fareswhb@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.addGroupMember)).perform(click())
        onView(withId(R.id.newMemberEmail)).perform(typeText("fareswhb@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.addGroupMember)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_group_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }


    @Test
    fun testAddNewMember() {
        // Espresso test for adding a new member to the group
        onView(withId(R.id.groupName)).perform(typeText("Test Group Name"), closeSoftKeyboard())
        onView(withId(R.id.groupDesc)).perform(typeText("Test Group Description"), closeSoftKeyboard())
        onView(withId(R.id.newMemberEmail)).perform(typeText("fareswhb@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.addGroupMember)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.create_group_layout)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

}