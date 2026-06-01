package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUser
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel

@Composable
fun DashboardScreen(
    user: AppUser?,
    viewModel: EarningViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ImmersiveAccentBlue)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcoming & Wallet Card Gradient (from-[#334455] to-[#1E2B3C])
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF334455), Color(0xFF1E2B3C))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome back,",
                                    color = ImmersiveAccentLight.copy(alpha = 0.75f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = user.name,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }

                            // Hot Streak indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFCC00).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFFFFCC00).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = "🔥", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user.streakCount} Days",
                                    color = Color(0xFFFFCC00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Balance Display styling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "CONVERTIBLE BALANCE",
                                    color = ImmersiveAccentLight.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${user.points}",
                                        color = Color.White,
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PTS",
                                        color = ImmersiveAccentLight.copy(alpha = 0.8f),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "≈ ₹${(user.points / 100.0)} Cash",
                                    color = Color(0xFF00FFCC),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onNavigateToTab(5) }, // withdraw to Wallet tab
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00C853),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                modifier = Modifier.testTag("withdraw_shortcut_button")
                            ) {
                                Icon(imageVector = Icons.Default.Done, contentDescription = "Withdraw", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Cash Out", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Daily Consecutive Streaks Check-In Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("streak_calendar_card")
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Attendance Bonus",
                                color = ImmersiveTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                             )
                        }

                        IconButton(onClick = { viewModel.claimDailyCheckIn() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Check active streak bonus",
                                tint = ImmersiveAccentBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Claim consecutive daily points below. Skipping a day resets your multiplier flame!",
                        color = ImmersiveTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7 days checklist icons styled
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (day in 1..7) {
                            val isCompleted = day <= user.streakCount
                            val isNext = day == user.streakCount + 1

                            val circleColor = when {
                                isCompleted -> ImmersiveGlowColor
                                isNext -> Color(0xFF334455)
                                else -> Color(0xFF16181A)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(circleColor)
                                        .clickable {
                                            if (isNext) {
                                                viewModel.claimDailyCheckIn()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Day $day Checked",
                                            tint = ImmersiveAccentDarkText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "$day",
                                            color = if (isNext) ImmersiveAccentLight else ImmersiveTextSecondary.copy(alpha = 0.4f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Day $day",
                                    color = if (isCompleted) ImmersiveAccentBlue else ImmersiveTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Check-in Trigger Action Button styled using blue accent
                    Button(
                        onClick = { viewModel.claimDailyCheckIn() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImmersiveAccentBlue,
                            contentColor = ImmersiveAccentDarkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("checkin_trigger_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ImmersiveAccentDarkText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Claim Daily Attendance Reward",
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveAccentDarkText
                        )
                    }
                }
            }
        }

        // Gameplay Challenges vs Task Earning Panel (Grid choices)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Battle Arena shortcut card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(2) } // Skill Games Arena tab
                        .testTag("go_gaming_card")
                        .border(1.dp, ImmersiveBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3F4850)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎮", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Reflex Arena", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Battle real players in skill grid duels.",
                            color = ImmersiveTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Offers shortcuts
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(1) } // Offerwalls tab
                        .testTag("go_offers_card")
                        .border(1.dp, ImmersiveBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3F4850)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📋", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Earn Tasks", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Download apps, provide manual reviews.",
                            color = ImmersiveTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Stats Dashboard Card styled
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "YOUR TOURNAMENT STATS",
                        fontSize = 11.sp,
                        color = ImmersiveTextSecondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${user.matchesPlayed}", color = ImmersiveTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Matches Played", color = ImmersiveTextSecondary, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${user.matchesWon}", color = Color(0xFF00FFCC), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Matches Won", color = ImmersiveTextSecondary, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val winrate = if (user.matchesPlayed > 0) {
                                (user.matchesWon * 100 / user.matchesPlayed)
                            } else 0
                            Text(text = "$winrate%", color = ImmersiveAccentBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Win Ratio", color = ImmersiveTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
