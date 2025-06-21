package com.mobdeve.s18.group9.dinosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.googlefonts.Font
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeGroupMembers
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.Achievement
import com.mobdeve.s18.group9.dinosync.model.DailyStudyHistory
import com.mobdeve.s18.group9.dinosync.model.FeelingEntry
import com.mobdeve.s18.group9.dinosync.model.Mood
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.model.User
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import java.util.Calendar
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeFeelingEntry
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeAchievements
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeDailyStudyHistory
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeMoods
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeStudyGroups
import kotlin.math.ceil


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)
        setContent {
            DinoSyncTheme {
                ProfileActivityScreen(userId = userId)
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy()")
    }
}

@Composable
fun ProfileActivityScreen(userId : Int) {
    val userId = userId
    val feelingEntries = initializeFeelingEntry()
    val dailyStudyHistory = initializeDailyStudyHistory()
    val studyGroups = initializeStudyGroups()
    val achievements = initializeAchievements()
    val users = initializeUsers()
    val moods = initializeMoods()

    val context = LocalContext.current
    val groupMembers = initializeGroupMembers()

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val interFontName = GoogleFont("Inter")
    val fontFamily = FontFamily(
        Font(
            googleFont = interFontName,
            fontProvider = provider,
            weight = FontWeight.Medium
        )
    )

    val selectedUser = users.find { it.userId == userId }

    // Prepare data for mood log (only for current month)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val userFeelingsThisMonth = feelingEntries.filter { it.userId == userId &&
            it.entryDate.month == currentMonth && it.entryDate.year + 1900 == currentYear
    }

    val moodMap = moods.associateBy { it.moodId }
    val totalDaysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
    val moodEntries = MutableList(totalDaysInMonth) { Color.LightGray }

    userFeelingsThisMonth.forEach { feelingEntry ->
        val day = feelingEntry.entryDate.date - 1 // day of month is 1-based
        val mood = moodMap[feelingEntry.moodId]
        moodEntries[day] = when (mood?.name) {
            "Happy" -> Color.Yellow
            "Sad" -> Color.Blue
            "Angry" -> Color.Red
            "Neutral" -> Color.Gray
            "Productive" -> Color.Green
            else -> Color.LightGray
        }
    }

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Profile",
                onGroupsClick = {
                    val intent = Intent(context, DiscoverGroupsActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                },
                onHomeClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                },
                onStatsClick = {
                    val intent = Intent(context, StatisticsActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {

            TopActionBar(
                onProfileClick = {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                },
                onSettingsClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Info Section
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Transparent, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = selectedUser?.userProfileImage ?: R.drawable.althea),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    )
                    Column {
                        Text(
                            selectedUser?.userName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = fontFamily
                        )
                        Text(
                            selectedUser?.userBio ?: "user bio user bio",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(Color.LightGray, shape = CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(30.dp))

            // Achievements Section (Filtered by userId)
            Text("Collection", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val userAchievements = achievements.filter { it.userId == userId }
                items(userAchievements.size) { index ->
                    val achievement = userAchievements[index]
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, RoundedCornerShape(10.dp))
                            .clickable {
                                // Navigate to AchievementDetailActivity with achievement ID
                                val intent = Intent(context, CompanionActivity::class.java)
                                intent.putExtra("userId", userId)
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = achievement.image),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Groups Section (Filtered)
            Text("Groups", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val userGroups = groupMembers
                    .filter { it.userId == userId }
                    .mapNotNull { member -> studyGroups.find { it.groupId == member.groupId } }

                items(userGroups.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = userGroups[index].image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userGroups[index].name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Mood Log Section
            Row {
                Text("Mood Log", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Text(
                    text = "Latest Mood",
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            MoodTrackerGrid()

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

fun getMoodColor(moodLevel: Int): Color {
    return when (moodLevel) {
        0 -> Color.Gray
        1 -> Color(0xFFD32F2F) // Terrible - Red
        2 -> Color(0xFFF57C00) // Bad - Orange
        3 -> Color(0xFFFBC02D) // Meh - Yellow
        4 -> Color(0xFF8BC34A) // Good - Light Green
        5 -> Color(0xFF388E3C) // Great - Green
        else -> Color.Gray
    }
}

fun generateMoodSampleData(): List<Int> {
    // Random mood levels from 0 to 5
    return List(365) { (0..5).random() }
}

@Composable
fun MoodTrackerGrid(
    modifier: Modifier = Modifier
) {
    // val moods = listOf(0, 1, 0, 2, 2, 0, 0), latest mood at end of list
    val moods = generateMoodSampleData()
    val daysToShow = 60
    val columns = 15
    val rows = 4

    // Pad moods to always be full grid
    val recentMoods = moods.takeLast(daysToShow)
        .let {
            if (it.size < daysToShow) List(daysToShow - it.size) { 0 } + it else it
        }

    // Fill grid in top-down, right-to-left row-major order
    val moodGrid = Array(rows) { Array(columns) { 0 } }

    for (i in recentMoods.indices) {
        val row = i / columns
        val col = columns - 1 - (i % columns) // reverse column order
        if (row < rows) {
            moodGrid[row][col] = recentMoods[recentMoods.size - 1 - i]
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columns) {
                    val mood = moodGrid[row][col]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = getMoodColor(mood),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}
