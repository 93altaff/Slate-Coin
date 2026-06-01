package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.SecureChat
import com.example.ui.theme.*
import com.example.viewmodel.EarningViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    messages: List<SecureChat>,
    viewModel: EarningViewModel
) {
    var chatInputText by remember { mutableStateOf("") }
    var showCiphertextOnly by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
    ) {
        // Encrypted Chat Header Metadata Indicator styled
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 18.dp, end = 18.dp, bottom = 6.dp)
                .border(1.dp, ImmersiveBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ImmersiveAccentBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURITY INTERACTION LINK",
                            color = ImmersiveAccentBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    // Toggle Raw cipher
                    IconButton(onClick = { showCiphertextOnly = !showCiphertextOnly }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Toggle cipher state",
                            tint = ImmersiveAccentLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Player data transmissions utilize AES-GCM-256 signatures. Toggle the cipher key to switch between hardware raw hashes and plain text packages.",
                    color = ImmersiveTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // Chat logs viewport
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isUserMessage) Alignment.End else Alignment.Start
                val cardColor = if (msg.isUserMessage) Color(0xFF334455) else ImmersiveSurface
                val cardBorderColor = if (msg.isUserMessage) ImmersiveAccentBlue else ImmersiveBorder

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = if (msg.isUserMessage) (Arrangement.End as Arrangement.Horizontal) else (Arrangement.Start as Arrangement.Horizontal),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        if (!msg.isUserMessage) {
                            val avColor = when (msg.avatarColorOrdinal) {
                                1 -> Color(0xFF00FFCC)
                                2 -> ImmersiveAccentBlue
                                3 -> Color(0xFFFFCC00)
                                else -> ImmersiveAccentLight
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(avColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = msg.senderName.take(1),
                                    color = ImmersiveAccentDarkText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Card(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (msg.isUserMessage) 12.dp else 2.dp,
                                bottomEnd = if (msg.isUserMessage) 2.dp else 12.dp
                            ),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            modifier = Modifier.border(1.dp, cardBorderColor, RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (msg.isUserMessage) 12.dp else 2.dp,
                                bottomEnd = if (msg.isUserMessage) 2.dp else 12.dp
                            ))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!msg.isUserMessage) {
                                    Text(
                                        text = msg.senderName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ImmersiveAccentLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                if (showCiphertextOnly) {
                                    Text(
                                        text = msg.encryptedPayload.take(34) + "...",
                                        color = Color(0xFF00FFCC),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 14.sp
                                    )
                                } else {
                                    Column {
                                        Text(
                                            text = "Cipher: " + msg.encryptedPayload.take(18) + "...",
                                            color = ImmersiveTextSecondary.copy(0.4f),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.decryptedText,
                                            color = ImmersiveTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (msg.isUserMessage) "AES Sealed" else "AES Verified ✔",
                        color = ImmersiveTextSecondary.copy(0.4f),
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Outbound typing bottom dock styled
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ImmersiveSurface)
                .border(1.dp, ImmersiveBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.clearSecureChat() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Session",
                    tint = ImmersiveTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = chatInputText,
                onValueChange = { chatInputText = it },
                placeholder = { Text("Send secure sandbox string...", color = ImmersiveTextSecondary.copy(0.4f), fontSize = 13.sp) },
                textStyle = LocalTextStyle.current.copy(color = ImmersiveTextPrimary, fontSize = 13.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text"),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ImmersiveBorder,
                    focusedBorderColor = ImmersiveAccentBlue
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (chatInputText.isNotBlank()) {
                        viewModel.sendSecureMessage(chatInputText)
                        chatInputText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersiveAccentBlue,
                    contentColor = ImmersiveAccentDarkText
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(56.dp)) // padding for bottom navigation
    }
}
