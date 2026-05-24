package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen() {
    var user by remember { mutableStateOf(ClubRepository.currentUser.value) }
    
    // Listen to repository changes and sync local state of screen
    LaunchedEffect(ClubRepository.currentUser.value) {
        user = ClubRepository.currentUser.value
    }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active session authorized.", color = Color.White.copy(alpha = 0.5f))
        }
    } else {
        ProfileContentScreen(userProfile = user!!, onProfileUpdated = { updated ->
            val index = ClubRepository.users.indexOfFirst { it.id == updated.id }
            if (index != -1) {
                ClubRepository.users[index] = updated
            }
            ClubRepository.currentUser.value = updated
            user = updated
            ClubRepository.saveUsers()
            ClubRepository.broadcastSystemNotification("👤 Profile Saved Successfully")
        })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContentScreen(userProfile: UserProfile, onProfileUpdated: (UserProfile) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    
    // Editing Text fields
    var editName by remember { mutableStateOf(userProfile.name) }
    var editBio by remember { mutableStateOf(userProfile.bio) }
    var editPhone by remember { mutableStateOf(userProfile.phoneNumber) }
    var editAge by remember { mutableStateOf(userProfile.age.toString()) }
    var editJersey by remember { mutableStateOf(userProfile.jerseyNumber?.toString() ?: "") }
    var editFitness by remember { mutableStateOf(userProfile.fitnessStatus) }

    // AI suggestion states
    var aiLoading by remember { mutableStateOf(false) }
    var aiTacticsText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Header Grid (Crest Circle & Edit Button)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PLAYER PROFILE CENTER",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = userProfile.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Jersey #${userProfile.jerseyNumber ?: "—"} • ${userProfile.position?.name ?: "STAFF"}",
                        color = Color(0xFFE50914),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Edit Button
                Button(
                    onClick = {
                        if (isEditing) {
                            // save trigger
                            val parsedAge = editAge.toIntOrNull() ?: userProfile.age
                            val parsedJersey = editJersey.toIntOrNull()
                            val updated = userProfile.copy(
                                name = editName,
                                bio = editBio,
                                phoneNumber = editPhone,
                                age = parsedAge,
                                jerseyNumber = parsedJersey,
                                fitnessStatus = editFitness
                            )
                            onProfileUpdated(updated)
                            isEditing = false
                        } else {
                            editName = userProfile.name
                            editBio = userProfile.bio
                            editPhone = userProfile.phoneNumber
                            editAge = userProfile.age.toString()
                            editJersey = userProfile.jerseyNumber?.toString() ?: ""
                            editFitness = userProfile.fitnessStatus
                            isEditing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEditing) "SAVE" else "EDIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Expanded Profile Details (Read mode vs Edit mode)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("BIOGRAPHY & CONFIG", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    if (isEditing) {
                        // Editable text input fields
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name/Nickname", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Short Bio biography detail...", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Phone Number", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text("Age", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.5f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editJersey,
                                onValueChange = { editJersey = it },
                                label = { Text("Jersey ID", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFFE50914)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.5f)
                            )
                            
                            // Fitness toggle selector
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fitness Gauges:", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                                    FitnessStatus.values().forEach { st ->
                                        val isStSel = editFitness == st
                                        Box(
                                            modifier = Modifier
                                                .background(if (isStSel) Color(0xFFE50914) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                                .clickable { editFitness = st }
                                                .padding(horizontal = 6.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(st.name.take(3), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Static Read-only view
                        Text(
                            text = if (userProfile.bio.isNotEmpty()) userProfile.bio else "No personal biography statement registered under this squad profile yet.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.03f))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProfileRowDetail("Chronological Age", "${userProfile.age} yrs old")
                            ProfileRowDetail("Contact Phone", if (userProfile.phoneNumber.isNotEmpty()) userProfile.phoneNumber else "None registered")
                            
                            // Fitness Gage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fitness Gauge Status", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (userProfile.fitnessStatus) {
                                                FitnessStatus.FIT -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                                FitnessStatus.RECOVERING -> Color(0xFFFFA000).copy(alpha = 0.15f)
                                                FitnessStatus.INJURED -> Color(0xFFE50914).copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = userProfile.fitnessStatus.name,
                                        color = when (userProfile.fitnessStatus) {
                                            FitnessStatus.FIT -> Color(0xFF4CAF50)
                                            FitnessStatus.RECOVERING -> Color(0xFFFFA000)
                                            FitnessStatus.INJURED -> Color(0xFFE50914)
                                        },
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

        // Performance Scorecard Metrics Grid
        if (userProfile.role == UserRole.PLAYER || userProfile.role == UserRole.CAPTAIN) {
            item {
                Column {
                    Text(
                        text = "HISTORICAL MATCH COMPILATION STATS",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GridStatCard("Caps", "${userProfile.matchesPlayed}", "Played", Modifier.weight(1f))
                        GridStatCard("Goals", "${userProfile.goalsScored}", "Scored", Modifier.weight(1f))
                        GridStatCard("Assists", "${userProfile.assists}", "Assisted", Modifier.weight(1f))
                        GridStatCard("Rating", "${userProfile.rating} ★", "Avg score", Modifier.weight(1f))
                    }
                }
            }
        }

        // AI predicted performance assistant (Gemini integration list)
        if (userProfile.role == UserRole.PLAYER || userProfile.role == UserRole.CAPTAIN) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131005)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFFA000).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                                Text("AI TACTICAL COMPLIANCE ANALYSIS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            if (aiTacticsText.isEmpty() && !aiLoading) {
                                Button(
                                    onClick = {
                                        aiLoading = true
                                        scope.launch {
                                            val tacticsPrompt = "Formulate a personalized tactical coaching insight dossier for a Rawat FC player. Name: ${userProfile.name}, Position: ${userProfile.position?.name}, Age: ${userProfile.age}, Fitness: ${userProfile.fitnessStatus.name}, Matches: ${userProfile.matchesPlayed}, Goals: ${userProfile.goalsScored}, Assists: ${userProfile.assists}, Yellow Cards: ${userProfile.yellowCards}. Give specific tactical tips."
                                            aiTacticsText = ClubRepository.getGeminiAIResponse(tacticsPrompt)
                                            aiLoading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Advise", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        if (aiLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFFFA000), modifier = Modifier.size(24.dp))
                            }
                        }

                        if (aiTacticsText.isNotEmpty() && !aiLoading) {
                            Text(
                                text = aiTacticsText,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp),
                                lineHeight = 17.sp
                            )
                        } else if (!aiLoading && aiTacticsText.isEmpty()) {
                            Text(
                                "Request live deep analysis from Gemini AI based on your match numbers and tactical fitness history.",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.5.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileRowDetail(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GridStatCard(label: String, value: String, sub: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color(0xFFE50914), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Text(sub, color = Color.White.copy(alpha = 0.3f), fontSize = 8.sp)
        }
    }
}
