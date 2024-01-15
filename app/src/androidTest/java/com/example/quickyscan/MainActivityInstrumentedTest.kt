package com.example.quickyscan

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.quickyscan.activities.CameraActivity
import com.example.quickyscan.activities.MainActivity
import com.example.quickyscan.activities.SavedFilesActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        // Initialize Intents before each test
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Intents after each test
        Intents.release()
    }

    @Test
    fun testScanButtonNavigation() {
        // Click on the scan button and check if it launches CameraActivity
        onView(ViewMatchers.withId(R.id.scan_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(CameraActivity::class.java.name))
    }

    @Test
    fun testFilesMenuItemNavigation() {
        // Click on the show_menu button, then on the Files item, and check if it launches SavedFilesActivity
        onView(ViewMatchers.withId(R.id.show_menu)).perform(click())
        onView(ViewMatchers.withText("Saved Files")).perform(click())
        Intents.intended(IntentMatchers.hasComponent(SavedFilesActivity::class.java.name))
    }

    @Test
    fun testScanMenuItemNavigation() {
        // Click on the show_menu button, then on the Scan item, and check if it launches CameraActivity
        onView(ViewMatchers.withId(R.id.show_menu)).perform(click())
        onView(ViewMatchers.withText("Scan")).perform(click())
        Intents.intended(IntentMatchers.hasComponent(CameraActivity::class.java.name))
    }
}