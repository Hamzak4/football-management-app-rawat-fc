package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen() {
    val groups = ClubRepository.chatGroups
    var selectedGroupId by remember { mutableStateOf("g_main") }
    val currentGroup = groups.find { it.id == selectedGroupId } ?: groups.first()
    
    val currentUser = ClubRepository.currentUser.value
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Typing indicator
    val typingIndicator = ClubRepository.typingIndicators[selectedGroupId]

    var textInput by remember { mutableStateOf("") }
    val groupMessages = ClubRepository.chatMessages.filter { it.groupId == selectedGroupId }
    val pinnedMessage = groupMessages.find { it.isPinned }
    
    // Popup React Dialog State
    var reactingMessageId by remember { mutableStateOf<String?>(null) }
    
    // Media attachment selection state
    var showMediaAttachDiag by remember { mutableStateOf(false) }

    // Scroll to bottom on load
    LaunchedEffect(groupMessages.size) {
        if (groupMessages.isNotEmpty()) {
            listState.animateScrollToItem(groupMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Group Swiper Channel Switcher Tabs
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(groups) { group ->
                    val isSelected = group.id == selectedGroupId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroupId = group.id },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(group.iconUrl, fontSize = 14.sp)
                                Text(group.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE50914),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.White.copy(alpha = 0.3f),
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        // Sub-Header describing currently selected room
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentGroup.description,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)

        // Pinned Message Announcement Panel
        AnimatedVisibility(
            visible = pinnedMessage != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            if (pinnedMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE50914).copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = Color(0xFFE50914),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PINNED MESSAGE BY " + pinnedMessage.senderName.uppercase(),
                            color = Color(0xFFE50914),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = pinnedMessage.text,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { ClubRepository.pinChatMessage(pinnedMessage.id, false) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Unpin",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Chat Bubble Scroll Area
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groupMessages) { msg ->
                    val isOwn = msg.senderId == currentUser?.id
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
                    ) {
                        // Sender profile initial badge if not own
                        if (!isOwn) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                    .align(Alignment.Bottom),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = msg.senderName.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Message content bubble
                        Column(
                            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
                        ) {
                            // Sender metadata
                            if (!isOwn) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = msg.senderName,
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = msg.senderRole.name,
                                        color = Color(0xFFE50914),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            // Dynamic bubble box container with Long Click context toggle
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (!isOwn) 2.dp else 16.dp,
                                            bottomEnd = if (isOwn) 2.dp else 16.dp
                                        )
                                    )
                                    .background(
                                        if (isOwn) Color(0xFFE50914) else Color(0xFF1A1A1A)
                                    )
                                    .combinedClickable(
                                        onLongClick = { reactingMessageId = msg.id },
                                        onClick = {}
                                    )
                                    .padding(vertical = 10.dp, horizontal = 14.dp)
                            ) {
                                Column {
                                    // Image support using Coil URL presets
                                    if (msg.mediaUrl != null && msg.isImage) {
                                        AsyncImage(
                                            model = msg.mediaUrl,
                                            contentDescription = "Shared Attachment",
                                            modifier = Modifier
                                                .widthIn(max = 240.dp)
                                                .heightIn(max = 180.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.Black),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 13.5.sp
                                    )
                                }
                            }

                            // Render Emoji Reactions Block
                            if (msg.reactions.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    msg.reactions.forEach { reaction ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    currentUser?.let {
                                                        ClubRepository.addEmojiReaction(msg.id, it.id, reaction.emoji)
                                                    }
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${reaction.emoji} ${reaction.count}",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Timestamp and pins indicators
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                if (msg.isPinned) {
                                    Icon(
                                        Icons.Default.PushPin,
                                        contentDescription = "pinned",
                                        tint = Color(0xFFE50914),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                                Text(
                                    text = msg.timestamp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontSize = 9.sp
                                )
                                if (isOwn) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check, // Simulated single read receipt tick
                                        contentDescription = "Read",
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating reactions contextual options dialog overlay list
            if (reactingMessageId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { reactingMessageId = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(24.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "MESSAGE CONTROL OFFICE",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            // Emoji Fast reactions row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val emojis = listOf("👍", "🔥", "❤️", "👏", "🩹", "⚽")
                                emojis.forEach { emoji ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                            .clickable {
                                                currentUser?.let {
                                                    ClubRepository.addEmojiReaction(reactingMessageId!!, it.id, emoji)
                                                }
                                                reactingMessageId = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 20.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Administrative control lines
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Pin Option
                                Button(
                                    onClick = {
                                        ClubRepository.pinChatMessage(reactingMessageId!!, true)
                                        reactingMessageId = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PushPin, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pin Message Anchor", color = Color.White, fontSize = 12.sp)
                                }

                                // Delete option simulation
                                if (currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.CAPTAIN) {
                                    Button(
                                        onClick = {
                                            val index = ClubRepository.chatMessages.indexOfFirst { it.id == reactingMessageId }
                                            if (index != -1) {
                                                ClubRepository.chatMessages.removeAt(index)
                                                ClubRepository.saveChatMessages()
                                            }
                                            reactingMessageId = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Delete Message (Admin)", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live typing indicator display layout
        if (typingIndicator != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFE50914), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = typingIndicator,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        // Message input dock section
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Media attachment toggle
                IconButton(
                    onClick = { showMediaAttachDiag = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Media",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Text Input Field
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Message team...", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE50914),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        focusedContainerColor = Color.Black.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
                )

                // Send Trigger
                IconButton(
                    onClick = {
                        if (textInput.isNotEmpty()) {
                            ClubRepository.sendMessage(selectedGroupId, textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE50914), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Submit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

    // Attachment Preset select Dialog list
    if (showMediaAttachDiag) {
        AlertDialog(
            onDismissRequest = { showMediaAttachDiag = false },
            title = { Text("SELECT FOOTBALL MEDIA PRESET", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a professional FC photo snapshot preset to share with your group instantly:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    
                    val presets = listOf(
                        "Victory Huddle" to "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&q=80&w=400",
                        "Tactical Board" to "https://images.unsplash.com/photo-1543351611-58f69d7c1781?auto=format&fit=crop&q=80&w=400",
                        "Training Ground" to "https://images.unsplash.com/photo-1517927033932-b3d18e61fb3a?auto=format&fit=crop&q=80&w=400"
                    )

                    presets.forEach { preset ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .clickable {
                                    ClubRepository.sendMessage(
                                        selectedGroupId,
                                        "Shared photos: ${preset.first}",
                                        imagePreset = preset.second
                                    )
                                    showMediaAttachDiag = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(preset.first, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFFE50914), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMediaAttachDiag = false }) {
                    Text("CANCEL", color = Color(0xFFE50914))
                }
            },
            containerColor = Color(0xFF111111)
        )
    }
}
