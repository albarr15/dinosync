
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
/*
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeAchievements
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeDailyStudyHistory
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeFeelingEntry
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeGroupMembers
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeMoods
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeStudyGroups
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
*/

import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import java.util.Calendar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.UserViewModel


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId") ?: ""
        setContent {
            DinoSyncTheme {
                ProfileActivityScreen(userId = userId)
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("ProfileActivity onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("ProfileActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("ProfileActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("ProfileActivity onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("ProfileActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("ProfileActivity onDestroy()")
    }
}

@Composable
fun ProfileActivityScreen(userId: String) {
    val context = LocalContext.current
    val userVM: UserViewModel = viewModel()

    // Observe state from ViewModel
    val user by userVM.user.collectAsState()
    // TODO: Add achievements, groups, moodHistory state when implemented in ViewModel

    // Load data on first composition
    LaunchedEffect(userId) {
        userVM.loadUser(userId)
    }

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
                        // TODO: change to user profile
                        painter = painterResource(R.drawable.althea),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    )
                    Column {
                        Text(
                            user?.userName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = fontFamily
                        )
                        Text(
                            user?.userBio ?: "user bio user bio",
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
            // TODO: Achievements Section (use real data from ViewModel when available)
            Text("Collection", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // TODO: Replace with real achievements from ViewModel
            }
            Spacer(modifier = Modifier.height(30.dp))
            // TODO: Groups Section (use real data from ViewModel when available)
            Text("Groups", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // TODO: Replace with real groups from ViewModel
            }
            Spacer(modifier = Modifier.height(30.dp))
            // Mood Log Section (placeholder, replace with real data when available)
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

