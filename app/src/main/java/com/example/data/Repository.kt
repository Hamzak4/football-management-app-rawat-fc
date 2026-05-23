package com.example.data

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ClubRepository {

    private val okHttpClient = OkHttpClient()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private var appContext: android.content.Context? = null

    fun initialize(context: android.content.Context) {
        appContext = context.applicationContext
        val testFile = context.getFileStreamPath("users.json")
        if (!testFile.exists()) {
            // First time running, save the default initial presets to local file database
            saveAllData()
        } else {
            // Load existing database data
            loadAllData()
        }
    }

    private fun <T> saveListToFile(context: android.content.Context, fileName: String, list: List<T>, itemType: java.lang.reflect.Type) {
        try {
            val adapter = moshi.adapter<List<T>>(itemType)
            val json = adapter.toJson(list)
            context.openFileOutput(fileName, android.content.Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun <T> loadListFromFile(context: android.content.Context, fileName: String, itemType: java.lang.reflect.Type): List<T>? {
        return try {
            val file = context.getFileStreamPath(fileName)
            if (!file.exists()) return null
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val adapter = moshi.adapter<List<T>>(itemType)
            adapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAllData() {
        val context = appContext ?: return
        try {
            saveListToFile(context, "users.json", users.toList(), Types.newParameterizedType(List::class.java, UserProfile::class.java))
            saveListToFile(context, "matches.json", matches.toList(), Types.newParameterizedType(List::class.java, MatchFixture::class.java))
            saveListToFile(context, "trainings.json", trainings.toList(), Types.newParameterizedType(List::class.java, TrainingSession::class.java))
            saveListToFile(context, "chat_groups.json", chatGroups.toList(), Types.newParameterizedType(List::class.java, ChatGroup::class.java))
            saveListToFile(context, "chat_messages.json", chatMessages.toList(), Types.newParameterizedType(List::class.java, ChatMessage::class.java))
            saveListToFile(context, "announcements.json", announcements.toList(), Types.newParameterizedType(List::class.java, Announcement::class.java))
            saveListToFile(context, "media_gallery.json", mediaGallery.toList(), Types.newParameterizedType(List::class.java, MediaItem::class.java))
            saveListToFile(context, "league_standings.json", leagueStandings.toList(), Types.newParameterizedType(List::class.java, TeamStanding::class.java))
            saveListToFile(context, "tournament_bracket.json", tournamentBracket.toList(), Types.newParameterizedType(List::class.java, BracketMatch::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAllData() {
        val context = appContext ?: return
        try {
            val loadedUsers = loadListFromFile<UserProfile>(context, "users.json", Types.newParameterizedType(List::class.java, UserProfile::class.java))
            val loadedMatches = loadListFromFile<MatchFixture>(context, "matches.json", Types.newParameterizedType(List::class.java, MatchFixture::class.java))
            val loadedTrainings = loadListFromFile<TrainingSession>(context, "trainings.json", Types.newParameterizedType(List::class.java, TrainingSession::class.java))
            val loadedChatGroups = loadListFromFile<ChatGroup>(context, "chat_groups.json", Types.newParameterizedType(List::class.java, ChatGroup::class.java))
            val loadedChatMessages = loadListFromFile<ChatMessage>(context, "chat_messages.json", Types.newParameterizedType(List::class.java, ChatMessage::class.java))
            val loadedAnnouncements = loadListFromFile<Announcement>(context, "announcements.json", Types.newParameterizedType(List::class.java, Announcement::class.java))
            val loadedMedia = loadListFromFile<MediaItem>(context, "media_gallery.json", Types.newParameterizedType(List::class.java, MediaItem::class.java))
            val loadedStandings = loadListFromFile<TeamStanding>(context, "league_standings.json", Types.newParameterizedType(List::class.java, TeamStanding::class.java))
            val loadedBracket = loadListFromFile<BracketMatch>(context, "tournament_bracket.json", Types.newParameterizedType(List::class.java, BracketMatch::class.java))

            if (loadedUsers != null && loadedUsers.isNotEmpty()) {
                users.clear()
                users.addAll(loadedUsers)
                val savedActiveUser = loadedUsers.find { it.role == UserRole.CAPTAIN } ?: loadedUsers.firstOrNull()
                if (savedActiveUser != null) {
                    currentUser.value = savedActiveUser
                }
            }
            if (loadedMatches != null && loadedMatches.isNotEmpty()) {
                matches.clear()
                matches.addAll(loadedMatches)
            }
            if (loadedTrainings != null && loadedTrainings.isNotEmpty()) {
                trainings.clear()
                trainings.addAll(loadedTrainings)
            }
            if (loadedChatGroups != null && loadedChatGroups.isNotEmpty()) {
                chatGroups.clear()
                chatGroups.addAll(loadedChatGroups)
            }
            if (loadedChatMessages != null && loadedChatMessages.isNotEmpty()) {
                chatMessages.clear()
                chatMessages.addAll(loadedChatMessages)
            }
            if (loadedAnnouncements != null && loadedAnnouncements.isNotEmpty()) {
                announcements.clear()
                announcements.addAll(loadedAnnouncements)
            }
            if (loadedMedia != null && loadedMedia.isNotEmpty()) {
                mediaGallery.clear()
                mediaGallery.addAll(loadedMedia)
            }
            if (loadedStandings != null && loadedStandings.isNotEmpty()) {
                leagueStandings.clear()
                leagueStandings.addAll(loadedStandings)
            }
            if (loadedBracket != null && loadedBracket.isNotEmpty()) {
                tournamentBracket.clear()
                tournamentBracket.addAll(loadedBracket)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ----------------------------------------------------
    // ACTIVE SESSION STATE
    // ----------------------------------------------------
    val currentUser = mutableStateOf<UserProfile?>(null)
    
    // Floating push notifications broadcast
    val currentNotification = mutableStateOf<String?>(null)

    // ----------------------------------------------------
    // IN-MEMORY COLLECTIONS (PERSISTENT STATE DURING SESSION)
    // ----------------------------------------------------
    val users = mutableStateListOf<UserProfile>()
    val matches = mutableStateListOf<MatchFixture>()
    val trainings = mutableStateListOf<TrainingSession>()
    val chatGroups = mutableStateListOf<ChatGroup>()
    val chatMessages = mutableStateListOf<ChatMessage>()
    val announcements = mutableStateListOf<Announcement>()
    val mediaGallery = mutableStateListOf<MediaItem>()
    val leagueStandings = mutableStateListOf<TeamStanding>()
    val tournamentBracket = mutableStateListOf<BracketMatch>()

    // Typing indicators by GroupId -> List of usernames typing
    val typingIndicators = mutableMapOf<String, String>()

    init {
        loadInitialPresets()
    }

    private fun loadInitialPresets() {
        // Clear previous state (safeguard)
        users.clear()
        matches.clear()
        trainings.clear()
        chatGroups.clear()
        chatMessages.clear()
        announcements.clear()
        mediaGallery.clear()
        leagueStandings.clear()
        tournamentBracket.clear()

        // 1. Populate Users (Players, Coaches, admin profiles)
        val p1 = UserProfile(
            id = "c1",
            name = "Rashid Khan",
            email = "coach@rawatfc.com",
            role = UserRole.COACH,
            phoneNumber = "0333-1234567",
            age = 45,
            bio = "UEFA B License holder. Head coach of Rawat FC since 2022. Passionate about developmental football and tactical discipline.",
            profilePic = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&q=80&w=200"
        )
        val p2 = UserProfile(
            id = "p1",
            name = "Hamza Khan",
            email = "hamza@rawatfc.com",
            role = UserRole.CAPTAIN,
            jerseyNumber = 10,
            position = PlayerPosition.MIDFIELDER,
            phoneNumber = "0321-7654321",
            age = 24,
            bio = "Creative attacking midfielder. Dictates play from the center. Former Islamabad Youth captain.",
            fitnessStatus = FitnessStatus.FIT,
            matchesPlayed = 8,
            goalsScored = 5,
            assists = 6,
            yellowCards = 1,
            rating = 8.4f,
            profilePic = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=200"
        )
        val p3 = UserProfile(
            id = "p2",
            name = "Zain Malik",
            email = "zain@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 9,
            position = PlayerPosition.STRIKER,
            phoneNumber = "0312-9876543",
            age = 23,
            bio = "Pacy striker who loves to play on the shoulder of defenders. Rawat's leading goal scorer.",
            fitnessStatus = FitnessStatus.FIT,
            matchesPlayed = 8,
            goalsScored = 9,
            assists = 2,
            rating = 8.1f,
            profilePic = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=200"
        )
        val p4 = UserProfile(
            id = "p3",
            name = "Bilal Ahmed",
            email = "bilal@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 1,
            position = PlayerPosition.GOALKEEPER,
            phoneNumber = "0300-1122334",
            age = 26,
            bio = "Reliable shot stopper with great distribution. Commands the defense.",
            fitnessStatus = FitnessStatus.FIT,
            matchesPlayed = 8,
            cleanSheets = 4,
            rating = 7.9f,
            profilePic = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200"
        )
        val p5 = UserProfile(
            id = "p4",
            name = "Usman Khalid",
            email = "usman@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 4,
            position = PlayerPosition.DEFENDER,
            phoneNumber = "0345-5556667",
            age = 25,
            bio = "Rock solid central defender. Dominant in the air.",
            fitnessStatus = FitnessStatus.FIT,
            matchesPlayed = 8,
            goalsScored = 1,
            yellowCards = 2,
            rating = 7.8f,
            profilePic = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200"
        )
        val p6 = UserProfile(
            id = "p5",
            name = "Adil Rashid",
            email = "adil@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 8,
            position = PlayerPosition.MIDFIELDER,
            phoneNumber = "0333-8889990",
            age = 21,
            bio = "Workhorse central midfielder with box-to-box engine.",
            fitnessStatus = FitnessStatus.FIT,
            matchesPlayed = 7,
            goalsScored = 2,
            assists = 3,
            rating = 7.6f,
            profilePic = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=200"
        )
        val p7 = UserProfile(
            id = "p6",
            name = "Faisal Shah",
            email = "faisal@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 17,
            position = PlayerPosition.DEFENDER,
            phoneNumber = "0311-4433221",
            age = 22,
            bio = "Attacking full-back with excellent crossing ability.",
            fitnessStatus = FitnessStatus.RECOVERING,
            matchesPlayed = 5,
            assists = 2,
            rating = 7.3f,
            profilePic = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=200"
        )
        val p8 = UserProfile(
            id = "p7",
            name = "Kamil Bukhari",
            email = "kamil@rawatfc.com",
            role = UserRole.PLAYER,
            jerseyNumber = 11,
            position = PlayerPosition.STRIKER,
            phoneNumber = "0300-9998887",
            age = 24,
            bio = "Dynamic winger. Pacy, agile, and loves 1-v-1 situations.",
            fitnessStatus = FitnessStatus.INJURED,
            matchesPlayed = 4,
            goalsScored = 1,
            rating = 7.0f,
            profilePic = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&q=80&w=200"
        )

        users.addAll(listOf(p1, p2, p3, p4, p5, p6, p7, p8))
        
        // Default login session is Hamza (Captain)
        currentUser.value = p2

        // 2. Populate Matches (Upcoming, Live, Finished)
        val m1 = MatchFixture(
            id = "m1",
            opponent = "Pindi Stars",
            opponentLogoUrl = "⭐",
            isHome = true,
            venueName = "Rawat FC Stadium, Islamabad",
            date = "2026-05-19",
            time = "17:30",
            status = MatchStatus.FINISHED,
            homeGoals = 3,
            awayGoals = 1,
            formation = "4-3-3",
            startingXIAIDs = listOf("p1", "p2", "p3", "p4", "p5", "p6"),
            goals = listOf(
                GoalTimeline(15, "Zain Malik", true),
                GoalTimeline(28, "Rawat FC", true), // free-kick by Hamza
                GoalTimeline(40, "Pindi Striker", false),
                GoalTimeline(72, "Zain Malik", true)
            ),
            cards = listOf(
                CardTimeline(25, "Usman Khalid", true),
                CardTimeline(60, "Pindi Midfielder", true)
            ),
            substitutions = listOf(
                SubTimeline(65, "Faisal Shah", "Adil Rashid")
            ),
            matchStats = MatchStats(58, 42, 14, 8, 8, 4, 6, 3, 10, 12),
            summary = "An outstanding display from Rawat FC. Zain Malik's double secure three crucial points in our title chase. Hamza Khan commanded the pitch with a stunning curling freekick in the 28th minute. Pindi Stars capitalised on a brief central defense error in the 40th, but Usman Khalid rallied the backline block. Rawat dominated the second half, solidifying possession and creating an elegant finishing routine for Zain Malik in the 72nd minute."
        )

        val m2 = MatchFixture(
            id = "m2",
            opponent = "Margalla FC",
            opponentLogoUrl = "🏔️",
            isHome = true,
            venueName = "Rawat FC Ground, Islamabad",
            date = "2026-05-26", // 3 days from simulated local date 2026-05-23
            time = "17:00",
            status = MatchStatus.UPCOMING,
            formation = "4-3-3",
            startingXIAIDs = listOf("p3", "p4", "p5", "p6", "p2", "p1")
        )

        val m3 = MatchFixture(
            id = "m3",
            opponent = "Islamabad Athletic",
            opponentLogoUrl = "⚡",
            isHome = false,
            venueName = "Jinnah Sports Stadium, Islamabad",
            date = "2026-06-02",
            time = "18:00",
            status = MatchStatus.UPCOMING,
            formation = "4-2-3-1"
        )

        matches.addAll(listOf(m1, m2, m3))

        // 3. Populate Training Sessions
        val t1 = TrainingSession(
            id = "t1",
            title = "Tactical Overloads & Pressing",
            description = "Focussed session on build-up patterns, switching play, and structured defensive recovery drills.",
            date = "2026-05-24", // Tomorrow
            time = "07:30",
            coachNotes = "Ensure high energy. We will practice the 4-3-3 high-press trigger. Hydrate heavily beforehand as weather will be warm.",
            drills = listOf(
                Drill("Warm-up & Agility Ladders", "Body preparation & footwork", 15),
                Drill("3-v-2 Tactical Overloads", "Quick passing & decision making", 30),
                Drill("Full Pitch High Press Rondo", "Positioning & transitions", 25),
                Drill("Stretching & Cool down", "Recovery", 10)
            ),
            attendance = mapOf(
                "p1" to AttendanceStatus.UNMARKED,
                "p2" to AttendanceStatus.UNMARKED,
                "p3" to AttendanceStatus.UNMARKED,
                "p4" to AttendanceStatus.UNMARKED,
                "p5" to AttendanceStatus.UNMARKED,
                "p6" to AttendanceStatus.UNMARKED,
                "p7" to AttendanceStatus.UNMARKED,
                "p8" to AttendanceStatus.UNMARKED
            )
        )

        val t2 = TrainingSession(
            id = "t2",
            title = "Set Piece Routines & Finishing",
            description = "Working on corner kicks, attacking free-kicks, and defensive block walls. Final finishing practice for attackers.",
            date = "2026-05-22", // Yesterday
            time = "08:00",
            coachNotes = "Great execution of corners today. Zain and Hamza looking exceptionally sharp in shooting practice.",
            drills = listOf(
                Drill("Possession Squares", "Warm-up in tight space", 15),
                Drill("Indirect Free Kicks", "Indirect tactics around box", 20),
                Drill("Corner Delivery Defending", "Aerial positioning & clearances", 25)
            ),
            attendance = mapOf(
                "p1" to AttendanceStatus.PRESENT,
                "p2" to AttendanceStatus.PRESENT,
                "p3" to AttendanceStatus.PRESENT,
                "p4" to AttendanceStatus.LATE,
                "p5" to AttendanceStatus.PRESENT,
                "p6" to AttendanceStatus.PRESENT,
                "p7" to AttendanceStatus.PRESENT,
                "p8" to AttendanceStatus.ABSENT // Injured
            )
        )

        trainings.addAll(listOf(t1, t2))

        // 4. Populate Group Chats
        val g1 = ChatGroup(
            id = "g_main",
            name = "Main Team Chat",
            description = "Official communications, player chit-chat, and announcements for all squad members.",
            type = GroupType.MAIN,
            lastMessageText = "See you all tomorrow morning at 7:30 AM sharp!",
            lastMessageTime = "09:30 PM",
            unreadCount = 2,
            isPinned = true,
            iconUrl = "📣",
            memberIds = users.map { it.id }
        )

        val g2 = ChatGroup(
            id = "g_tactics",
            name = "Match & Tactics",
            description = "Detailed match coordination, standard formations, opposition report and coaching strategy.",
            type = GroupType.MATCH_DAY,
            lastMessageText = "Check out the opposition stats from the last game.",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isPinned = false,
            iconUrl = "📋",
            memberIds = listOf("c1", "p1", "p2", "p4", "p5")
        )

        val g3 = ChatGroup(
            id = "g_injury",
            name = "Medical & Recovery",
            description = "Squad recovery reports, physio feedback, fitness tracking and training exemptions.",
            type = GroupType.INJURY,
            lastMessageText = "Physio checked my ankle, looking better for next Tuesday.",
            lastMessageTime = "2 Days Ago",
            isPinned = false,
            iconUrl = "🩹",
            memberIds = listOf("c1", "p1", "p7", "p8")
        )

        val g4 = ChatGroup(
            id = "g_admin",
            name = "Management Hub",
            description = "Admin room for Coach, Captain & operations management.",
            type = GroupType.MANAGEMENT,
            lastMessageText = "Bracket data for Twin Cities Super Cup updated.",
            lastMessageTime = "3 Days Ago",
            isPinned = false,
            iconUrl = "👑",
            memberIds = listOf("c1", "p1")
        )

        chatGroups.addAll(listOf(g1, g2, g3, g4))

        // 5. Populate Chat Messages
        val m_msg1 = ChatMessage(
            id = "m_1",
            groupId = "g_main",
            senderId = "c1",
            senderName = "Coach Rashid",
            senderRole = UserRole.COACH,
            text = "Outstanding win yesterday guys! Very proud of the character shown in the second half.",
            timestamp = "09:00 AM",
            isPinned = true
        )

        val m_msg2 = ChatMessage(
            id = "m_2",
            groupId = "g_main",
            senderId = "p2",
            senderName = "Zain Malik",
            senderRole = UserRole.PLAYER,
            text = "Thanks coach! Massive credit to Hamza for that world-class assist.",
            timestamp = "09:05 AM"
        )

        val m_msg3 = ChatMessage(
            id = "m_3",
            groupId = "g_main",
            senderId = "p1",
            senderName = "Hamza Khan",
            senderRole = UserRole.CAPTAIN,
            text = "Just put it in the right area mate, you did the hard part with that finish! Let's keep the focus high.",
            timestamp = "09:12 AM"
        )

        val m_msg4 = ChatMessage(
            id = "m_4",
            groupId = "g_main",
            senderId = "c1",
            senderName = "Coach Rashid",
            senderRole = UserRole.COACH,
            text = "Training scheduled for tomorrow morning. We will focus on tactical overload structures. See you all tomorrow morning at 7:30 AM sharp!",
            timestamp = "09:30 PM"
        )

        chatMessages.addAll(listOf(m_msg1, m_msg2, m_msg3, m_msg4))

        // Tactics msg preset
        chatMessages.add(ChatMessage(
            id = "m_tact_1",
            groupId = "g_tactics",
            senderId = "c1",
            senderName = "Coach Rashid",
            senderRole = UserRole.COACH,
            text = "Margalla FC plays with a very high defensive block. We need to exploit the space behind their full-backs with early balls.",
            timestamp = "04:00 PM"
        ))

        // Injury msg preset
        chatMessages.add(ChatMessage(
            id = "m_rec_1",
            groupId = "g_injury",
            senderId = "p8",
            senderName = "Kamil Bukhari",
            senderRole = UserRole.PLAYER,
            text = "Completed my light jog routine today. Ankle feels solid, zero pain. Ready to raise intensity.",
            timestamp = "Yesterday"
        ))

        // 6. Populate Announcements
        val a1 = Announcement(
            id = "a1",
            title = "Twin Cities Tournament Reschedule",
            content = "The final league matches format has been finalized. Rawat FC's match against Islamabad Athletic is officially scheduled for June 2nd, 6:00 PM at Jinnah Sports Stadium. This is our crucial title decider match, and a supporters bus is being organized for our local fanbase. Ensure family invite details are registered by this Saturday.",
            author = "Rashid Khan",
            authorRole = UserRole.COACH,
            date = "May 22, 2026",
            category = AnnouncementCategory.GENERAL,
            views = 12,
            isPinned = true
        )

        val a2 = Announcement(
            id = "a2",
            title = "STRICT: Mandatory Protective Gear Check",
            content = "Starting tomorrow's tactical training session, any player presenting without standard shin-guards and hydration water bottles will be barred from tactical contact drills. Player safety is our paramount concern as warm weather sets in.",
            author = "Rashid Khan",
            authorRole = UserRole.COACH,
            date = "May 23, 2026",
            category = AnnouncementCategory.URGENT,
            views = 8,
            isPinned = false
        )

        announcements.addAll(listOf(a1, a2))

        // 7. Populating League Table
        leagueStandings.addAll(listOf(
            TeamStanding(1, "Islamabad Athletic FC", 10, 8, 1, 1, 16, 25),
            TeamStanding(2, "Rawat FC", 10, 7, 2, 1, 12, 23),
            TeamStanding(3, "Rawat United", 10, 6, 2, 2, 8, 20),
            TeamStanding(4, "Margalla FC", 10, 5, 3, 2, 4, 18),
            TeamStanding(5, "Pindi Stars", 10, 4, 3, 3, 1, 15),
            TeamStanding(6, "Islamabad Greens", 10, 3, 1, 6, -5, 10),
            TeamStanding(7, "Capital FC", 10, 2, 1, 7, -12, 7),
            TeamStanding(8, "Khyber Tigers", 10, 1, 1, 8, -24, 4)
        ))

        // 8. Populating Tournament Bracket
        tournamentBracket.addAll(listOf(
            BracketMatch("Semifinal A", "Rawat FC", "Rawat United", 3, 2, "May 10, 2026", true),
            BracketMatch("Semifinal B", "Islamabad Athletic", "Margalla FC", 2, 1, "May 11, 2026", true),
            BracketMatch("Final Match", "Rawat FC", "Islamabad Athletic", null, null, "June 20, 2026", false)
        ))

        // 9. Populating Media Gallery
        mediaGallery.addAll(listOf(
            MediaItem("med1", "Team Huddle - Pindi Victory", "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&q=80&w=400", "MATCHES", false, "May 19, 2026", "Hamza Khan"),
            MediaItem("med2", "Zain Malik Strike Photo", "https://images.unsplash.com/photo-1517927033932-b3d18e61fb3a?auto=format&fit=crop&q=80&w=400", "MATCHES", false, "May 19, 2026", "Rashid Khan"),
            MediaItem("med3", "High Intensity Press Drills", "https://images.unsplash.com/photo-1543351611-58f69d7c1781?auto=format&fit=crop&q=80&w=400", "TRAINING", false, "May 15, 2026", "Rashid Khan")
        ))
    }

    // ----------------------------------------------------
    // ACTIONS & CONTROLS
    // ----------------------------------------------------

    fun loginSimulated(email: String, role: UserRole): Boolean {
        val existing = users.find { it.email.equals(email, ignoreCase = true) }
        return if (existing != null) {
            currentUser.value = existing
            true
        } else {
            // Create user dynamically
            val shortName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            val newUser = UserProfile(
                id = UUID.randomUUID().toString(),
                name = "$shortName Khan",
                email = email,
                role = role,
                jerseyNumber = (11..99).random(),
                position = PlayerPosition.values().random(),
                bio = "Newly onboarded Rawat FC squadron member."
            )
            users.add(newUser)
            currentUser.value = newUser
            saveAllData()
            true
        }
    }

    fun logout() {
        currentUser.value = null
    }

    fun broadcastSystemNotification(text: String) {
        currentNotification.value = text
        // Clear after 4 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentNotification.value == text) {
                currentNotification.value = null
            }
        }, 4000)
    }

    fun addAnnouncement(title: String, content: String, category: AnnouncementCategory) {
        val authorProfile = currentUser.value ?: return
        val newAnn = Announcement(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            author = authorProfile.name,
            authorRole = authorProfile.role,
            date = "May 23, 2026",
            category = category,
            views = 1,
            isPinned = false
        )
        announcements.add(0, newAnn)
        saveAllData()
        broadcastSystemNotification("📢 Announcement: $title")
    }

    fun addMatchFixture(opponent: String, isHome: Boolean, venue: String, date: String, time: String) {
        val newMatch = MatchFixture(
            id = UUID.randomUUID().toString(),
            opponent = opponent,
            opponentLogoUrl = "⚽",
            isHome = isHome,
            venueName = venue,
            date = date,
            time = time,
            status = MatchStatus.UPCOMING
        )
        matches.add(newMatch)
        saveAllData()
        broadcastSystemNotification("🏆 Match Added: vs $opponent on $date")
    }

    fun addTrainingSession(title: String, description: String, date: String, time: String, venue: String, notes: String, drills: List<Drill>) {
        val attendMap = users.filter { it.role == UserRole.PLAYER || it.role == UserRole.CAPTAIN }.associate { it.id to AttendanceStatus.UNMARKED }
        val newSession = TrainingSession(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            date = date,
            time = time,
            venue = venue,
            coachNotes = notes,
            drills = drills,
            attendance = attendMap
        )
        trainings.add(0, newSession)
        saveAllData()
        broadcastSystemNotification("🏃 Training Scheduled: $title ($date)")
    }

    fun markAttendance(trainingId: String, userId: String, status: AttendanceStatus) {
        val index = trainings.indexOfFirst { it.id == trainingId }
        if (index != -1) {
            val s = trainings[index]
            val newAttend = s.attendance.toMutableMap()
            newAttend[userId] = status
            trainings[index] = s.copy(attendance = newAttend)
            saveAllData()
        }
    }

    fun addMediaItem(title: String, url: String, category: String) {
        val author = currentUser.value?.name ?: "Admin"
        val newItem = MediaItem(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url,
            category = category,
            uploader = author,
            date = "May 23, 2026"
        )
        mediaGallery.add(0, newItem)
        saveAllData()
        broadcastSystemNotification("🖼️ Media Uploaded: $title")
    }

    fun sendMessage(groupId: String, text: String, imagePreset: String? = null) {
        val user = currentUser.value ?: return
        val msgId = UUID.randomUUID().toString()
        val newMessage = ChatMessage(
            id = msgId,
            groupId = groupId,
            senderId = user.id,
            senderName = user.name,
            senderRole = user.role,
            text = text,
            timestamp = "08:19 AM", // Match simulated time
            mediaUrl = imagePreset,
            isImage = imagePreset != null
        )
        chatMessages.add(newMessage)
        saveAllData()

        // Update group last message summary
        val gIndex = chatGroups.indexOfFirst { it.id == groupId }
        if (gIndex != -1) {
            val g = chatGroups[gIndex]
            chatGroups[gIndex] = g.copy(
                lastMessageText = if (imagePreset != null) "[Image] $text" else text,
                lastMessageTime = "08:19 AM"
            )
        }

        // Trigger Automated Dynamic Reply after short latency to simulate professional real-time feel
        simulateAutoResponse(groupId, text)
    }

    private fun simulateAutoResponse(groupId: String, text: String) {
        val currentContextUser = currentUser.value ?: return
        
        // Select an automated respondent
        val responders = users.filter { it.id != currentContextUser.id }
        if (responders.isEmpty()) return
        val responder = responders.random()

        CoroutineScope(Dispatchers.Main).launch {
            // Show typing indicator
            typingIndicators[groupId] = "${responder.name} is typing..."
            delay(1805)
            typingIndicators.remove(groupId)

            val autoText = generateSimulatedReply(responder, text)
            val autoMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                senderId = responder.id,
                senderName = responder.name,
                senderRole = responder.role,
                text = autoText,
                timestamp = "08:21 AM"
            )
            chatMessages.add(autoMsg)

            val gIndex = chatGroups.indexOfFirst { it.id == groupId }
            if (gIndex != -1) {
                val g = chatGroups[gIndex]
                chatGroups[gIndex] = g.copy(
                    lastMessageText = autoText,
                    lastMessageTime = "08:21 AM",
                    unreadCount = g.unreadCount + 1
                )
            }
            broadcastSystemNotification("💬 New Message from ${responder.name}")
            saveAllData()
        }
    }

    private fun generateSimulatedReply(user: UserProfile, message: String): String {
        return when (user.role) {
            UserRole.COACH -> {
                if (message.contains("training", true) || message.contains("practice", true)) {
                    "Agreed. Ensure you warm-up properly beforehand. Strict timing discipline is expected."
                } else if (message.contains("win", true) || message.contains("match", true) || message.contains("goal", true)) {
                    "That is the tactical focus we worked on. We must review video tapes and maintain squad shape."
                } else {
                    "Understood. Let's direct our concentration to the next fixture vs Margalla FC. Stay fit."
                }
            }
            UserRole.CAPTAIN -> {
                if (message.contains("tactics", true) || message.contains("play", true)) {
                    "Yes guys, we need to close down channels and support the double pivot. Big match ahead!"
                } else {
                    "Brilliant! Let's get three points, Rawat FC to the top! 💯💪"
                }
            }
            else -> {
                if (message.contains("injury", true) || message.contains("pain", true)) {
                    "Get well soon bro! The squad needs you at 100%."
                } else if (message.contains("ready", true)) {
                    "Count me in. Fully fit and excited for matchday!"
                } else {
                    "Got it, looking forward to the next run out. Rawat FC all the way!"
                }
            }
        }
    }

    fun pinChatMessage(messageId: String, isPin: Boolean) {
        val index = chatMessages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val msg = chatMessages[index]
            chatMessages[index] = msg.copy(isPinned = isPin)
            saveAllData()
            broadcastSystemNotification(if (isPin) "📌 Message Pinned" else "📌 Message Unpinned")
        }
    }

    fun addEmojiReaction(messageId: String, userId: String, emoji: String) {
        val index = chatMessages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val msg = chatMessages[index]
            val reactions = msg.reactions.toMutableList()
            val existing = reactions.find { it.emoji == emoji }
            if (existing != null) {
                if (!existing.userIds.contains(userId)) {
                    val updatedUsers = existing.userIds + userId
                    reactions.remove(existing)
                    reactions.add(MessageReaction(emoji, updatedUsers.size, updatedUsers))
                }
            } else {
                reactions.add(MessageReaction(emoji, 1, listOf(userId)))
            }
            chatMessages[index] = msg.copy(reactions = reactions)
            saveAllData()
        }
    }

    // ----------------------------------------------------
    // LIVE AI GEMINI PORT - BACKEND MATCH & INSIGHTS ASSIST
    // ----------------------------------------------------

    suspend fun getGeminiAIResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val key = try {
            val candidate = BuildConfig.GEMINI_API_KEY
            if (candidate.isEmpty() || candidate.contains("MY_GEMINI_API_KEY")) "" else candidate
        } catch (e: Exception) {
            ""
        }

        if (key.isEmpty()) {
            return@withContext "🤖 **Offline AI Assistant Fallback**:\nNo live API secret key configured. Presenting expert standard assessment:\n\n*Rawat FC has demonstrated exceptional defensive compactness this season. Relying heavily on tactical transition triggers through central playmakers like Hamza Khan ($10), the team's counter-attacking speed remains lethal. Overworked hamstring statistics suggests high-press drills should decrease by 15% to mitigate mid-season muscle fatigues.*"
        }

        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$key"
            
            val jsonBody = JSONObject().apply {
                val contents = JSONObject().apply {
                    val parts = JSONObject().apply {
                        put("text", "$prompt (Please provide formatting in clear, bold, high-quality bulletin structures fitting an premium football club staff dossier. Keep it concise, under 200 words.)")
                    }
                    put("parts", org.json.JSONArray().put(parts))
                }
                put("contents", org.json.JSONArray().put(contents))
            }

            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            } else {
                "⚠️ Network failed: Response code ${response.code}. Providing localized tactical feedback instead."
            }
        } catch (e: Exception) {
            "⚠️ External network exception: ${e.localizedMessage}. fallback to offline team review."
        }
    }
}
