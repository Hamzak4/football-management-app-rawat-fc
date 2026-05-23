package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@Composable
fun TrainingScreen() {
    var selectedSessionId by remember { mutableStateOf<String?>(null) }
    val sessions = ClubRepository.trainings

    val activeSession = sessions.find { it.id == selectedSessionId }

    AnimatedContent(
        targetState = activeSession,
        transitionSpec = {
            slideInHorizontally { w -> w } + fadeIn() togetherWith
            slideOutHorizontally { w -> -w } + fadeOut()
        },
        label = "TrainSlide"
    ) { session ->
        if (session == null) {
            SessionsDashboardList(onSelect = { selectedSessionId = it.id })
        } else {
            SessionExpandDetails(session = session, onBack = { selectedSessionId = null })
        }
    }
}

@Composable
fun SessionsDashboardList(onSelect: (TrainingSession) -> Unit) {
    val sessions = ClubRepository.trainings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "TRAINING LOG ARCHIVES",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Squad Athletic Training",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (sessions.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No training run sessions registered.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .clickable { onSelect(session) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Category top
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE50914).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("PRACTICE DRILLS", color = Color(0xFFE50914), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                                Text(
                                    text = session.date,
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = session.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = session.description,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Venue Details
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
                                    Text(session.venue, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text(
                                    text = session.time,
                                    color = Color(0xFFE50914),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
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
fun SessionExpandDetails(session: TrainingSession, onBack: () -> Unit) {
    val currentRole = ClubRepository.currentUser.value?.role
    val isAdminOrCoach = currentRole == UserRole.COACH || currentRole == UserRole.ADMIN

    // Re-trigger recomposition when attendance modifications happen
    var updatedRoster by remember { mutableStateOf(session.attendance) }
    
    // Listen to changes
    LaunchedEffect(session.attendance) {
        updatedRoster = session.attendance
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button Hub
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SQUAD RUN DETAILS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Broad Card Heading
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.date.uppercase(),
                            color = Color(0xFFE50914),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = session.time,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = session.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = session.description,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Drills Pipeline Timeline
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DRILL OPERATIONS TIMELINE",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (session.drills.isEmpty()) {
                    Text("No drilling focus registered for this run.", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            session.drills.forEachIndexed { i, drill ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFFE50914).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${i + 1}", color = Color(0xFFE50914), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(drill.name, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        Text("Focus: ${drill.focus}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                    }

                                    Text(
                                        text = "${drill.durationMinutes} min",
                                        color = Color(0xFFE50914),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Coach Notes Box
        if (session.coachNotes.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3512).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sports,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text("COACH DIRECTION NOTES", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                            Text(
                                text = session.coachNotes,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Attendance marking visual list roster
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SQUAD ATTENDANCE ROSTER",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val rosterPlayers = ClubRepository.users.filter { it.role == UserRole.PLAYER || it.role == UserRole.CAPTAIN }
                        
                        rosterPlayers.forEach { player ->
                            val status = updatedRoster[player.id] ?: AttendanceStatus.UNMARKED
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left details
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Profile Image
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color.White.copy(alpha = 0.05f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(player.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Column {
                                        Text(player.name, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "#${player.jerseyNumber ?: "N/A"} • ${player.position?.name ?: "SQUAD"}",
                                            color = Color.White.copy(alpha = 0.4f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                // Interactive State controls (If admin/coach, allow clicking, else show status badge read-only)
                                if (isAdminOrCoach) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        AttendanceMiniButton("P", status == AttendanceStatus.PRESENT, Color(0xFF4CAF50)) {
                                            ClubRepository.markAttendance(session.id, player.id, AttendanceStatus.PRESENT)
                                            updatedRoster = updatedRoster.toMutableMap().apply { this[player.id] = AttendanceStatus.PRESENT }
                                        }
                                        AttendanceMiniButton("L", status == AttendanceStatus.LATE, Color(0xFFFFA000)) {
                                            ClubRepository.markAttendance(session.id, player.id, AttendanceStatus.LATE)
                                            updatedRoster = updatedRoster.toMutableMap().apply { this[player.id] = AttendanceStatus.LATE }
                                        }
                                        AttendanceMiniButton("A", status == AttendanceStatus.ABSENT, Color(0xFFE50914)) {
                                            ClubRepository.markAttendance(session.id, player.id, AttendanceStatus.ABSENT)
                                            updatedRoster = updatedRoster.toMutableMap().apply { this[player.id] = AttendanceStatus.ABSENT }
                                        }
                                    }
                                } else {
                                    // Static Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (status) {
                                                    AttendanceStatus.PRESENT -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                                    AttendanceStatus.LATE -> Color(0xFFFFA000).copy(alpha = 0.15f)
                                                    AttendanceStatus.ABSENT -> Color(0xFFE50914).copy(alpha = 0.15f)
                                                    else -> Color.White.copy(alpha = 0.05f)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = status.name,
                                            color = when (status) {
                                                AttendanceStatus.PRESENT -> Color(0xFF4CAF50)
                                                AttendanceStatus.LATE -> Color(0xFFFFA000)
                                                AttendanceStatus.ABSENT -> Color(0xFFE50914)
                                                else -> Color.White.copy(alpha = 0.4f)
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceMiniButton(label: String, isSelected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (isSelected) activeColor else Color.White.copy(alpha = 0.05f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
