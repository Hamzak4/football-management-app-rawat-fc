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
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var activeTab by remember { mutableStateOf("HOME") } // "HOME", "CHAT", "MATCHES", "TRAINING", "TOURNAMENT", "PROFILE", "MEDIA", "ATTENDANCE", "ADMIN"

    val currentNotification = ClubRepository.currentNotification.value
    val currentUser = ClubRepository.currentUser.value
    val isCoachOrAdmin = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.ADMIN

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
