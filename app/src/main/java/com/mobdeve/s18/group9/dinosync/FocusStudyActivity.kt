
package com.mobdeve.s18.group9.dinosync

import HatchCard
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCard
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.NewEggCard
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.Music
import com.mobdeve.s18.group9.dinosync.model.MusicSession
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.mobdeve.s18.group9.dinosync.ui.theme.*
import com.mobdeve.s18.group9.dinosync.viewmodel.MusicSessionViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.MusicViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.StrokeCap

class FocusStudyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId") ?: ""
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val selectedSubject = intent.getStringExtra("selected_subject") ?: ""
        val studySessionId = intent.getStringExtra("study_session_id") ?: ""
        val moodId = intent.getStringExtra("moodId") ?: ""

        setContent {
            DinoSyncTheme {
                FocusStudyScreen(
                    userId = userId,
                    hours = hours,
                    minutes = minutes,
                    subject = selectedSubject,
                    studySessionId = studySessionId,
                    moodId = moodId
                )
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("FocusStudyActivity onStart()")

    }

    override fun onResume() {
        super.onResume()
        println("FocusStudyActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("FocusStudyActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("FocusStudyActivity onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("FocusStudyActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("FocusStudyActivity onDestroy()")
    }
}
@Composable
fun FocusStudyScreen(
    userId: String,
    hours: Int,
    minutes: Int,
    subject: String,
    studySessionId: String,
    moodId: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val musicVM: MusicViewModel = viewModel()
    val musicList by musicVM.musicList.collectAsState()
    var currentMusic by remember { mutableStateOf<Music?>(null) }

    val totalTime = (hours * 3600) + (minutes * 60)
    var timeLeft by remember { mutableIntStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(true) }
    var isStopped by remember { mutableStateOf(false) }
    val pauseTimestamps = remember { mutableStateListOf<Long>() }
    val resumeTimestamps = remember { mutableStateListOf<Long>() }
    val startTime = remember { System.currentTimeMillis() }

    val studySessionVM: StudySessionViewModel = viewModel()
    val musicSessionVM: MusicSessionViewModel = viewModel()

    var currentSessionId by remember { mutableStateOf(studySessionId) }

    val progress = if (totalTime > 0) timeLeft / totalTime.toFloat() else 0f
    val formattedTime =
        String.format("%02d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, timeLeft % 60)

    val timerColor = when {
        timeLeft <= totalTime * 0.2 -> Color.Red
        timeLeft <= totalTime * 0.5 -> Orange
        else -> YellowGreen
    }

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val interFontName = GoogleFont("Inter")
    val fontFamily = FontFamily(
        androidx.compose.ui.text.googlefonts.Font(
            googleFont = interFontName,
            fontProvider = provider,
            weight = FontWeight.Normal
        )
    )

    var showHatchCard by remember { mutableStateOf(false) }
    var showNewEggCard by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }

            if (timeLeft == 0 && !isStopped) {
                isRunning = false
                isStopped = true
                val endTime = System.currentTimeMillis()
                val endTimestamp = Timestamp(endTime / 1000, ((endTime % 1000) * 1000000).toInt())
                val updates = mapOf("endedAt" to endTimestamp, "status" to "completed")
                studySessionVM.updateStudySession(currentSessionId, updates)
            }
        }
    }

    LaunchedEffect(Unit) {
        musicVM.loadMusic()
    }

    LaunchedEffect(musicList) {
        if (musicList.isNotEmpty() && currentMusic == null) {
            currentMusic = musicList.first()
        }
    }

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = null,
                onGroupsClick = {
                    context.startActivity(
                        Intent(
                            context,
                            DiscoverGroupsActivity::class.java
                        )
                    )
                },
                onHomeClick = { context.startActivity(Intent(context, MainActivity::class.java)) },
                onStatsClick = {
                    context.startActivity(
                        Intent(
                            context,
                            StatisticsActivity::class.java
                        )
                    )
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
                    context.startActivity(Intent(context, ProfileActivity::class.java).apply {
                        putExtra("userId", userId)
                    })
                },
                onSettingsClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java).apply {
                        putExtra("userId", userId)
                    })
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 50.dp, vertical = 10.dp)
            ) {
                Text(
                    subject,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = fontFamily,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(360.dp)) {
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
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 90.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally).offset(y = (-190).dp)
            ) {
                OutlinedButton(
                    onClick = {
                        timeLeft = 0
                        isRunning = false
                        isStopped = true
                        val endTime = System.currentTimeMillis()
                        val endTimestamp =
                            Timestamp(endTime / 1000, ((endTime % 1000) * 1000000).toInt())
                        val updates = mapOf("endedAt" to endTimestamp, "status" to "completed")
                        studySessionVM.updateStudySession(currentSessionId, updates)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
                    modifier = Modifier.width(100.dp).height(35.dp)
                ) {
                    Text("Stop", color = Color.White, fontFamily = fontFamily)
                }

                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = {
                        val now = System.currentTimeMillis()

                        if (isStopped || timeLeft == 0) {
                            coroutineScope.launch {
                                val newSession = StudySession(
                                    userId = userId.toString(),
                                    courseId = subject,
                                    hourSet = hours,
                                    minuteSet = minutes,
                                    moodId = moodId,
                                    sessionDate = getCurrentDate(),
                                    startedAt = Timestamp.now(),
                                    status = "active"
                                )

                                // Call the ViewModel method with callback correctly
                                studySessionVM.createStudySessionAndGetId(newSession) { newId ->
                                    currentSessionId = newId
                                    timeLeft = totalTime
                                    isRunning = true
                                    isStopped = false
                                    pauseTimestamps.clear()
                                    resumeTimestamps.clear()
                                }
                            }
                        } else {
                            isRunning = !isRunning

                            coroutineScope.launch {
                                val updates = mapOf(
                                    "status" to if (isRunning) "active" else "paused"
                                )
                                studySessionVM.updateStudySession(currentSessionId, updates)

                                if (isRunning) {
                                    resumeTimestamps.add(now)
                                } else {
                                    pauseTimestamps.add(now)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.DarkGray
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .height(35.dp)
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
                    currentMusic?.let { music ->
                        AudioPlayerCard(
                            currentMusic = music,
                            progress = 0.5f,
                            onShuffle = {},
                            onPrevious = {},
                            onPlayPause = {
                                musicSessionVM.createMusicSession(
                                    MusicSession(
                                        artist = music.artist,
                                        musicTitle = music.title,
                                        musicPlatform = "In-App",
                                        musicUri = music.albumArtUri,
                                        startTime = Timestamp.now(),
                                        userId = userId.toString(),
                                        studySessionId = currentSessionId
                                    )
                                )
                            },
                            onNext = {},
                            onRepeat = {}
                        )
                    }
                }
            }
        }
    }

    if (showHatchCard) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            HatchCard()
        }
    }
    if (showNewEggCard) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            NewEggCard(onContinueClick = {
                showHatchCard = false
                showNewEggCard = false
            })
        }
    }
}