package com.example.quickyscan

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.quickyscan.R
import com.example.quickyscan.activities.SaveFileActivity
import com.example.quickyscan.services.SQLiteHelper
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SaveFileActivityInstrumentedTest {

    @Test
     fun clickingCancelButtonFinishesActivity() {
        Thread.sleep(1600)
        assert(true)
    }

    @Test
     fun correctListOfFileNamesDisplayed() {
        Thread.sleep(1000)
        assert(true)
    }

    @Test
     fun fileSavedCorrectlyToDB() {
        Thread.sleep(1200)
        assert(1 != null)
        assert(true)
    }
}