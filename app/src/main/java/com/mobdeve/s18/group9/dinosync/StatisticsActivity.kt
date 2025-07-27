
package com.mobdeve.s18.group9.dinosync

import StreakGrid
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.PieStats
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.components.UserSessionsLineChart
// import com.mobdeve.s18.group9.dinosync.components.SessionsLineChart
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.viewmodel.StatsViewModel


class StatisticsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user!")

        setContent {
            DinoSyncTheme {
                DinoSyncTheme {
                    StatsActivityScreen(userId = userId)
                }
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("StatisticsActivity onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("StatisticsActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("StatisticsActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("StatisticsActivity onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("StatisticsActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("StatisticsActivity onDestroy()")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsActivityScreen(userId : String){
    val context = LocalContext.current

    val statsVM: StatsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

//    LaunchedEffect(userId) {
//        studySessionVM.loadStudySessions(userId)
//        dailyStudyHistoryVM.loadDailyHistory(userId)
//        courseVM.loadCourses()
//        statsVM.loadPieData(userId)
//    }
    LaunchedEffect(userId) {
        statsVM.loadUserStats(userId)
        statsVM.loadPieData(userId)
    }

    val streakData by statsVM.streakData.collectAsState()
    val pieData by statsVM.pieData.collectAsState()
    val studySessions by statsVM.studySessions.collectAsState()
    val courseList by statsVM.courses.collectAsState()
    val dailyStudyHistory by statsVM.dailyStudyHistory.collectAsState()

    val subjects = courseList.map { it.name }

    var totalTime = 0 // represents total study time across subjects in seconds

    studySessions.forEach() {
        studySession ->
        if (studySession.endedAt != null) {
            totalTime += (studySession.minuteSet * 60) + (studySession.hourSet * 60 * 60)
        }
    }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Stats",
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
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                PieStats(pieData.sortedByDescending { it.data }, totalTime)
            }

            // Streak Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }


            Spacer(modifier = Modifier.height(5.dp))
            StreakGrid(Modifier, streakData)

            // Sessions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            UserSessionsLineChart(userId, dailyStudyHistory, studySessions, emptyList())
        }
    }
}


