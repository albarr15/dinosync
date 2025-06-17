package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeCourses
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.PieStats
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyWhite
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.floor


class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)

        setContent {
            DinoSyncTheme {
                StatsActivityScreen(userId = userId)
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
fun StatsActivityScreen(userId : Int){
    val context = LocalContext.current
    val userList = initializeUsers()
    val selectedUser = userList.random()

    val courseList = initializeCourses()
    val subjects = courseList.map { it.name }

    val totalTime = 2 * 60 * 60; // dummy data, represents total study time across subjects in seconds

    var stats_data by remember { mutableStateOf(HashMap<String, Int>()) }
    // format: course, time_spent_focusing

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
                selectedItem = null,
                onGroupsClick = {
                    val intent = Intent(context, DiscoverGroupsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                },
                onHomeClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                },
                onStatsClick = {
                    val intent = Intent(context, StatisticsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
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
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                },
                onSettingsClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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
        }
    }
}
