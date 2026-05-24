package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var activeTab by remember { mutableStateOf("HOME") } // "HOME", "CHAT", "MATCHES", "TRAINING", "TOURNAMENT", "PROFILE", "MEDIA", "ATTENDANCE", "ADMIN"

    val currentNotification = ClubRepository.currentNotification.value
    val currentUser = ClubRepository.currentUser.value
    val isCoachOrAdmin = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.ADMIN

    if (currentUser != null && !currentUser.isApproved) {
        PendingApprovalScreen(currentUser = currentUser, onLogout = onLogout)
        return
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE50914), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "R",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 18.sp
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "RAWAT FC ",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "HUB",
                                        color = Color(0xFFE50914),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    )
                                }
                                Text(
                                    text = "INTERNAL MANAGEMENT SYSTEM v2.4",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Connection Pill
                        Row(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(100.dp))
                                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                            Text(
                                text = "Firebase Connected",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.sp
                            )
                        }

                        // Profile quick link
                        IconButton(onClick = { activeTab = "PROFILE" }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "My Profile",
                                tint = if (activeTab == "PROFILE") Color(0xFFE50914) else Color.White
                            )
                        }

                        // Media center link
                        IconButton(onClick = { activeTab = "MEDIA" }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Media Folders",
                                tint = if (activeTab == "MEDIA") Color(0xFFE50914) else Color.White
                            )
                        }

                        // Log rankings attendance compliance index
                        IconButton(onClick = { activeTab = "ATTENDANCE" }) {
                            Icon(
                                imageVector = Icons.Default.Poll, // Analytics graph standard
                                contentDescription = "Analytics",
                                tint = if (activeTab == "ATTENDANCE") Color(0xFFE50914) else Color.White
                            )
                        }

                        // Office button (Coach admin only)
                        if (isCoachOrAdmin) {
                            IconButton(onClick = { activeTab = "ADMIN" }) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Coordinator Hub",
                                    tint = if (activeTab == "ADMIN") Color(0xFFE50914) else Color.White
                                )
                            }
                        }

                        // Logout trigger
                        IconButton(onClick = {
                            ClubRepository.logout()
                            onLogout()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Disconnect Hub",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    )
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                NavigationBar(
                    containerColor = Color.Black,
                    modifier = Modifier.background(Color.Black),
                    tonalElevation = 0.dp
                ) {
                    // Navigation items Array
                    val items = listOf(
                        Triple("HOME", "Dashboard", Icons.Default.Dashboard),
                        Triple("CHAT", "Team Chat", Icons.Default.Chat),
                        Triple("MATCHES", "Fixtures", Icons.Default.SportsSoccer),
                        Triple("TRAINING", "Practices", Icons.Default.FitnessCenter),
                        Triple("TOURNAMENT", "Standings", Icons.Default.EmojiEvents)
                    )
    
                    items.forEach { item ->
                        val isSelected = activeTab == item.first
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeTab = item.first },
                            icon = {
                                Icon(
                                    imageVector = item.third,
                                    contentDescription = item.second,
                                    tint = if (isSelected) Color(0xFFE50914) else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            label = {
                                Text(
                                    text = item.second,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated content transitions for clean native transitions feel
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenSwitch"
            ) { tab ->
                when (tab) {
                    "HOME" -> DashboardScreen(onNavigate = { dest -> activeTab = dest.uppercase() })
                    "CHAT" -> ChatScreen()
                    "MATCHES" -> MatchesScreen()
                    "TRAINING" -> TrainingScreen()
                    "TOURNAMENT" -> TournamentScreen()
                    "PROFILE" -> ProfileScreen()
                    "MEDIA" -> MediaScreen()
                    "ATTENDANCE" -> AttendanceScreen()
                    "ADMIN" -> AdminPanelScreen()
                }
            }

            // Real-time floating Push Notification simulated slide banner alert
            AnimatedVisibility(
                visible = currentNotification != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                if (currentNotification != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF221111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE50914).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE50914).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color(0xFFE50914),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "RAWAT FC REALTIME HUB",
                                    color = Color(0xFFE50914),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = currentNotification,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingApprovalScreen(currentUser: UserProfile, onLogout: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var editName by remember { mutableStateOf(currentUser.name) }
    var updateFeedback by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE50914).copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, Color(0xFFE50914).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFE50914),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ACCESS RESTRICTED",
                color = Color(0xFFE50914),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Membership Access Pending",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "To protect Rawat FC team privacy, your squad profile must be authorized by an executive administrator before you can join chats or view training scheduled details.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Interactive info block
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "YOUR PROFILE INFORMATION",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Display Fields
                    Column {
                        Text("Linked Email Address", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        Text(currentUser.email, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Column {
                        Text("Requested Access Role", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        Text(currentUser.role.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Editable Name field! ("both users can update their names")
                    Column {
                        Text("Edit Display Name", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            placeholder = { Text("Enter your full name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFE50914),
                                containerColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (editName.isNotBlank()) {
                                val updated = currentUser.copy(name = editName)
                                // Update in users array
                                val index = ClubRepository.users.indexOfFirst { it.id == currentUser.id }
                                if (index != -1) {
                                    ClubRepository.users[index] = updated
                                }
                                ClubRepository.currentUser.value = updated
                                ClubRepository.saveAllData()
                                updateFeedback = "Display Name updated successfully!"
                            } else {
                                updateFeedback = "Name cannot be empty."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("UPDATE PROFILE NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (updateFeedback != null) {
                        Text(
                            text = updateFeedback!!,
                            color = if (updateFeedback!!.contains("successfully")) Color(0xFF4CAF50) else Color(0xFFE50914),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        LaunchedEffect(updateFeedback) {
                            delay(4000)
                            updateFeedback = null
                        }
                    }

                    if (currentUser.role == UserRole.ADMIN) {
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        Text(
                            text = "Admin setup requires email dispatch verification:",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                        Button(
                            onClick = {
                                val selectorIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("mailto:")
                                }
                                val emailIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("hamxak441@gmail.com"))
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Rawat FC Admin Registration Request: ${editName}")
                                    val bodyText = """
                                        Hello Head Admin,

                                        I am requesting Admin access for Rawat FC.

                                        Name: ${editName}
                                        Email: ${currentUser.email}

                                        Please approve my credentials under player/admin approvals section inside the Rawat FC Manager app.

                                        Best,
                                        ${editName}
                                    """.trimIndent()
                                    putExtra(android.content.Intent.EXTRA_TEXT, bodyText)
                                    selector = selectorIntent
                                }
                                try {
                                    val chooser = android.content.Intent.createChooser(emailIntent, "Send Admin Request...")
                                    chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(chooser)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DISPATCH VERIFICATION EMAIL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = {
                ClubRepository.logout()
                onLogout()
            }) {
                Text("Exit Current Session", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
