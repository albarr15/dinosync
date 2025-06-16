package com.mobdeve.s18.group9.dinosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeMusic
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCard
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import kotlinx.coroutines.delay
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.Orange
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen



class FocusStudyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the data passed via Intent
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val selectedSubject = intent.getStringExtra("selected_subject") ?: ""

        setContent {
            DinoSyncTheme {
                FocusStudyScreen(hours = hours, minutes = minutes, subject = selectedSubject)
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
fun FocusStudyScreen(hours: Int, minutes: Int, subject: String) {

    val context = LocalContext.current

    val totalTime = (hours * 3600) + (minutes * 60)
    var timeLeft by remember { mutableIntStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(true) }
    var isStopped by remember { mutableStateOf(false) }
    val progress = if (totalTime > 0) timeLeft / totalTime.toFloat() else 0f
    val hours = timeLeft / 3600
    val minutes = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60

    val formattedTime = "%02d:%02d:%02d".format(hours, minutes, seconds)
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

    /*** Countdown Timer ***/
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isRunning = false
        }
    }

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = { BottomNavigationBar(
            selectedItem    = null,
            onGroupsClick   = {
                val intent = Intent(context, DiscoverGroupsActivity::class.java)
                context.startActivity(intent)
            },
            onProfileClick  = {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            },
            onStatsClick    = {
                val intent = Intent(context, StatisticsActivity::class.java)
                context.startActivity(intent)
            }
        ) }
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
                onProfileClick = { },
                onNotificationsClick = {  },
                onSettingsClick = { }
            )

            Spacer(modifier = Modifier.height(12.dp))

            /******** Course Box *********/
            Box(
                modifier = Modifier
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 90.dp, vertical = 15.dp)
            ){
                Text(subject, fontWeight = FontWeight.ExtraBold, fontFamily = fontFamily)
            }
            Spacer(modifier = Modifier.height(30.dp))

            /*** Circular progress and timer ***/
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 35f

                    drawArc(
                        color = Color.LightGray,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = timerColor,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
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
                    .offset(y = (-100).dp)
            ) {
                OutlinedButton(
                    onClick = {
                        timeLeft = totalTime     // Reset time
                        isRunning = false        // Stop countdown
                        isStopped = true

                    },
                    colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
                    modifier = Modifier
                        .width(130.dp)
                        .height(35.dp)
                ) {
                    Text("Stop", color = Color.White, fontFamily = fontFamily)
                }

                Spacer(modifier = Modifier.width(20.dp))
                /*** Buttons ***/
                Button(
                    onClick = {

                        if (timeLeft == 0) {
                            timeLeft = totalTime // Restart the timer
                            isRunning = true
                            isStopped = false
                        } else {
                            isRunning = !isRunning // Toggle pause/resume
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.DarkGray
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(35.dp)
                ) {
                    Text(text = when {
                        isStopped || timeLeft == 0 -> "Repeat"
                        isRunning -> "Pause"
                        else -> "Resume"
                    },
                        fontFamily = fontFamily)
                }
            }

            HorizontalDivider(modifier = Modifier.offset(y = (-50).dp), thickness = 1.dp, color = Color.White)

            Box(
                modifier = Modifier
                    .fillMaxSize().offset(y = (-40).dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    val currentMusic = initializeMusic()[1]
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
}




