package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.viewmodel.CompanionActViewModel
import com.google.firebase.auth.FirebaseAuth

class CompanionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user!")
        setContent {
            DinoSyncTheme {
                CompanionActivityScreen(userId = userId)
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() { super.onStart(); println("CompanionActivity onStart()") }
    override fun onResume() { super.onResume(); println("CompanionActivity onResume()") }
    override fun onPause() { super.onPause(); println("CompanionActivity onPause()") }
    override fun onStop() { super.onStop(); println(" CompanionActivity onStop()") }
    override fun onRestart() { super.onRestart(); println("CompanionActivity  onRestart()") }
    override fun onDestroy() { super.onDestroy(); println("CompanionActivity onDestroy()") }
}

@Composable
fun CompanionActivityScreen(userId: String) {
    val context = LocalContext.current
    val companionVM: CompanionActViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val companions by companionVM.companions.collectAsState()

    // Load companions on first composition
    androidx.compose.runtime.LaunchedEffect(userId) {
        companionVM.loadCompanions(userId)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Home",
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your Companions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 20.dp)
            )
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.eggcompanion),
                    contentDescription = "Egg Image",
                    modifier = Modifier.size(320.dp)
                )
            }
            // Show the most recently created unhatched egg first; if none, show most recently hatched
            val mostRecentUnhatched = companions.filter { !it.isHatched() }.lastOrNull()
            val mostRecentHatched = companions.filter { it.isHatched() }
                .maxByOrNull { it.dateAwarded?.seconds ?: 0L }
            val displayCompanion = mostRecentUnhatched ?: mostRecentHatched
            if (displayCompanion != null) {
                val hatchText = if (displayCompanion.isHatched()) {
                    "Hatched!"
                } else {
                    val timeLeft = displayCompanion.remainingHatchTime
                    val formattedTime = when {
                        timeLeft >= 3600 -> {
                            val hours = timeLeft / 3600
                            val minutes = (timeLeft % 3600) / 60
                            val seconds = timeLeft % 60

                            if (hours == 1) {
                                "${hours} hr, ${minutes} mins, ${seconds} secs"

                            } else {
                                "${hours} hrs, ${minutes} mins, ${seconds} secs"
                            }
                        }
                        timeLeft >= 60 -> {
                            val minutes = timeLeft / 60
                            val seconds = timeLeft % 60
                            "${minutes} mins, ${seconds} secs"
                        }
                        else -> "${timeLeft} secs"
                    }
                    "Time left to hatch: $formattedTime"
                }
                Text(
                    text = hatchText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Start focusing",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Collection",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(5.dp))
            val userCompanions = companions
                .filter { it.userId == userId && it.isHatched() }
                .sortedByDescending { it.dateAwarded?.seconds ?: 0L }
            if (userCompanions.isEmpty()) {
                Text("No companions yet.", color = Color.Gray)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(userCompanions) { companion ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(companion.getBGColor()),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = companion.getDrawableRes()),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}