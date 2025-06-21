package com.mobdeve.s18.group9.dinosync

import HatchCard
import com.mobdeve.s18.group9.dinosync.components.NewEggCard
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeMusic
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCard
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.Orange
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import kotlinx.coroutines.delay


class FocusStudyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val selectedSubject = intent.getStringExtra("selected_subject") ?: ""

        setContent {
            DinoSyncTheme {
                FocusStudyScreen(userId = userId, hours = hours, minutes = minutes, subject = selectedSubject)
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
fun FocusStudyScreen(userId: Int, hours: Int, minutes: Int, subject: String) {
    val context = LocalContext.current
    val totalTime = (hours * 3600) + (minutes * 60)
    var timeLeft by remember { mutableIntStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(true) }
    var isStopped by remember { mutableStateOf(false) }
    val musicList = remember { initializeMusic() }
    val progress = if (totalTime > 0) timeLeft / totalTime.toFloat() else 0f
    val hoursLeft = timeLeft / 3600
    val minutesLeft = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60
    val formattedTime = "%02d:%02d:%02d".format(hoursLeft, minutesLeft, seconds)

    val isLowTime = timeLeft <= totalTime * 0.2
    val isMidTime = timeLeft <= totalTime * 0.5
    val timerColor = when {
        isLowTime -> Color.Red
        isMidTime -> Orange
        else -> YellowGreen
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
            weight = FontWeight.Normal
        )
    )

    val currentMusic = musicList[1]
    var showHatchCard by remember { mutableStateOf(false) }
    var showNewEggCard by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L) // 10 secs
                timeLeft--
            }
            isRunning = false
        }
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft == 0) {
            showHatchCard = true
            delay(10000L)  // 3 secs
            showNewEggCard = true
        }
    }

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = null,
                onGroupsClick = {
                    context.startActivity(Intent(context, DiscoverGroupsActivity::class.java))
                },
                onHomeClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                onStatsClick = {
                    context.startActivity(Intent(context, StatisticsActivity::class.java))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
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

            Box(
                modifier = Modifier
                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 50.dp, vertical = 10.dp)
            ) {
                Text(subject, fontWeight = FontWeight.ExtraBold, fontFamily = fontFamily, color = Color.White)
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(360.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 50f
                    drawArc(
                        color = Color.LightGray,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = timerColor,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = formattedTime,
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 90.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .offset(y = (-190).dp)
            ) {
                OutlinedButton(
                    onClick = {
                        timeLeft = totalTime
                        isRunning = false
                        isStopped = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
                    modifier = Modifier.width(100.dp).height(35.dp)
                ) {
                    Text("Stop", color = Color.White, fontFamily = fontFamily)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = {
                        if (timeLeft == 0) {
                            timeLeft = totalTime
                            isRunning = true
                            isStopped = false
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.DarkGray
                    ),
                    modifier = Modifier.width(100.dp).height(35.dp)
                ) {
                    Text(
                        text = when {
                            isStopped || timeLeft == 0 -> "Repeat"
                            isRunning -> "Pause"
                            else -> "Resume"
                        },
                        fontFamily = fontFamily
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().offset(y = (-120).dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
                ) {
                    AudioPlayerCard(
                        currentMusic = currentMusic,
                        progress = 0.5f,
                        onShuffle = { },
                        onPrevious = { },
                        onPlayPause = { },
                        onNext = { },
                        onRepeat = { }
                    )
                }
            }
        }
    }
    if (showHatchCard) {
        Box(
            modifier = Modifier.fillMaxSize().size(500.dp),
            contentAlignment = Alignment.Center
        ) {
            HatchCard()
        }
    }
    if (showNewEggCard) {
        Box(
            modifier = Modifier.fillMaxSize().size(500.dp),
            contentAlignment = Alignment.Center
        ) {
            NewEggCard(
                onContinueClick = {
                    showHatchCard = false
                    showNewEggCard = false
                }
            )
        }
    }
}



