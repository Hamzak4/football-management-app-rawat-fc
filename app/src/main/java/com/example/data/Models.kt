package com.example.data

import java.time.LocalDateTime

enum class UserRole {
    ADMIN, COACH, CAPTAIN, PLAYER
}

enum class PlayerPosition {
    STRIKER, MIDFIELDER, DEFENDER, GOALKEEPER
}

enum class FitnessStatus {
    FIT, INJURED, RECOVERING
}

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, UNMARKED
}

enum class MatchStatus {
    UPCOMING, LIVE, FINISHED
}

enum class AnnouncementCategory {
    URGENT, GENERAL, MATCH_DAY, TRAINING
}

enum class GroupType {
    MAIN, TRAINING, MATCH_DAY, MANAGEMENT, TOURNAMENT, INJURY
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val jerseyNumber: Int? = null,
    val position: PlayerPosition? = null,
    val phoneNumber: String = "",
    val age: Int = 22,
    val bio: String = "",
    val fitnessStatus: FitnessStatus = FitnessStatus.FIT,
    val matchesPlayed: Int = 0,
    val goalsScored: Int = 0,
    val assists: Int = 0,
    val cleanSheets: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val rating: Float = 7.5f,
    val profilePic: String = "",
    val isApproved: Boolean = true
)

data class GoalTimeline(
    val minute: Int,
    val scorer: String,
    val isTeamGoal: Boolean
)

data class CardTimeline(
    val minute: Int,
    val playerName: String,
    val isYellow: Boolean
)

data class SubTimeline(
    val minute: Int,
    val playerIn: String,
    val playerOut: String
)

data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0
)

data class MatchFixture(
    val id: String,
    val opponent: String,
    val opponentLogoUrl: String = "",
    val isHome: Boolean = true,
    val venueName: String,
    val latitude: Double = 33.6844, // Default Islamabad coords
    val longitude: Double = 73.0479,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:MM
    val status: MatchStatus = MatchStatus.UPCOMING,
    val homeGoals: Int = 0,
    val awayGoals: Int = 0,
    val formation: String = "4-3-3",
    val startingXIAIDs: List<String> = emptyList(),
    val subsAIDs: List<String> = emptyList(),
    val goals: List<GoalTimeline> = emptyList(),
    val cards: List<CardTimeline> = emptyList(),
    val substitutions: List<SubTimeline> = emptyList(),
    val matchStats: MatchStats = MatchStats(),
    val summary: String = ""
)

data class Drill(
    val name: String,
    val focus: String,
    val durationMinutes: Int
)

data class TrainingSession(
    val id: String,
    val title: String,
    val description: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:MM
    val venue: String = "Rawat FC Ground, Islamabad",
    val latitude: Double = 33.5255, // Rawat coords
    val longitude: Double = 73.1950,
    val coachNotes: String = "",
    val drills: List<Drill> = emptyList(),
    val attendance: Map<String, AttendanceStatus> = emptyMap() // UserId -> status
)

data class MessageReaction(
    val emoji: String,
    val count: Int,
    val userIds: List<String>
)

data class ChatMessage(
    val id: String,
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val text: String,
    val timestamp: String, // HH:MM or format
    val mediaUrl: String? = null,
    val isImage: Boolean = false,
    val reactions: List<MessageReaction> = emptyList(),
    val readBy: List<String> = emptyList(),
    val isPinned: Boolean = false
)

data class ChatGroup(
    val id: String,
    val name: String,
    val description: String,
    val type: GroupType,
    val lastMessageText: String = "",
    val lastMessageTime: String = "",
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val iconUrl: String = "",
    val memberIds: List<String> = emptyList()
)

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorRole: UserRole,
    val date: String,
    val category: AnnouncementCategory,
    val views: Int = 0,
    val isPinned: Boolean = false
)

data class TeamStanding(
    val rank: Int,
    val teamName: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val gd: Int,
    val points: Int
)

data class BracketMatch(
    val round: String, // "Quarterfinals", "Semifinals", "Final"
    val teamA: String,
    val teamB: String,
    val scoreA: Int?,
    val scoreB: Int?,
    val date: String,
    val isCompleted: Boolean
)

data class MediaItem(
    val id: String,
    val title: String,
    val url: String,
    val category: String, // "MATCHES", "TRAINING", "EVENTS"
    val isVideo: Boolean = false,
    val date: String,
    val uploader: String
)
