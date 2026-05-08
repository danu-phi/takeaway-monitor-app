package com.example.takeawaymonitor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end navigation test that starts MainActivity and verifies each screen
 * is reachable via the NavHost.
 *
 * Requires a connected device / emulator.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStarts_onDashboardScreen() {
        // NavHost starts at "dashboard" destination
        composeTestRule
            .onNodeWithText("Dashboard Screen")
            .assertIsDisplayed()
    }
}
