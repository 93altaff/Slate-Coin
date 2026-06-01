package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EarnOffer
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel

@Composable
fun OffersScreen(
    offers: List<EarnOffer>,
    viewModel: EarningViewModel
) {
    var selectedOffer by remember { mutableStateOf<EarnOffer?>(null) }
    var proofText by remember { mutableStateOf("") }
    var showAdminPanel by remember { mutableStateOf(false) }

    // Admin form inputs
    var adminTitle by remember { mutableStateOf("") }
    var adminDesc by remember { mutableStateOf("") }
    var adminPoints by remember { mutableStateOf("") }
    var adminCategory by remember { mutableStateOf("Offerwall") }
    var adminUrl by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var adminPasswordError by remember { mutableStateOf(false) }

    val categories = listOf("Offerwall", "App Install", "Survey", "Verification")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Admin panel toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Offerwall Tasks",
                            color = ImmersiveTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Complete simple micro-gigs to credit wallet",
                            color = ImmersiveTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Admin spawn switch
                    Button(
                        onClick = { showAdminPanel = !showAdminPanel },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAdminPanel) ImmersiveAccentBlue else ImmersiveSurface,
                            contentColor = if (showAdminPanel) ImmersiveAccentDarkText else ImmersiveTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.border(1.dp, ImmersiveBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = if (showAdminPanel) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Admin Area",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Expanded Admin controller panel
            if (showAdminPanel) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ImmersiveBorder, RoundedCornerShape(18.dp))
                            .testTag("admin_creation_panel"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                text = "🔒 MANUALLY SPAWN OFFERS (ADMIN SIMULATOR)",
                                color = ImmersiveAccentBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = adminTitle,
                                onValueChange = { adminTitle = it },
                                label = { Text("Offer Title", color = ImmersiveTextSecondary) },
                                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_input_title"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ImmersiveBorder,
                                    focusedBorderColor = ImmersiveAccentBlue
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = adminDesc,
                                onValueChange = { adminDesc = it },
                                label = { Text("Task Description / Proof Requirements", color = ImmersiveTextSecondary) },
                                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_input_desc"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ImmersiveBorder,
                                    focusedBorderColor = ImmersiveAccentBlue
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = adminUrl,
                                onValueChange = { adminUrl = it },
                                label = { Text("Task Launch URL (e.g., https://site.com)", color = ImmersiveTextSecondary) },
                                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_input_url"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ImmersiveBorder,
                                    focusedBorderColor = ImmersiveAccentBlue
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = adminPoints,
                                    onValueChange = { adminPoints = it },
                                    label = { Text("Points", color = ImmersiveTextSecondary) },
                                    textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_input_points"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = ImmersiveBorder,
                                        focusedBorderColor = ImmersiveAccentBlue
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF334455))
                                        .border(1.dp, ImmersiveBorder, RoundedCornerShape(4.dp))
                                        .clickable {
                                            val curIdx = categories.indexOf(adminCategory)
                                            val nextIdx = (curIdx + 1) % categories.size
                                            adminCategory = categories[nextIdx]
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Category: $adminCategory",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = {
                                    adminPassword = it
                                    adminPasswordError = false
                                },
                                label = { Text("Admin Password Token", color = if (adminPasswordError) Color.Red else ImmersiveTextSecondary) },
                                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                                singleLine = true,
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_input_password"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = if (adminPasswordError) Color.Red else ImmersiveBorder,
                                    focusedBorderColor = if (adminPasswordError) Color.Red else ImmersiveAccentBlue
                                )
                            )

                            if (adminPasswordError) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Error: Invalid Admin Password Token!",
                                    color = Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (adminPassword != "9372@Altaf93Slate") {
                                        adminPasswordError = true
                                        return@Button
                                    }
                                    val pts = adminPoints.toIntOrNull() ?: 0
                                    if (adminTitle.isNotBlank() && pts > 0) {
                                        viewModel.adminAddManualOffer(
                                            title = adminTitle,
                                            desc = adminDesc,
                                            points = pts,
                                            category = adminCategory,
                                            taskUrl = adminUrl.ifBlank { "https://example.com/task-start" },
                                            passwordEntered = adminPassword
                                        )
                                        adminTitle = ""
                                        adminDesc = ""
                                        adminPoints = ""
                                        adminUrl = ""
                                        adminPassword = ""
                                        adminPasswordError = false
                                        showAdminPanel = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_save_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImmersiveAccentBlue,
                                    contentColor = ImmersiveAccentDarkText
                                )
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Publish Live Manual Task", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Offers List
            items(offers) { offer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offer_card_${offer.id}")
                        .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val categoryIcon = when (offer.category) {
                                    "App Install" -> "📲"
                                    "Survey" -> "🗳️"
                                    "Verification" -> "🔑"
                                    else -> "📋"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF3F4850)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = categoryIcon, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = offer.title,
                                        color = if (offer.isCompleted) ImmersiveTextPrimary.copy(alpha = 0.4f) else ImmersiveTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = offer.category + if (offer.isManualAdmin) " (Admin)" else "",
                                        color = if (offer.isManualAdmin) ImmersiveAccentBlue else ImmersiveTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Dynamic Point Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (offer.isCompleted) Color.Gray.copy(alpha = 0.12f)
                                        else ImmersiveAccentLight.copy(alpha = 0.12f)
                                    )
                                    .border(
                                        1.dp,
                                        if (offer.isCompleted) Color.Gray.copy(0.25f) else ImmersiveAccentLight.copy(0.25f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${offer.pointsReward} Pts",
                                    color = if (offer.isCompleted) Color.Gray else ImmersiveAccentLight,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = offer.description,
                            color = ImmersiveTextSecondary.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (offer.isCompleted) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = Color(0xFF00C853),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Completed & Credited",
                                    color = Color(0xFF00C853),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    selectedOffer = offer
                                    proofText = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("claim_button_${offer.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImmersiveAccentBlue,
                                    contentColor = ImmersiveAccentDarkText
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Complete & Submit Proof", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Complete offer overlay modal
        if (selectedOffer != null) {
            val configOffer = selectedOffer!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.75f))
                    .clickable { selectedOffer = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp))
                        .clickable(enabled = false) {}, // prevent click-closing
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Verify Task Completion",
                            color = ImmersiveTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = configOffer.title,
                            color = ImmersiveAccentLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Admin verification parameters require entering proof of work. E.g. Screenshot link, task code or completed email details:",
                            color = ImmersiveTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Start Task (Open URL) Action Button
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        Button(
                            onClick = {
                                try {
                                    val urlToOpen = configOffer.taskUrl.ifBlank { "https://example.com/task" }
                                    uriHandler.openUri(urlToOpen)
                                } catch (e: Exception) {
                                    // Fail-safe handler
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_task_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImmersiveAccentBlue,
                                contentColor = ImmersiveAccentDarkText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Task Action Icon",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Task (${configOffer.taskUrl.ifBlank { "https://example.com" }})",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = proofText,
                            onValueChange = { proofText = it },
                            placeholder = { Text("E.g. Completed sign-up with handle 'Slayer_One'", color = ImmersiveTextSecondary.copy(0.4f)) },
                            textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary),
                            minLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("proof_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = ImmersiveBorder,
                                focusedBorderColor = ImmersiveAccentBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedOffer = null },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveTextPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveBorder)
                            ) {
                                Text(text = "Cancel")
                            }

                            Button(
                                onClick = {
                                    viewModel.completeOffer(configOffer.id, proofText)
                                    selectedOffer = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00C853),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(2f)
                                    .testTag("submit_proof_button")
                            ) {
                                Text(text = "Verify Submission", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
