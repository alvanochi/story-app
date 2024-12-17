package com.dicoding.submissionintermediate.ui.upload

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.submissionintermediate.R
import com.dicoding.submissionintermediate.helper.EspressoIdlingResource
import com.dicoding.submissionintermediate.ui.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UploadActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(UploadActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.instance)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.instance)
    }

    @Test
    fun testAddStory() {
        val mockImageUri = Uri.parse("android.resource://com.dicoding.submissionintermediate/" + R.drawable.mock_astra)

        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_OPEN_DOCUMENT))
            .respondWith(
                Instrumentation.ActivityResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        data = mockImageUri
                    }
                )
            )

        onView(withId(R.id.btn_gallery)).perform(click())

        onView(withId(R.id.iv_preview)).check(matches(isDisplayed()))

        onView(withId(R.id.ed_add_description)).perform(typeText("UI Test"))

        onView(withId(R.id.button_add)).check(matches(isEnabled()))

        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.progressIndicator)).check(matches(isDisplayed()))

        hasComponent(MainActivity::class.java.name)
    }

}


