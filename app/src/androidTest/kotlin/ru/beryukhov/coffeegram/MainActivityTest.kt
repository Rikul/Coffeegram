package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Basic instrumented tests for MainActivity.
 * Run with: ./gradlew :app:connectedDebugAndroidTest
 */
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_monthTableIsDisplayed() {
        // Verify the app launches and shows the month calendar
        composeTestRule.onNodeWithContentDescription("left_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("right_arrow").assertIsDisplayed()
    }

    @Test
    fun clickDay_navigatesToCoffeeList() {
        // Click on day 1 in the calendar
        composeTestRule.onNodeWithText("1").performClick()

        // Verify we navigated to the coffee list (shows coffee types)
        composeTestRule.onNodeWithText("Cappuccino").assertIsDisplayed()
    }

    @Test
    fun navigateToSettings_themeOptionsDisplayed() {
        // Click on Settings in bottom navigation
        composeTestRule.onNodeWithText("Settings").performClick()

        // Verify settings page shows theme options
        composeTestRule.onNodeWithText("System").assertIsDisplayed()
    }
}
