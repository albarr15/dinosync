
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
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

import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import java.util.Calendar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobdeve.s18.group9.dinosync.model.Mood
import com.mobdeve.s18.group9.dinosync.viewmodel.ProfileViewModel


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
    val profileVM: ProfileViewModel = viewModel()

    // Observe state from ViewModel
    val user by profileVM.user.collectAsState()

    val companions by profileVM.companions.collectAsState()
    val userGroups by profileVM.groups.collectAsState()
    val moodHistory by profileVM.moodHistory.collectAsState()

    // Load data on first composition
    LaunchedEffect(userId) {
        profileVM.loadUserProfile(userId)
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
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("userId", userId)
                    }
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
                        painter = painterResource(R.drawable.althea),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    )
                    Column {
                        Text(
                            user?.userName ?: "Username",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = fontFamily
                        )
                        Text(
                            user?.userBio ?: "User Bio",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = fontFamily,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(30.dp))

            // COLLECTION SECTION
            Text("Collection", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))

            val latestCompanions = companions
                .sortedByDescending { it.dateAwarded }
                .take(3)


            if (latestCompanions.isEmpty()) {
                Text("No companions yet.", color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(latestCompanions.size) { index ->
                        val companion = latestCompanions[index]
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(companion.getBGColor(), RoundedCornerShape(10.dp))
                                .clickable {
                                    // Navigate to Companion screen with user ID
                                    val intent = Intent(context, CompanionActivity::class.java)
                                    intent.putExtra("userId", userId)
                                    context.startActivity(intent)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(companion.getDrawableRes()),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    // Add 'View All' box as the last item
                    item {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                .clickable {
                                    val intent = Intent(context, CompanionActivity::class.java)
                                    intent.putExtra("userId", userId)
                                    context.startActivity(intent)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "View All",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(30.dp))
            // GROUPS SECTION
            Text("Groups", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(5.dp))

            val topGroups = userGroups
                .sortedBy { it.name }
                .take(3)

            if (topGroups.isEmpty()) {
                Text("No groups yet.", color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(topGroups.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.LightGray, RoundedCornerShape(10.dp))
                                .clickable {
                                    Log.d("ProfileActivity", "Navigating to GroupActivity with groupId: " +
                                            "${topGroups[index].groupId}, userId: $userId")
                                    val intent = Intent(context, GroupActivity::class.java)
                                    intent.putExtra("groupId", topGroups[index].groupId)
                                    intent.putExtra("userId", userId)
                                    context.startActivity(intent)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.group_default),
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
                    // Add 'View All' box as the last item
                    item {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                .clickable {
                                    val intent = Intent(context, DiscoverGroupsActivity::class.java)
                                    intent.putExtra("userId", userId)
                                    context.startActivity(intent)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "View All",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            // MOOD LOG SECTION
            Row {
                Text("Mood Log", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(5.dp))
            MoodTrackerGrid(moods = moodHistory)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

fun getMoodColor(moodLevel: Int): Color {
    return when (moodLevel) {
        1 -> Color(0xFFD32F2F) // Terrible - Red
        2 -> Color(0xFFF57C00) // Bad - Orange
        3 -> Color(0xFFFBC02D) // Meh - Yellow
        4 -> Color(0xFF8BC34A) // Good - Light Green
        5 -> Color(0xFF388E3C) // Great - Green
        else -> Color(0x3B6E6E6E)
    }
}


@Composable
fun MoodTrackerGrid(
    modifier: Modifier = Modifier,
    moods: List<Mood>
) {
    val daysToShow = 60
    val columns = 15
    val rows = 4

    // Placeholder mood with empty name for padding
    val placeholderMood = Mood(name = "", imageKey = "")

    // Pad moods with placeholders to make up 60 items
    val recentMoods = moods.takeLast(daysToShow).let {
        if (it.size < daysToShow)
            List(daysToShow - it.size) { placeholderMood } + it
        else it
    }

    // Fill grid top-down, right-to-left row-major order
    val moodGrid = Array(rows) { Array(columns) { placeholderMood } }

    for (i in recentMoods.indices) {
        val row = i / columns
        val col = i % columns
        if (row < rows) {
            moodGrid[row][col] = recentMoods[recentMoods.size - 1 - i]
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columns) {
                    val mood = moodGrid[row][col]
                    val moodLevel = mood.level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = getMoodColor(moodLevel),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}
