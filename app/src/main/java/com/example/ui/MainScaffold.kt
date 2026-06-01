package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: EarningViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val user by viewModel.mainUser.collectAsState()
    val leaderboardUsers by viewModel.leaderboard.collectAsState()
    val allOffersByAdmin by viewModel.offers.collectAsState()
    val listTransactions by viewModel.transactions.collectAsState()
    val secureChatMessages by viewModel.chatMessages.collectAsState()

    // Active push notification banner
    val activeBannerNotification by viewModel.activeNotification.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SLATE COIN",
                                color = ImmersiveTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.testTag("app_brand_title")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ImmersiveAccentLight.copy(0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SECURE",
                                    color = ImmersiveAccentLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ImmersiveBackground,
                        titleContentColor = ImmersiveTextPrimary
                    ),
                    actions = {
                        // Show current points directly in topBar
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ImmersiveSurface)
                                .border(1.dp, ImmersiveBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.points ?: 0} PTS",
                                color = ImmersiveAccentLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.testTag("scaffold_header_points")
                            )
                        }
                    },
                    modifier = Modifier.testTag("main_app_top_bar")
                )

                // Simulated Push Notification Banner overlay sliding down at top of Scaffold
                AnimatedVisibility(
                    visible = activeBannerNotification != null,
                    enter = slideInVertically { -it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ImmersiveAccentBlue)
                            .padding(12.dp)
                            .testTag("dynamic_noti_banner"),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "🔔", fontSize = 16.sp, color = ImmersiveAccentDarkText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeBannerNotification ?: "",
                                color = ImmersiveAccentDarkText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = ImmersiveSurface,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .testTag("bottom_nav_scaffold")
                    .drawBehind {
                        drawLine(
                            color = ImmersiveBorder,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                val tabs = listOf(
                    Triple("Home", Icons.Default.Home, 0),
                    Triple("Tasks", Icons.AutoMirrored.Filled.List, 1),
                    Triple("Play Duel", Icons.Default.PlayArrow, 2),
                    Triple("Leaders", Icons.Default.Star, 3),
                    Triple("Chat", Icons.Default.Lock, 4),
                    Triple("Wallet", Icons.Default.Done, 5)
                )
                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ImmersiveAccentLight,
                            selectedTextColor = ImmersiveAccentLight,
                            unselectedIconColor = ImmersiveTextSecondary,
                            unselectedTextColor = ImmersiveTextSecondary,
                            indicatorColor = Color(0xFF334455)
                        )
                    )
                }
            }
        },
        containerColor = ImmersiveBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    user = user,
                    viewModel = viewModel,
                    onNavigateToTab = { selectedTab = it }
                )
                1 -> OffersScreen(
                    offers = allOffersByAdmin,
                    viewModel = viewModel
                )
                2 -> MatchmakingScreen(
                    viewModel = viewModel
                )
                3 -> LeaderboardScreen(
                    users = leaderboardUsers,
                    viewModel = viewModel
                )
                4 -> ChatScreen(
                    messages = secureChatMessages,
                    viewModel = viewModel
                )
                5 -> WalletScreen(
                    user = user,
                    transactions = listTransactions,
                    viewModel = viewModel
                )
            }
        }
    }
}
