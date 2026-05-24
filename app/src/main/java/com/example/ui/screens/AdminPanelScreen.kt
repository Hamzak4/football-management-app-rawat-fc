package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen() {
    val currentUser = ClubRepository.currentUser.value
    val isCoachOrAdmin = currentUser?.role == UserRole.COACH || currentUser?.role == UserRole.ADMIN

    // Access Denied Shield if the participant is a standard player
    if (!isCoachOrAdmin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1414)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, Color(0xFFE50914).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "🔒 LOCK",
                        tint = Color(0xFFE50914),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "ADMIN ACCESS STRICTLY RESTRICTED",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Only Rawat FC head coaches and directors are authorized to post notices, register match scorelines, or schedule squad training days.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        return
    }

    // Dynamic Admin Sections
    var expandedSection by remember { mutableStateOf("MATCH") } // "MATCH", "TRAN", "ANNC", "APPROVALS"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Heading
        item {
            Column {
                Text(
                    text = "CLUB EXECUTIVE OPERATIONS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Administrative Dashboard",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Section Selectors Row
        item {
            val pendingCount = ClubRepository.users.count { !it.isApproved }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "MATCH" to "Match",
                    "TRAN" to "Training",
                    "ANNC" to "Notice",
                    "APPROVALS" to "Approvals ($pendingCount)"
                ).forEach { sec ->
                    val isChosen = expandedSection == sec.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isChosen) Color(0xFFE50914) else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { expandedSection = sec.first }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sec.second, color = if (isChosen) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Expanded Action Forms based on section selected
        when (expandedSection) {
            "MATCH" -> {
                item {
                    var opp by remember { mutableStateOf("") }
                    var date by remember { mutableStateOf("2026-05-29") }
                    var time by remember { mutableStateOf("17:30") }
                    var venue by remember { mutableStateOf("Rawat FC Stadium, Islamabad") }
                    var isHome by remember { mutableStateOf(true) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("ADD FUTURE FIXTURE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = opp,
                                onValueChange = { opp = it },
                                placeholder = { Text("E.g. Rawat United, Jinnah Athletic...") },
                                label = { Text("Opponent Team Name") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = venue,
                                onValueChange = { venue = it },
                                label = { Text("Match Venue Address") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = date,
                                    onValueChange = { date = it },
                                    label = { Text("Fixture Date (YYYY-MM-DD)") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.2f)
                                )

                                OutlinedTextField(
                                    value = time,
                                    onValueChange = { time = it },
                                    label = { Text("Kickoff (HH:MM)") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(0.8f)
                                )
                            }

                            // Home/Away toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Arena Side:", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(true to "Home Game", false to "Away Game").forEach { field ->
                                        val isChosen = isHome == field.first
                                        Box(
                                            modifier = Modifier
                                                .background(if (isChosen) Color(0xFFE50914) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                                .clickable { isHome = field.first }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(field.second, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    if (opp.isNotEmpty() && venue.isNotEmpty()) {
                                        ClubRepository.addMatchFixture(opp, isHome, venue, date, time)
                                        opp = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REGISTER FIXTURE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            "TRAN" -> {
                item {
                    var title by remember { mutableStateOf("") }
                    var desc by remember { mutableStateOf("") }
                    var date by remember { mutableStateOf("2026-05-25") }
                    var time by remember { mutableStateOf("08:00") }
                    var venue by remember { mutableStateOf("Rawat FC Ground, Islamabad") }
                    var notes by remember { mutableStateOf("") }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("SCHEDULE RAW SQUAD WORKOUT", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("E.g. Crossing Practice, Tactical high press...") },
                                label = { Text("Session Operation Title") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = desc,
                                onValueChange = { desc = it },
                                placeholder = { Text("Warm ups, fitness triggers, recovery logs...") },
                                label = { Text("Methodology Description") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Coach Tactical Safety Directives") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = date,
                                    onValueChange = { date = it },
                                    label = { Text("Scheduled Date") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = time,
                                    onValueChange = { time = it },
                                    label = { Text("Starting Time") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(0.8f)
                                )
                            }

                            Button(
                                onClick = {
                                    if (title.isNotEmpty() && venue.isNotEmpty()) {
                                        val drills = listOf(
                                            Drill("Dynamic Rondo", "Aerobic focus", 15),
                                            Drill("Target Overloads", "Shooting accuracy", 25)
                                        )
                                        ClubRepository.addTrainingSession(title, desc, date, time, venue, notes, drills)
                                        title = ""
                                        desc = ""
                                        notes = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LAUNCH TRAINING REGISTER", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            "ANNC" -> {
                item {
                    var title by remember { mutableStateOf("") }
                    var content by remember { mutableStateOf("") }
                    var category by remember { mutableStateOf(AnnouncementCategory.URGENT) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("BROADCAST RELEASABLE BULLETIN", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Urgent rescheduled notice...") },
                                label = { Text("Alert Broadcast Header") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                placeholder = { Text("Alert body statement details...") },
                                label = { Text("Operational Details Content") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Category Selector
                            Column {
                                Text("Dispatch Severity Group:", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val cats = listOf(AnnouncementCategory.URGENT, AnnouncementCategory.GENERAL, AnnouncementCategory.MATCH_DAY)
                                    cats.forEach { cat ->
                                        val isChosen = category == cat
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (isChosen) Color(0xFFE50914) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                                .clickable { category = cat }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(cat.name, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (title.isNotEmpty() && content.isNotEmpty()) {
                                        ClubRepository.addAnnouncement(title, content, category)
                                        title = ""
                                        content = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("BROADCAST TO ENTIRE TEAM", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            "APPROVALS" -> {
                val pendingUsers = ClubRepository.users.filter { !it.isApproved }
                if (pendingUsers.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = "No Pending Approvals",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "All registered players and admins have been fully authorized to access the club roster.",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    pendingUsers.forEach { pending ->
                        item(key = pending.id) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF181111)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = pending.name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = pending.email,
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontSize = 11.sp
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (pending.role == UserRole.ADMIN) Color(0xFFE50914) else Color(
                                                        0xFF2196F3
                                                    ), RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = pending.role.name,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                val updated = pending.copy(isApproved = true)
                                                val index = ClubRepository.users.indexOfFirst { it.id == pending.id }
                                                if (index != -1) {
                                                    ClubRepository.users[index] = updated
                                                    ClubRepository.saveAllData()
                                                    ClubRepository.broadcastSystemNotification("✅ Approved ${pending.name}")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(1.5f)
                                                .height(32.dp)
                                        ) {
                                            Text(
                                                text = "APPROVE SQUAD MEMBER",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                val index = ClubRepository.users.indexOfFirst { it.id == pending.id }
                                                if (index != -1) {
                                                    ClubRepository.users.removeAt(index)
                                                    ClubRepository.saveAllData()
                                                    ClubRepository.broadcastSystemNotification("❌ Rejected ${pending.name}")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .height(32.dp)
                                        ) {
                                            Text(
                                                text = "REJECT",
                                                fontSize = 10.sp,
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
}
