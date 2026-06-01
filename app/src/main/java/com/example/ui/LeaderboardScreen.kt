package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUser
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel

@Composable
fun LeaderboardScreen(
    users: List<AppUser>,
    viewModel: EarningViewModel
) {
    var referralCodeInput by remember { mutableStateOf("") }
    val mainUser by viewModel.mainUser.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Referral Promo Code input card styled
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_card")
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ImmersiveAccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REFERRAL PROGRAM",
                            color = ImmersiveAccentBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Claim +300 bonus points by entering a friend's referral code. Check the real-time player list below for active codes!",
                        color = ImmersiveTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val alreadyReferred = mainUser != null && !mainUser!!.referredBy.isNullOrEmpty()

                    if (alreadyReferred) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ImmersiveAccentBlue.copy(0.12f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ImmersiveAccentBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Referral Applied: ${mainUser?.referredBy}",
                                color = ImmersiveAccentBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = referralCodeInput,
                                onValueChange = { referralCodeInput = it },
                                placeholder = { Text("E.g. ALPH-921", color = ImmersiveTextSecondary.copy(0.4f)) },
                                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("referral_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ImmersiveBorder,
                                    focusedBorderColor = ImmersiveAccentBlue
                                )
                            )

                            Button(
                                onClick = {
                                    if (referralCodeInput.isNotBlank()) {
                                        viewModel.applyReferral(referralCodeInput)
                                        referralCodeInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImmersiveAccentBlue,
                                    contentColor = ImmersiveAccentDarkText
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("apply_referral_button")
                            ) {
                                Text(text = "Claim", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ImmersiveBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Your Referral Code:", fontSize = 11.sp, color = ImmersiveTextSecondary)
                        Text(
                            text = mainUser?.referralCode ?: "EARN-SLATE-199",
                            fontWeight = FontWeight.Black,
                            color = ImmersiveAccentLight,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Leaderboard List Header
        item {
            Text(
                text = "Real-Time Leaderboard (Top 10)",
                color = ImmersiveTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Points update in real-time as users secure micro-gig and duel credits.",
                color = ImmersiveTextSecondary,
                fontSize = 12.sp
            )
        }

        // Leaderboard players rows styled
        itemsIndexed(users) { index, player ->
            val rankPos = index + 1
            val isMainUser = player.id == 1

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("leaderboard_item_$index")
                    .border(
                        1.dp,
                        if (isMainUser) ImmersiveAccentBlue else ImmersiveBorder,
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Rank Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when (rankPos) {
                                        1 -> Color(0xFFFFCC00)
                                        2 -> ImmersiveTextPrimary
                                        3 -> Color(0xFFFFAB91)
                                        else -> Color(0xFF2E3133)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (rankPos <= 3) "🏆" else "$rankPos",
                                color = if (rankPos <= 3) Color.Unspecified else ImmersiveTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = if (rankPos <= 3) 12.sp else 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.name,
                                    color = if (isMainUser) Color(0xFF00FFCC) else ImmersiveTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (player.streakCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "🔥 ${player.streakCount}d", fontSize = 10.sp, color = Color(0xFFFFCC00), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Code: " + player.referralCode + if (player.isSeed) " (Opponent)" else " (You)",
                                color = ImmersiveTextSecondary.copy(0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${player.points}",
                            color = ImmersiveAccentLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "PTS",
                            color = ImmersiveTextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
