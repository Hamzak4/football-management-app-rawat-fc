package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen() {
    val mediaItems = ClubRepository.mediaGallery
    var categoryFilter by remember { mutableStateOf("ALL") } // "ALL", "MATCHES", "TRAINING"

    val filtered = if (categoryFilter == "ALL") mediaItems else mediaItems.filter { it.category == categoryFilter }

    // Roster upload dialog triggers
    var openUploadDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VISUAL ALBUMS & REELS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Rawat FC Media Gallery",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Upload floating action
            IconButton(
                onClick = { openUploadDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE50914), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Media", tint = Color.White)
            }
        }

        // Folder filters layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL" to "All Media", "MATCHES" to "Match Snaps", "TRAINING" to "Practice").forEach { filter ->
                val isSelected = categoryFilter == filter.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(0xFFE50914) else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { categoryFilter = filter.first }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.second,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List Grid View of media
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No media captured in this folder.", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    ) {
                        Column {
                            // Coil Image Renderer
                            AsyncImage(
                                model = item.url,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                contentScale = ContentScale.Crop
                            )

                            // Footer info
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("By ${item.uploader.split(" ").firstOrNull() ?: "Coach"}", color = Color(0xFFE50914), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(item.date.split(",").firstOrNull() ?: "", color = Color.White.copy(alpha = 0.4f), fontSize = 8.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Media Upload simulation dialog form
    if (openUploadDialog) {
        var uploadTitle by remember { mutableStateOf("") }
        var uploadCategory by remember { mutableStateOf("MATCHES") }
        
        AlertDialog(
            onDismissRequest = { openUploadDialog = false },
            title = { Text("SIMULATE PHOTO SNAPSHOT UPLOAD", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Provide simulated metadata and we will automatically inject a professional football visual preset photo into the club repository:", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    
                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        placeholder = { Text("Caption Title...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFE50914),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category picker
                    Column {
                        Text("Category Folder:", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("MATCHES" to "Matches", "TRAINING" to "Training").forEach { cat ->
                                val isChosen = uploadCategory == cat.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (isChosen) Color(0xFFE50914) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                        .clickable { uploadCategory = cat.first }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat.second, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadTitle.isNotEmpty()) {
                            // Pick a high-res soccer placeholder photo preset
                            val presetUrl = when (uploadCategory) {
                                "MATCHES" -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&q=80&w=400"
                                else -> "https://images.unsplash.com/photo-1543351611-58f69d7c1781?auto=format&fit=crop&q=80&w=400"
                            }
                            ClubRepository.addMediaItem(uploadTitle, presetUrl, uploadCategory)
                            openUploadDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
                ) {
                    Text("UPLOAD")
                }
            },
            dismissButton = {
                TextButton(onClick = { openUploadDialog = false }) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.5f))
                }
            },
            containerColor = Color(0xFF111111)
        )
    }
}
