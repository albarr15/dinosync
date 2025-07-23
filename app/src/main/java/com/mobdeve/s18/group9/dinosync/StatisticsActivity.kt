
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
// import com.mobdeve.s18.group9.dinosync.components.SessionsLineChart
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.CourseViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.DailyStudyHistoryViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import ir.ehsannarmani.compose_charts.models.Pie


class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

@Composable
fun StatsActivityScreen(userId : String){
    val context = LocalContext.current

    val studySessionVM: StudySessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val studySessions by studySessionVM.studySessions.collectAsState()

    val dailyStudyHistoryVM: DailyStudyHistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val dailyStudyHistory by dailyStudyHistoryVM.dailyHistory.collectAsState()

    val courseVM: CourseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val courseList by courseVM.courses.collectAsState()

    val subjects = courseList.map { it.name }

    val totalTime = 2 * 60 * 60; // dummy data, represents total study time across subjects in seconds

    var dummy_data by remember {
        mutableStateOf(
            listOf(
                Pie(label = "MOBICOM", data = 63.0, color = DirtyGreen),
                Pie(label = "STCLOUD", data = 22.0, color = Color(0xFFA7C957)),
                Pie(label = "CSOPESY", data = 10.0, color = Color.LightGray)

            )
        )
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

                PieStats(dummy_data, totalTime)
            }

            // Streak Section
            Row {
                Text(
                    text = "Streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Latest Study History",
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            StreakGrid()

            // Sessions Section
            Row {
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Latest Session",
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            // SessionsLineChart(userId, dailyStudyHistory, studySessions)
        }
    }
}

fun getStudyActColor(studyLevel: Int): Color {
    return when (studyLevel) {
        0 -> Color.Gray
        in 1..2 -> Color(0xFFD32F2F) // Terrible - Red
        in 3..6 -> Color(0xFFF57C00) // Bad - Orange
        in 7..9 -> Color(0xFFFBC02D) // Meh - Yellow
        in 10..12 -> Color(0xFF8BC34A) // Good - Light Green
        in 12..24 -> Color(0xFF388E3C) // Great - Green
        else -> Color.Gray
    }
}

fun generateTimeSampleData(): List<Int> {
    // Random time levels from 0 to 5
    return List(365) { (0..24).random() }
}

@Composable
fun StreakGrid(
    modifier: Modifier = Modifier
) {
    // val daily_time = listOf(0, 1, 0, 2, 2, 0, 0), latest time at end of list, in hrs
    val study_act = generateTimeSampleData()
    val daysToShow = 60
    val columns = 15
    val rows = 4

    // Pad moods to always be full grid
    val recentStudyAct = study_act.takeLast(daysToShow)
        .let {
            if (it.size < daysToShow) List(daysToShow - it.size) { 0 } + it else it
        }

    // Fill grid in top-down, right-to-left row-major order
    val studyActGrid = Array(rows) { Array(columns) { 0 } }

    for (i in recentStudyAct.indices) {
        val row = i / columns
        val col = columns - 1 - (i % columns) // reverse column order
        if (row < rows) {
            studyActGrid[row][col] = recentStudyAct[recentStudyAct.size - 1 - i]
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
                    val x = studyActGrid[row][col]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = getStudyActColor(x),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}