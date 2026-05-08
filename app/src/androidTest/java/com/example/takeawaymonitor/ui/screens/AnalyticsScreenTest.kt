package com.example.takeawaymonitor.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.takeawaymonitor.ui.theme.TakeawayMonitorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun navController() =
        TestNavHostController(ApplicationProvider.getApplicationContext())

    @Test
    fun analyticsScreen_displaysText() {
        composeTestRule.setContent {
            TakeawayMonitorTheme {
                AnalyticsScreen(navController = navController())
            }
        }
        composeTestRule
            .onNodeWithText("Analytics Screen")
            .assertIsDisplayed()
    }
}
