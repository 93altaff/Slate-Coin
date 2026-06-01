package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainScaffold
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EarningViewModel
import com.example.viewmodel.MatchmakingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Slate Coin", appName)
  }

  @Test
  fun `test activity launch does not crash`() {
    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      // If launch completes, there's no fatal startup crash
    }
  }

  @Test
  fun `test viewModel flows and state operations`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = EarningViewModel(context as android.app.Application)

    // Verify initial values
    assertNotNull(viewModel.mainUser)
    assertNotNull(viewModel.leaderboard)
    assertNotNull(viewModel.offers)
    assertNotNull(viewModel.transactions)
    assertNotNull(viewModel.chatMessages)

    // Trigger basic operations to ensure no crash
    viewModel.claimDailyCheckIn()
    viewModel.sendSecureMessage("Hello secure sandbox!")
    viewModel.clearSecureChat()
    
    // Admin ops
    viewModel.adminAddManualOffer("Test Offer", "Test Description", 200, "Survey", passwordEntered = "9372@Altaf93Slate")
    
    // Referral ops
    viewModel.applyReferral("EARN-SLATE-123")

    // Withdrawal ops (invalid and valid cases)
    viewModel.initiateUpiWithdrawal("abc", 50) // Invalid UPI ID
    viewModel.initiateUpiWithdrawal("abc@ybl", 150) // Valid formatting but potentially beyond balance

    // Matchmaking ops
    assertEquals(MatchmakingState.Idle, viewModel.matchState.value)
    viewModel.registerGameClick(3) // register click while idle (should ignore and not crash)
    
    viewModel.abandonMatch()
  }

  @Test
  fun `test compose all screens does not crash`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = EarningViewModel(context as android.app.Application)
    
    composeTestRule.setContent {
      MyApplicationTheme {
        MainScaffold(viewModel = viewModel)
      }
    }
    
    // Check that we render the main brand title
    composeTestRule.onNodeWithTag("app_brand_title").assertExists()
  }
}

