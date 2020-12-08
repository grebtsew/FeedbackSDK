package com.example.mysdk


import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AiImageTestRecording {
    lateinit var appContext: Context
    @Before
    fun init() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.mysdk", appContext.packageName)

    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)


    @Test
    fun aiImageTestRecording() {
        val appCompatEditText = onView(
                allOf(withId(R.id.editTextTextPersonName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1)))
        appCompatEditText.perform(scrollTo(), replaceText("test"), closeSoftKeyboard())

        val imageView = onView(
                allOf(withId(R.id.imageView),
                        withParent(withParent(withId(R.id.aiimage))),
                        isDisplayed()))
        imageView.check(matches(isDisplayed()))

        val appCompatEditText2 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1)))
        appCompatEditText2.perform(scrollTo(), click())

        val appCompatEditText3 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1)))
        appCompatEditText3.perform(scrollTo(), replaceText("Comput"))

        val appCompatEditText4 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("Comput"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1),
                        isDisplayed()))
        appCompatEditText4.perform(closeSoftKeyboard())

        val appCompatEditText5 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("Comput"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1)))
        appCompatEditText5.perform(scrollTo(), click())

        val appCompatEditText6 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("Comput"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1)))
        appCompatEditText6.perform(scrollTo(), replaceText("Computer"))

        val appCompatEditText7 = onView(
                allOf(withId(R.id.editTextTextPersonName), withText("Computer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                1),
                        isDisplayed()))
        appCompatEditText7.perform(closeSoftKeyboard())

        val linearLayout = onView(
                allOf(withId(R.id.aiimage),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()))
        linearLayout.check(matches(isDisplayed()))

        val imageView2 = onView(
                allOf(withId(R.id.imageView),
                        withParent(withParent(withId(R.id.aiimage))),
                        isDisplayed()))
        imageView2.check(matches(isDisplayed()))

        val imageView3 = onView(
                allOf(withId(R.id.imageView),
                        withParent(withParent(withId(R.id.aiimage))),
                        isDisplayed()))
        imageView3.check(matches(isDisplayed()))

        val appCompatEditText8 = onView(
                allOf(withId(R.id.editTextNumberSigned),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        0),
                                2)))
        appCompatEditText8.perform(scrollTo(), replaceText("10"), closeSoftKeyboard())

        val imageView4 = onView(
                allOf(withId(R.id.imageView),
                        withParent(withParent(withId(R.id.aiimage))),
                        isDisplayed()))
        imageView4.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
