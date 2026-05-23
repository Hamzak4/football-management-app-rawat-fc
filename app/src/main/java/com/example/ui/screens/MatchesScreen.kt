package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.launch

@Composable
fun MatchesScreen() {
    var selectedMatch by remember { mutableStateOf<MatchFixture?>(null) }
    
    AnimatedContent(
        targetState = selectedMatch,
        transitionSpec = {
            slideInHorizontally { width -> width } + fadeIn() togetherWith
            slideOutHorizontally { width -> -width } + fadeOut()
        },
        label = "MatchSlide"
    ) { match ->
        if (match == null) {
            FixturesListScreen(onMatchSelected = { selectedMatch = it })
        } else {
            MatchDetailScreen(match = match, onBack = { selectedMatch = null })
        }
    }
}

@Composable
fun FixturesListScreen(onMatchSelected: (MatchFixture) -> Unit) {
    val fixtures = ClubRepository.matches
    var tabSelected by remember { mutableStateOf("UPCOMING") } // "UPCOMING" or "FINISHED"

    val filtered = fixtures.filter {
        if (tabSelected == "UPCOMING") it.status == MatchStatus.UPCOMING
        else it.status == MatchStatus.FINISHED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("UPCOMING" to "Fixtures Queue", "FINISHED" to "Past Scoreboards").forEach { tab ->
                val isSelected = tabSelected == tab.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(0xFFE50914) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { tabSelected = tab.first }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.second.uppercase(),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List Area
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "No matches registered in this queue.",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { match ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .clickable { onMatchSelected(match) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Sub title header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (match.isHome) "HOME GAME" else "AWAY GAME",
                                    color = Color(0xFFE50914),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = match.date,
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Score / Teams section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Rawat FC
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFE50914), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("RFC", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                    Text(
                                        "Rawat FC",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                // Score display or VS
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (match.status == MatchStatus.FINISHED) {
                                        Text(
                                            text = "${match.homeGoals} - ${match.awayGoals}",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    } else {
                                        Text(
                                            text = "VS",
                                            color = Color.White.copy(alpha = 0.3f),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                // Opponent
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White.copy(alpha = 0.05f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(match.opponentLogoUrl, fontSize = 18.sp)
                                    }
                                    Text(
                                        match.opponent,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Venue / Kickoff footer
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
                                    Text(match.venueName, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text(
                                    text = "Kickoff ${match.time}",
                                    color = Color(0xFFE50914),
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

@Composable
fun MatchDetailScreen(match: MatchFixture, onBack: () -> Unit) {
    var subTabSelected by remember { mutableStateOf("TACTICS") } // "TACTICS", "STATS", "TIMELINE"
    
    // AI summarizer state
    var aiLoading by remember { mutableStateOf(false) }
    var aiReportResult by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Custom Back Button Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "MATCH DOSSIER DETAILS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Main match scoreboard card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = match.venueName.uppercase(),
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.size(48.dp).background(Color(0xFFE50914), CircleShape), contentAlignment = Alignment.Center) {
                                Text("RFC", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Rawat FC", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = if (match.status == MatchStatus.FINISHED) "${match.homeGoals} - ${match.awayGoals}" else "VS",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.05f), CircleShape), contentAlignment = Alignment.Center) {
                                Text(match.opponentLogoUrl, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(match.opponent, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Kickoff: ${match.date} at ${match.time}",
                        color = Color(0xFFE50914),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tab selection for match details subviews
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                listOf("TACTICS" to "Tactical Pitch", "STATS" to "Squad Stats", "TIMELINE" to "Timeline Logs").forEach { tab ->
                    val isSelected = subTabSelected == tab.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) Color(0xFF1F1F1F) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { subTabSelected = tab.first }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.second,
                            color = if (isSelected) Color(0xFFE50914) else Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Subviews renders
        when (subTabSelected) {
            "TACTICS" -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "STARTING LINUPE & FORMATION (${match.formation})",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Visual Football Pitch Canvas Drawing positions
                        PitchLayout(formation = match.formation)
                    }
                }
            }
            "STATS" -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "MATCH COMPARATIVE STATS",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Renders stat bar scales
                            val stats = match.matchStats
                            StatProgressBar(label = "Possession", valueHome = stats.possessionHome, valueAway = stats.possessionAway, isPercent = true)
                            StatProgressBar(label = "Shots Taken", valueHome = stats.shotsHome, valueAway = stats.shotsAway)
                            StatProgressBar(label = "Shots On Target", valueHome = stats.shotsOnTargetHome, valueAway = stats.shotsOnTargetAway)
                            StatProgressBar(label = "Corners Awarded", valueHome = stats.cornersHome, valueAway = stats.cornersAway)
                            StatProgressBar(label = "Fouls Committed", valueHome = stats.foulsHome, valueAway = stats.foulsAway)
                        }
                    }
                }
            }
            "TIMELINE" -> {
                if (match.goals.isEmpty() && match.cards.isEmpty() && match.substitutions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No timeline logs logged. Upcoming match fixture.", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                        }
                    }
                } else {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("TIMELINE INCIDENTS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                // Render Goals timeline, cards, subs combined or separate
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    match.goals.forEach { goal ->
                                        TimelineItem(
                                            minute = goal.minute,
                                            title = if (goal.isTeamGoal) "GOAL! RAWAT FC" else "GOAL! ${match.opponent.uppercase()}",
                                            sub = goal.scorer,
                                            icon = Icons.Default.SportsSoccer,
                                            iconColor = if (goal.isTeamGoal) Color(0xFFE50914) else Color.White.copy(alpha = 0.4f)
                                        )
                                    }
                                    match.cards.forEach { card ->
                                        TimelineItem(
                                            minute = card.minute,
                                            title = if (card.isYellow) "Yellow Card" else "Red Card",
                                            sub = card.playerName,
                                            icon = Icons.Default.Warning,
                                            iconColor = if (card.isYellow) Color(0xFFFFEB3B) else Color(0xFFE50914)
                                        )
                                    }
                                    match.substitutions.forEach { sub ->
                                        TimelineItem(
                                            minute = sub.minute,
                                            title = "Substitution",
                                            sub = "In: ${sub.playerIn} • Out: ${sub.playerOut}",
                                            icon = Icons.Default.SwapHoriz,
                                            iconColor = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live AI Match analysis generator (Gemini implementation)
        if (match.status == MatchStatus.FINISHED) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE50914).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                                Text("AI INTELLIGENT MATCH REPORT", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            if (aiReportResult.isEmpty() && !aiLoading) {
                                Button(
                                    onClick = {
                                        aiLoading = true
                                        coroutineScope.launch {
                                            val reportPrompt = "Generate an editorial football match report analysis for Rawat FC vs ${match.opponent}. Real statistics: Rawat score ${match.homeGoals}, Opponent score ${match.awayGoals}, Possession: Rawat ${match.matchStats.possessionHome}%, Shots on target ${match.matchStats.shotsOnTargetHome}. Details of incident goals: ${match.goals.map { "${it.scorer} - Minute ${it.minute}" }}."
                                            aiReportResult = ClubRepository.getGeminiAIResponse(reportPrompt)
                                            aiLoading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Analyze", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (aiLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFE50914), modifier = Modifier.size(24.dp))
                            }
                        }

                        // Display active static match summary if not queried, otherwise present Gemini Live results
                        val mainDocText = if (aiReportResult.isNotEmpty()) aiReportResult else match.summary
                        if (mainDocText.isNotEmpty() && !aiLoading) {
                            Text(
                                text = mainDocText,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.5.sp,
                                modifier = Modifier.padding(top = 10.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatProgressBar(label: String, valueHome: Int, valueAway: Int, isPercent: Boolean = false) {
    val total = if (valueHome + valueAway == 0) 1 else (valueHome + valueAway)
    val ratio = valueHome.toFloat() / total.toFloat()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$valueHome${if (isPercent) "%" else ""}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(label.uppercase(), color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("$valueAway${if (isPercent) "%" else ""}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        
        // Progress scale
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (ratio == 0f) 0.01f else ratio)
                    .background(Color(0xFFE50914))
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (1f - ratio == 0f) 0.01f else 1f - ratio)
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
fun TimelineItem(minute: Int, title: String, sub: String, icon: ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Minute column
        Text(
            text = "$minute'",
            color = Color(0xFFE50914),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )

        // Event Badge Icon
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, iconColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
        }

        // Details column
        Column {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
        }
    }
}

@Composable
fun PitchLayout(formation: String) {
    // Elegant emerald-green visual football field
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E3512)) // Deep Pitch Lawn Green
            .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        // Draw pitch marking lines via Canvas draw elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Pitch center line
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f),
                strokeWidth = 2.dp.toPx()
            )

            // Center circle
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = 45.dp.toPx(),
                center = Offset(w / 2f, h / 2f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Inside Center dot
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = 4.dp.toPx(),
                center = Offset(w / 2f, h / 2f)
            )

            // Penalty box top
            drawRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(w / 4f, 0f),
                size = Size(w / 2f, h / 6f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Penalty box bottom
            drawRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(w / 4f, h - (h / 6f)),
                size = Size(w / 2f, h / 6f),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Overlay Player Jerseys based on 4-3-3 formation
        // GK, 4 Defenders, 3 Midfielders, 3 Strikers
        Box(modifier = Modifier.fillMaxSize()) {
            // Keepers Anchor
            PlayerNode(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                jersey = "1", name = "Bilal"
            )

            // Defensive Line of 4
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 65.dp, start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlayerNode(jersey = "2", name = "Faisal")
                PlayerNode(jersey = "4", name = "Usman")
                PlayerNode(jersey = "5", name = "CB")
                PlayerNode(jersey = "3", name = "RB")
            }

            // Midfield Trio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = 40.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlayerNode(jersey = "8", name = "Adil")
                PlayerNode(jersey = "10", name = "Hamza")
                PlayerNode(jersey = "6", name = "CM")
            }

            // Forward Line of 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlayerNode(jersey = "11", name = "Kamil")
                PlayerNode(jersey = "9", name = "Zain")
                PlayerNode(jersey = "7", name = "LW")
            }
        }
    }
}

@Composable
fun PlayerNode(modifier: Modifier = Modifier, jersey: String, name: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Red jersey circle circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFE50914), CircleShape)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = jersey,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = name,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}
