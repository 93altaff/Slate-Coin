package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUser
import com.example.data.UpiTransaction
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    user: AppUser?,
    transactions: List<UpiTransaction>,
    viewModel: EarningViewModel
) {
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ImmersiveAccentBlue)
        }
        return
    }

    var upiIdInput by remember { mutableStateOf(user.upiId ?: "") }
    var pointsToWithdrawStr by remember { mutableStateOf("") }
    
    val upiProcessing by viewModel.upiProcessing.collectAsState()
    val upiStatusList by viewModel.upiStatusList.collectAsState()
    val securityLogs by viewModel.securityLogList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Display
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CONVERTIBLE BALANCE",
                        fontSize = 11.sp,
                        color = ImmersiveTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${user.points} Points",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Exchange Ratio: 100 PTS = ₹1.00 UPI Cash",
                                fontSize = 11.sp,
                                color = ImmersiveTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ImmersiveAccentLight.copy(0.12f))
                                .border(1.dp, ImmersiveAccentLight.copy(0.25f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "₹${(user.points / 100.0)} Cash",
                                color = ImmersiveAccentLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // UPI Instant Settlement Gateway Request Card styled
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upi_withdrawal_card")
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = ImmersiveAccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURE INSTANT UPI SETTLEMENT",
                            color = ImmersiveAccentBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = { upiIdInput = it },
                        label = { Text("UPI VPA Address (e.g. name@bank)", color = ImmersiveTextSecondary) },
                        textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upi_input_address"),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ImmersiveBorder,
                            focusedBorderColor = ImmersiveAccentBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pointsToWithdrawStr,
                        onValueChange = { pointsToWithdrawStr = it },
                        label = { Text("Points to Withdraw (Min 100)", color = ImmersiveTextSecondary) },
                        textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("points_input_withdraw"),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ImmersiveBorder,
                            focusedBorderColor = ImmersiveAccentBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    val requestedVal = pointsToWithdrawStr.toIntOrNull() ?: 0
                    val canRequest = requestedVal >= 100 && requestedVal <= user.points && upiIdInput.isNotBlank() && !upiProcessing

                    Button(
                        onClick = {
                            viewModel.initiateUpiWithdrawal(upiIdInput, requestedVal)
                            pointsToWithdrawStr = ""
                        },
                        enabled = canRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C853),
                            disabledContainerColor = Color(0xFF232527)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_upi_payout_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (upiProcessing) "Processing settlement..." else "Execute Secure Instant Payout",
                            fontWeight = FontWeight.Bold,
                            color = if (canRequest) Color.White else ImmersiveTextSecondary.copy(0.4f)
                        )
                    }

                    // Payout settlement status checklist live log
                    AnimatedVisibility(
                        visible = upiProcessing || upiStatusList.isNotEmpty(),
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF16181A))
                                .border(1.dp, ImmersiveBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "IMPS SETTLEMENT LOGS",
                                    color = ImmersiveAccentLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                upiStatusList.forEach { line ->
                                    Text(
                                        text = line,
                                        color = if (line.startsWith("✅")) Color(0xFF00FFCC) else if (line.contains("❌") || line.contains("⚠️")) Color(0xFFFF5252) else ImmersiveTextPrimary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                                if (upiProcessing) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(color = Color(0xFF00C853), modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }

        // LIVE SECURITY CONSOLE / ANTI-FRAUD STATS TERMINAL styled
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ANTI-FRAUD FAIRPLAY CORE",
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Connected indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FFCC))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF16181A))
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            items(securityLogs) { log ->
                                Text(
                                    text = log,
                                    color = if (log.contains("RISK") || log.contains("ALERT")) Color(0xFFFF5252) else Color(0xFF00FFCC),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Payout histories header
        item {
            Text(
                text = "Settlement Log History",
                color = ImmersiveTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No payout history recorded yet. Build up points to redeem!",
                        color = ImmersiveTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(transactions) { tx ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
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
                        Column {
                            Text(text = tx.upiId, fontWeight = FontWeight.Bold, color = ImmersiveTextPrimary, fontSize = 13.sp)
                            Text(text = "TxID: ${tx.txId} • $dateStr", color = ImmersiveTextSecondary, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${tx.rupeesWithdrawn}",
                                color = if (tx.status == "SUCCESSFUL") Color(0xFF00FFCC) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )

                            Text(
                                text = tx.status,
                                color = if (tx.status == "SUCCESSFUL") Color(0xFF00FFCC).copy(0.7f) else Color(0xFFFF5252).copy(0.7f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
