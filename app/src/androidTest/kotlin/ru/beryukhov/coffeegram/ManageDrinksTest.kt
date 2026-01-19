package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test

class ManageDrinksTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Verify that Settings has a "Manage Drinks" option
    @Test
    fun settings_hasManageDrinksOption() {
        // Click on Settings in bottom navigation
        composeTestRule.onNodeWithText("Settings").performClick()

        // Verify "Manage Drinks" option is displayed
        composeTestRule.onNodeWithText("Manage Drinks").assertIsDisplayed()
    }

    // Verify that default drinks are displayed in Manage Drinks screen
    @Test
    fun manageDrinks_displaysDefaultDrinksAndAddButton() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle() // Wait for the list to load

        // Verify default drinks are displayed

        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Espresso"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Latte"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Cappuccino"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Americano"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Macchiato"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Glace"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Frappe"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Mocha"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Fredo"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Irish"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Cocoa"))
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Chocolate"))

        composeTestRule.onNodeWithTag("AddDrinkButton").assertIsDisplayed()
    }

    // Verify that clicking "Add" button navigates to Add Drink screen
    @Test
    fun manageDrinks_addButtonNavigatesToAddDrink() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        // Click on "Add" button
        composeTestRule.onNodeWithTag("AddDrinkButton").performClick()

        composeTestRule.waitForIdle()
        
        // Verify that Add Drink screen is displayed
        composeTestRule.onNodeWithText("Add New Drink").assertIsDisplayed()

        // Verify the Icon selection is displayed
        composeTestRule.onNodeWithText("Drink Icon").assertIsDisplayed()

        // Verify the Cost input is displayed
        composeTestRule.onNodeWithText("Default Cost").assertIsDisplayed()
    }

    // Verify that when we click on a drink, we navigate to Edit Drink screen
    @Test
    fun manageDrinks_clickingDrinkNavigatesToEditDelete() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle() // Wait for the list to load

        // Click on "Espresso"
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Espresso"))
        composeTestRule.onNodeWithText("Espresso").performClick()

        composeTestRule.waitForIdle()

        // Verify that Edit Drink screen is displayed with Espresso details
        composeTestRule.onNodeWithText("Edit Drink").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DrinkNameInput").assertTextContains("Espresso")

        // Verify that drink icon is displayed (we check for the presence of the icon selection)
        composeTestRule.onNodeWithTag("DrinkIconDropdown").assertTextContains("Espresso")
        
        // Verify that default cost is displayed
        composeTestRule.onNodeWithTag("DrinkPriceInput").assertIsDisplayed()

        // Save button and Delete button
        composeTestRule.onNodeWithTag("SaveDrinkButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DeleteDrinkButton").assertIsDisplayed()
    }

    // Add a new drink and verify it appears in the list
    @Test
    fun manageDrinks_addNewDrink() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        // Click on "Add" button
        composeTestRule.onNodeWithTag("AddDrinkButton").performClick()

        composeTestRule.waitForIdle()

        // Enter drink name and save
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextInput("Test Drink")

        // Cost should be empty by default
        composeTestRule.onNodeWithTag("DrinkPriceInput").assertTextContains("")

        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()

        // Verify that new drink appears in the list
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Test Drink"))
        composeTestRule.onNodeWithText("Test Drink").assertIsDisplayed()
    }

    @Test
    fun manageDrinks_deleteDrink() {

        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle() // Wait for the list to load

        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Glace"))
        composeTestRule.onNodeWithText("Glace").performClick()

        composeTestRule.waitForIdle()

        // Click on "Delete" button
        composeTestRule.onNodeWithTag("DeleteDrinkButton").performClick()

        composeTestRule.waitForIdle()

        // Confirm deletion
        composeTestRule.onNodeWithText("Delete this drink?").assertIsDisplayed()

        // First click "No" and verify the drink still exists
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("DrinkNameInput").assertTextContains("Glace")

        // Now click "Delete" again and confirm with "Yes"
        composeTestRule.onNodeWithTag("DeleteDrinkButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Yes").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Glace").assertDoesNotExist()
    }

    @Test
    fun manageDrinks_addInvalidDrink() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle()

        // Click on "Add" button
        composeTestRule.onNodeWithTag("AddDrinkButton").performClick()
        composeTestRule.waitForIdle()

        // Verify that Save button is disabled when name is empty
        composeTestRule.onNodeWithTag("SaveDrinkButton").assertIsNotEnabled()

        // Enter a valid name
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextInput("Test Drink Name")

        // Verify that user cant enter 1000
        composeTestRule.onNodeWithTag("DrinkPriceInput").performClick().performTextInput("1000")
        composeTestRule.onNodeWithTag("DrinkPriceInput").assertTextContains("")

        // Save the drink
        composeTestRule.onNodeWithText("Save").performClick()
    }
    
    @Test
    fun manageDrinks_editDrinkInvalidData() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle() // Wait for the list to load

        // Click on "Espresso"
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Espresso"))
        composeTestRule.onNodeWithText("Espresso").performClick()

        composeTestRule.waitForIdle()

        // Clear the name input
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextReplacement("")
        
        // Verify that Save button is disabled
        composeTestRule.onNodeWithTag("SaveDrinkButton").assertIsNotEnabled()

         // Enter a valid name
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextInput("Espresso")

        // Verify that user cant enter 1000
        composeTestRule.onNodeWithTag("DrinkPriceInput").performClick().performTextInput("1000")
        composeTestRule.onNodeWithTag("DrinkPriceInput").assertTextContains("")

        // Save the drink
        composeTestRule.onNodeWithText("Save").performClick()
    }

    // Add a new drink, then edit it and verify changes are saved
    @Test
    fun manageDrinks_addAndEditDrink() {
        // Navigate to Manage Drinks screen
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Manage Drinks").performClick()

        Thread.sleep(2000)
        composeTestRule.waitForIdle()
        
        // Click on "Add" button
        composeTestRule.onNodeWithTag("AddDrinkButton").performClick()
        composeTestRule.waitForIdle()

        // Enter drink name and save
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextInput("Test Drink")
        
        // Set icon
        composeTestRule.onNodeWithTag("DrinkIconDropdown").performClick()
        composeTestRule.onNodeWithText("Latte").performClick()

        // Set price
        composeTestRule.onNodeWithTag("DrinkPriceInput").performClick().performTextInput("150")

        composeTestRule.onNodeWithText("Save").performClick()   

        composeTestRule.waitForIdle()

        // Verify that new drink appears in the list
        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Test Drink"))
        composeTestRule.onNodeWithText("Test Drink").assertIsDisplayed()    

        // Click on the newly added drink to edit
        composeTestRule.onNodeWithText("Test Drink").performClick()

        composeTestRule.waitForIdle()

        // Assert that the details are correct
        composeTestRule.onNodeWithTag("DrinkNameInput").assertTextContains("Test Drink")
        composeTestRule.onNodeWithTag("DrinkIconDropdown").assertTextContains("Latte")
        composeTestRule.onNodeWithTag("DrinkPriceInput").assertTextContains("150")

        // Change the name, icon and price
        composeTestRule.onNodeWithTag("DrinkNameInput").performClick().performTextReplacement("Updated Test Drink")
        composeTestRule.onNodeWithTag("DrinkIconDropdown").performClick()
        composeTestRule.onNodeWithText("Espresso").performClick()
        composeTestRule.onNodeWithTag("DrinkPriceInput").performClick().performTextReplacement("200")

        // Save changes
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithTag("DrinkList").performScrollToNode(hasText("Updated Test Drink"))
        composeTestRule.onNodeWithText("Updated Test Drink").assertIsDisplayed()    

    }

}
