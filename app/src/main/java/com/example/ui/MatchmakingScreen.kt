package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUser
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel
import com.example.viewmodel.MatchmakingState

@Composable
fun MatchmakingScreen(
    viewModel: EarningViewModel
) {
    val state by viewModel.matchState.collectAsState()
    val userScore by viewModel.userScore.collectAsState()
    val rivalScore by viewModel.rivalScore.collectAsState()
    val timeLeft by viewModel.gameTimeLeft.collectAsState()
    val opponent by viewModel.opponent.collectAsState()
    val activeTarget by viewModel.gameTargetIndex.collectAsState()
    val fraudAlert by viewModel.fraudAlert.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            fraudAlert != null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                        .testTag("fraud_lockout_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1415))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🚨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "ANTI-FRAUD LOCKOUT ALERT",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = fraudAlert ?: "Automated rapid clicks detected.",
                            color = ImmersiveTextPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Our high-fidelity system inspects physical delay deviations (<60ms intervals) to keep reward pools perfectly fair for actual players.",
                            color = ImmersiveTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.resetFraudAlert() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Acknowledge & Reconnect", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            state is MatchmakingState.Idle -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(ImmersiveAccentBlue.copy(0.12f))
                            .border(1.dp, ImmersiveAccentBlue.copy(0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚔️", fontSize = 44.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Reflex Duels Battleground",
                        color = ImmersiveTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time online multiplayer skill matchmaking",
                        color = ImmersiveTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ImmersiveAccentBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "MATCHMAKER POLICIES", color = ImmersiveAccentBlue, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "1. Entry Fee: Free Trial Round.\n2. Duration: 15 second click reflexes.\n3. Scoring: Gold active nodes will illuminate. Score by clicking fast! Miss clicks lose points.\n4. Reward pool: Win credits your ledger +150 Points!\n5. Fair play: Bot and mechanical macro patterns result in instant temporary penalties.",
                                color = ImmersiveTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.startMatchmaking() },
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveAccentBlue, contentColor = ImmersiveAccentDarkText),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("find_match_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = ImmersiveAccentDarkText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Find Fast Match Duel", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }

            state is MatchmakingState.PreparingIntegrity -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ImmersiveAccentBlue, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "SHIELD SYSTEM: BOOT CHECK", color = ImmersiveAccentBlue, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Inspecting hardware attestation keys...", color = ImmersiveTextSecondary, fontSize = 11.sp)
                }
            }

            state is MatchmakingState.Searching -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ImmersiveAccentLight, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "DUEL MATCHMAKING AT WORK...", color = ImmersiveTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Contacting secure relays for low-latency nodes...", color = ImmersiveTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(40.dp))
                    OutlinedButton(
                        onClick = { viewModel.abandonMatch() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveTextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveBorder)
                    ) {
                        Text(text = "Cancel Search")
                    }
                }
            }

            state is MatchmakingState.Countdown -> {
                val sec = (state as MatchmakingState.Countdown).seconds
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "COMPETITOR FOUND!",
                        color = ImmersiveAccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF334455)), contentAlignment = Alignment.Center) {
                                Text(text = "👤", fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "You", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Text(text = "VS", color = ImmersiveTextSecondary.copy(0.4f), fontWeight = FontWeight.Black, fontSize = 18.sp)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF2E3133)), contentAlignment = Alignment.Center) {
                                Text(text = "👾", fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = opponent?.name ?: "Rival", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "$sec",
                        color = ImmersiveAccentLight,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            state is MatchmakingState.Playing -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // LIVE HUD SCORING BAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ImmersiveSurface)
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "YOUR SCORE", color = ImmersiveTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "$userScore", color = Color(0xFF00FFCC), fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }

                        // Countdown Timer in center
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252).copy(0.12f))
                                .border(1.dp, Color(0xFFFF5252).copy(0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${timeLeft}s",
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = opponent?.name?.uppercase() ?: "RIVAL", color = ImmersiveTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "$rivalScore", color = ImmersiveAccentLight, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    // Progress slider line visual comparison
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(0.1f))
                    ) {
                        val total = (userScore + rivalScore).toFloat().coerceAtLeast(1f)
                        val ratio = (userScore / total).coerceIn(0.001f, 0.999f)
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(ratio)
                                    .background(Color(0xFF00FFCC))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f - ratio)
                                    .background(ImmersiveAccentLight)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // THE REFLEX GRID
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(ImmersiveSurface)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(9) { idx ->
                                val isTarget = idx == activeTarget
                                val blockColor = if (isTarget) ImmersiveAccentLight else Color(0xFF2E3133)
                                val highlightBorder = if (isTarget) ImmersiveAccentLight else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(blockColor)
                                        .border(2.dp, highlightBorder, RoundedCornerShape(14.dp))
                                        .clickable { viewModel.registerGameClick(idx) }
                                        .testTag("game_grid_block_$idx"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isTarget) {
                                        Text(text = "🔥", fontSize = 28.sp)
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(ImmersiveBorder)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🔒 BIOMETRIC VERIFIER: Click vector spacing analytics active.",
                        color = ImmersiveTextSecondary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            state is MatchmakingState.GameEnded -> {
                val data = state as MatchmakingState.GameEnded
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                if (data.userScore >= data.rivalScore) Color(0xFF00FFCC).copy(0.12f)
                                else Color(0xFFFF5252).copy(0.12f)
                            )
                            .border(
                                1.dp,
                                if (data.userScore >= data.rivalScore) Color(0xFF00FFCC).copy(0.3f) else Color(0xFFFF5252).copy(0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (data.userScore > data.rivalScore) "🏆" else if (data.userScore == data.rivalScore) "🤝" else "💔",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (data.userScore > data.rivalScore) "DUEL VICTOR!" else "DUEL TERMINATED",
                        color = ImmersiveTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.rewardText,
                        color = ImmersiveAccentLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ScoreCard("You", data.userScore, Color(0xFF00FFCC))
                    Spacer(modifier = Modifier.height(8.dp))
                    ScoreCard(opponent?.name ?: "Opponent", data.rivalScore, ImmersiveAccentLight)

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { viewModel.abandonMatch() }, // clean check
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveAccentBlue, contentColor = ImmersiveAccentDarkText),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("end_confirm_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Back to Lobby", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreCard(label: String, score: Int, tint: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ImmersiveBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold, color = ImmersiveTextPrimary)
            Text(text = "$score Points", fontWeight = FontWeight.Black, color = tint)
        }
    }
}
