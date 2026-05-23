package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@Composable
fun TournamentScreen() {
    var tabSelected by remember { mutableStateOf("STANDINGS") } // "STANDINGS" or "BRACKET"
    val standings = ClubRepository.leagueStandings
    val bracket = ClubRepository.tournamentBracket

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Heading
        Column {
            Text(
                text = "CAMPAIGN & TOURNAMENTS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Twin Cities Super Cup",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Segmented selector tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            listOf("STANDINGS" to "League Standings", "BRACKET" to "Knockout Tree").forEach { tab ->
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
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sub views
        if (tabSelected == "STANDINGS") {
            // Renders standard robust scrollable league table
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // League heading indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#  TEAM SQUAD", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.width(135.dp)
                    ) {
                        Text("P", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                        Text("W", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                        Text("GD", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text("PTS", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.05f))

                // Stands rows items
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(standings) { team ->
                        val isRawat = team.teamName.contains("Rawat FC", ignoreCase = true)
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isRawat) Color(0xFFE50914).copy(alpha = 0.1f) else Color(0xFF111111)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isRawat) Color(0xFFE50914).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${team.rank}",
                                        color = if (isRawat) Color(0xFFE50914) else Color.White.copy(alpha = 0.5f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.width(16.dp)
                                    )
                                    Text(
                                        text = team.teamName,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (isRawat) FontWeight.ExtraBold else FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.width(135.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${team.played}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    Text("${team.won}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    Text(
                                        text = (if (team.gd > 0) "+" else "") + "${team.gd}",
                                        color = if (team.gd > 0) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(32.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "${team.points}",
                                        color = if (isRawat) Color(0xFFE50914) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.width(35.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Renders knockout visual brackets rows
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bracket) { bMatch ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Round header label
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFE50914), modifier = Modifier.size(14.dp))
                                    Text(bMatch.round.uppercase(), color = Color(0xFFE50914), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                                }
                                Text(bMatch.date, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Team A
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(bMatch.teamA, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = bMatch.scoreA?.toString() ?: "—",
                                    color = if (bMatch.scoreA != null && bMatch.scoreB != null && bMatch.scoreA > bMatch.scoreB) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Separator line
                            Divider(color = Color.White.copy(alpha = 0.03f))

                            Spacer(modifier = Modifier.height(10.dp))

                            // Team B
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(bMatch.teamB, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = bMatch.scoreB?.toString() ?: "—",
                                    color = if (bMatch.scoreA != null && bMatch.scoreB != null && bMatch.scoreB > bMatch.scoreA) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
