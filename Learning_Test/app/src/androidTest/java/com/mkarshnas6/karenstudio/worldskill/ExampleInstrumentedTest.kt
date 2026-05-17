package com.mkarshnas6.karenstudio.worldskill

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mkarshnas6.karenstudio.worldskill", appContext.packageName)
    }

    @Before
    fun beforeTestHomeScreen() {
//        this function do every thing before start testing
//        like : create DB , create Channel notification
    }

    @After
    fun afterTestHomeScreen() {
//        this function do every thing after ending testing
//        like : clean temp files . and other ...
    }

    @get:Rule
    val composeTestMainActivity = createAndroidComposeRule<MainActivity>()

    @Test
    fun testHomeScreenSaveAndScrollMainScroll() {
//        set test tag on modifier widgets
        composeTestMainActivity.onNodeWithTag("TextFieldSaveDataStore")
            .performTextInput("test data from data store")

        composeTestMainActivity.onNodeWithTag("ButtonSaveDataStore")
            .performClick()



        composeTestMainActivity.onNodeWithTag("MainColumnHomeScreen")
            .performTouchInput { swipeUp() }

    }


    @Test
    fun testUiTestScreen() {
        // enter the screen
        composeTestMainActivity.onNodeWithTag("btn_TestUiScreen")
            .performScrollTo()

        composeTestMainActivity.onNodeWithTag("btn_TestUiScreen")
            .performClick()

        composeTestMainActivity.onNodeWithTag("TextFieldSearchProduct")
            .performTextInput("Laptop")

        composeTestMainActivity.waitForIdle()

        // simple product have exists
        composeTestMainActivity.onNodeWithTag("product_name_Laptop_Gaming_Pro").isDisplayed()

        // don't show another products
        composeTestMainActivity.onNodeWithTag("product_name_Wireless_Headphones")
            .assertDoesNotExist()

        composeTestMainActivity.onNodeWithTag("product_name_Coffee_Maker").assertDoesNotExist()

        // delete search
        composeTestMainActivity.onNodeWithTag("btn_delete_search")
            .performClick()

        composeTestMainActivity.waitForIdle()

        composeTestMainActivity.onNodeWithTag("category_product_Sports")
            .performClick()

        composeTestMainActivity.waitForIdle()

        composeTestMainActivity.onNodeWithTag("product_name_Running_Shoes")
            .isDisplayed()

        composeTestMainActivity.onNodeWithTag("product_name_Yoga_Mat")
            .isDisplayed()

        composeTestMainActivity.onNodeWithTag("product_name_Protein_Powder")
            .isDisplayed()

        composeTestMainActivity.onNodeWithTag("product_name_Water_Bottle").isDisplayed()

        composeTestMainActivity.onNodeWithTag("product_name_Coffee_Maker").assertDoesNotExist()

        composeTestMainActivity.onNodeWithTag("product_name_Laptop_Gaming_Pro").assertDoesNotExist()

        composeTestMainActivity.onNodeWithTag("tab_Profile").performClick()

        composeTestMainActivity.onNodeWithTag("TextTitleTabProfile").isDisplayed()

        composeTestMainActivity.onNodeWithTag("btn_LoginTabProfile").performScrollTo()

        composeTestMainActivity.onNodeWithTag("btn_AgreeTermsTabProfile").performClick()

        composeTestMainActivity.onNodeWithTag("btn_LoginTabProfile").performClick()

        composeTestMainActivity.waitForIdle()

        composeTestMainActivity.onNodeWithTag("text_MessageErrorTabProfile").isDisplayed()

        composeTestMainActivity.onNodeWithTag("TextField_UsernameTabProfile")
            .performTextInput("test user name")

        composeTestMainActivity.onNodeWithTag("TextField_EmailTabProfile")
            .performTextInput("mkarshnas6@gmail.com")

        composeTestMainActivity.onNodeWithTag("TextField_PasswordTabProfile")
            .performTextInput("12345")

        composeTestMainActivity.onNodeWithTag("TextField_AgeTabProfile").performTextInput("19")

        composeTestMainActivity.onNodeWithTag("btn_GenderMale").performClick()

        composeTestMainActivity.onNodeWithTag("btn_LoginTabProfile").performClick()

        composeTestMainActivity.onNodeWithTag("loading_BtnLoginTabProfile").isDisplayed()

        composeTestMainActivity.waitForIdle()

        composeTestMainActivity.onNodeWithTag("Snackbar_TabProfile").isDisplayed()

        composeTestMainActivity.onNodeWithTag("tab_Products").performClick()

        composeTestMainActivity.waitForIdle()


        composeTestMainActivity.onNodeWithTag("category_product_All")
            .performClick()

        composeTestMainActivity.waitForIdle()


    }

}