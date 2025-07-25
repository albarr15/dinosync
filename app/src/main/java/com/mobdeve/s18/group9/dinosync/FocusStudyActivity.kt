
package com.mobdeve.s18.group9.dinosync

import HatchCard
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.mobdeve.s18.group9.dinosync.viewmodel.MusicViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCardSpotify
import com.mobdeve.s18.group9.dinosync.repository.local.LocalPlaybackManager
import com.mobdeve.s18.group9.dinosync.spotify.SpotifyConstants
import com.mobdeve.s18.group9.dinosync.spotify.SpotifyPlaybackManager
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import androidx.constraintlayout.compose.ConstraintLayout
import com.mobdeve.s18.group9.dinosync.viewmodel.CompanionViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.DailyStudyHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getCurDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Manila")
    return sdf.format(Date())
}

class FocusStudyActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val spotifyTitleState = mutableStateOf("Spotify Track")
    private val spotifyArtistState = mutableStateOf("Artist")
    private val spotifyAlbumArtBitmap  = mutableStateOf<Bitmap?>(null)
    private val spotifyIsPlayingState = mutableStateOf(false)
    private val spotifyProgressState = mutableStateOf(0f)
    private lateinit var spotifyPlaybackManager: SpotifyPlaybackManager
    private lateinit var localPlaybackManager: LocalPlaybackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getStringExtra("userId") ?: ""
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val selectedSubject = intent.getStringExtra("selected_subject") ?: ""
        val studySessionId = intent.getStringExtra("study_session_id") ?: ""
        val moodId = intent.getStringExtra("moodId") ?: ""

        spotifyPlaybackManager = SpotifyPlaybackManager(this)
        localPlaybackManager = LocalPlaybackManager(applicationContext)

        setContent {
            DinoSyncTheme {
                FocusStudyScreen(
                    userId = userId,
                    hours = hours,
                    minutes = minutes,
                    subject = selectedSubject,
                    studySessionId = studySessionId,
                    moodId = moodId,
                    spotifyAppRemote = spotifyAppRemote,
                    spotifyPlaybackManager = spotifyPlaybackManager,
                    localPlaybackManager = localPlaybackManager,
                    spotifyTitle = spotifyTitleState,
                    spotifyArtist = spotifyArtistState,
                    spotifyAlbumArtBitmap  = spotifyAlbumArtBitmap,
                    spotifyIsPlaying = spotifyIsPlayingState,
                    spotifyProgress = spotifyProgressState
                )
            }
        }
    }


    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("FocusStudyActivity onStart()")

        spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }

        val connectionParams = ConnectionParams.Builder(SpotifyConstants.CLIENT_ID)
            .setRedirectUri(SpotifyConstants.REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(remote: SpotifyAppRemote) {
                spotifyAppRemote = remote
                Log.d("♫ Spotify", "Connected to Spotify App Remote")
                connected()
            }

            override fun onFailure(error: Throwable) {
                Log.e("♫ Spotify", "Connection failed", error)
                when (error) {
                    is NotLoggedInException, is UserNotAuthorizedException -> {
                        Log.e("♫ Spotify", "User not logged in or authorized.")
                    }

                    is CouldNotFindSpotifyApp -> {
                        Log.e("♫ Spotify", "Spotify app not found. Prompt to install.")
                    }
                }
            }
        })
        spotifyPlaybackManager.connect { success ->
            if (success) {
                spotifyPlaybackManager.subscribeToPlayerState(
                    onPlayerStateChanged = { playerState ->
                        Log.d("Spotify", "${playerState.track.name} by ${playerState.track.artist.name}")
                    }
                )
            }
        }
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

        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
            Log.d("♫ Spotify", "Disconnected from App Remote")
        }
        spotifyPlaybackManager.disconnect()
        localPlaybackManager.stop()
    }

    override fun onRestart() {
        super.onRestart()
        println("FocusStudyActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("FocusStudyActivity onDestroy()")
        spotifyPlaybackManager.disconnect()
        localPlaybackManager.stop()
    }

    private fun connected() {
        spotifyAppRemote?.apply {
            playerApi.subscribeToPlayerState()
                .setEventCallback { playerState ->
                    val track = playerState.track ?: return@setEventCallback

                    // Update states
                    spotifyTitleState.value = track.name
                    spotifyArtistState.value = track.artist.name
                    spotifyIsPlayingState.value = !playerState.isPaused

                    val duration = track.duration.toFloat().coerceAtLeast(1f)
                    spotifyProgressState.value = playerState.playbackPosition.toFloat() / duration

                    // Load album art from Spotify API
                    imagesApi.getImage(track.imageUri)
                        .setResultCallback { bitmap ->
                            spotifyAlbumArtBitmap.value = bitmap
                        }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == SpotifyConstants.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    Log.d("♫ SpotifyAuth", "Access token received: $accessToken")
                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e("♫ SpotifyAuth", "Auth error: ${response.error}")
                }

                else -> {
                    Log.w("♫ SpotifyAuth", "Auth cancelled or unknown response.")
                }
            }
        }
    }
}


@Composable
fun FocusStudyScreen(
    userId: String,
    hours: Int,
    minutes: Int,
    subject: String,
    studySessionId: String,
    moodId: String,
    spotifyAppRemote: SpotifyAppRemote?,
    spotifyPlaybackManager: SpotifyPlaybackManager,
    localPlaybackManager: LocalPlaybackManager,
    spotifyTitle: State<String>,
    spotifyArtist: State<String>,
    spotifyAlbumArtBitmap: State<Bitmap?>,
    spotifyIsPlaying: State<Boolean>,
    spotifyProgress: State<Float>
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


    val studySessionVM: StudySessionViewModel = viewModel()
    var playbackMode by remember { mutableStateOf(PlaybackMode.IN_APP) }

    val companionVM: CompanionViewModel = viewModel()
    // Load companions for the user
    LaunchedEffect(userId) {
        companionVM.loadCompanions(userId)
    }
    val companions by companionVM.companions.collectAsState()
    // Get the most recent companion (if any)
    val currentCompanion = companions
        .filter { it.current }
        .maxByOrNull { it.dateCreated?.seconds ?: 0L }
    // Log.d("CompanionDebug", "Companions: $companions")
    // Log.d("CompanionDebug", "CurrentCompanion: $currentCompanion")

    var showHatchCard by remember { mutableStateOf(false) }

    // Watch for hatching
    /*
    LaunchedEffect(currentCompanion?.remainingHatchTime) {
        Log.d("CompanionDebug", "LaunchedEffect: remainingHatchTime = ${currentCompanion?.remainingHatchTime}")
        // Only show hatch card if companion was just awarded (not during active timer)
        if (currentCompanion != null &&
            currentCompanion.remainingHatchTime == 0 &&
            currentCompanion.dateAwarded != null) {
            showHatchCard = true
            Log.d("CompanionDebug", "Setting showHatchCard = true (hatch time reached)")
        }
    }
     */

    var currentSessionId by remember { mutableStateOf(studySessionId) }
    val dailyStudyHistoryVM: DailyStudyHistoryViewModel = viewModel()
    var currentStartedAt by remember { mutableStateOf<Timestamp?>(null) }

    val progress = if (totalTime > 0) timeLeft / totalTime.toFloat() else 0f
    val safeTimeLeft = maxOf(0, timeLeft)
    val formattedTime = String.format("%02d:%02d:%02d", safeTimeLeft / 3600, (safeTimeLeft % 3600) / 60, safeTimeLeft % 60)

    val timerColor = when {
        timeLeft <= totalTime * 0.2 -> Color.Red
        timeLeft <= totalTime * 0.5 -> Orange
        else -> YellowGreen
    }

    // FOR COMPANION: HATCH / NEW EGG CARD
    var showNewEggCard by remember { mutableStateOf(false) }


    var currentCompanionName = currentCompanion?.name
    var currentCompanionDrawableRes = currentCompanion?.getDrawableRes()

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ✅ Timer countdown logic
    LaunchedEffect(currentSessionId, currentCompanion) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft = maxOf(0, timeLeft - 1)
            }
            if (timeLeft == 0 && !isStopped) {
                isRunning = false
                isStopped = true

                val endTime = System.currentTimeMillis()
                val endTimestamp = Timestamp(endTime / 1000, ((endTime % 1000) * 1000000).toInt())
                val updates = mapOf("endedAt" to endTimestamp, "status" to "completed")
                studySessionVM.updateStudySession(currentSessionId, updates)

                val elapsedMinutes = calculateActualElapsedSeconds(
                    context = context,
                    totalTime = totalTime,
                    timeLeft = timeLeft
                )
                dailyStudyHistoryVM.updateDailyHistory(
                    userId = userId,
                    date = getCurDate(),
                    moodId = moodId,
                    additionalMinutes = (elapsedMinutes / 60).toFloat(),
                    studyMode = "Individual"
                )

                //Log.d("CompanionDebug", "Companions: $companions")
                //Log.d("CompanionDebug", "CurrentCompanion: $currentCompanion")

                if (currentCompanion != null) {
                    val sessionDuration = totalTime - timeLeft // in seconds
                    val newHatchTime = (currentCompanion.remainingHatchTime - sessionDuration).coerceAtLeast(0)

                    if (newHatchTime == 0 ) {
                        showHatchCard = true
                        delay(5000L)
                        showNewEggCard = true
                    }

                    companionVM.updateCurrentCompanion(
                        userId,
                        currentCompanion.current,
                        dateAwarded = endTimestamp,
                        remainingHatchTime = newHatchTime
                    )
                } else {
                    Log.d("CompanionVM ", "ERROR: no currentCompanion found.")
                }
            }
        }
    }

    var localProgress by remember { mutableStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        while (isPlaying && localPlaybackManager.isPlaying()) {
            delay(1000)
            val duration = localPlaybackManager.getDuration()
            val position = localPlaybackManager.getCurrentPosition()
            localProgress = if (duration > 0) position / duration.toFloat() else 0f
        }
    }


    // ✅ Load music
    LaunchedEffect(Unit) {
        Toast.makeText(context, "totalTime: $totalTime seconds", Toast.LENGTH_SHORT).show()
        musicVM.loadMusic() }

    LaunchedEffect(musicList) {
        if (musicList.isNotEmpty() && currentMusic == null) currentMusic = musicList.first()
    }

    val localPlaybackManager = remember {
        LocalPlaybackManager(context) {
            currentMusic?.let { current ->
                val index = musicList.indexOf(current)
                if (index in musicList.indices && index < musicList.lastIndex) {
                    val nextMusic = musicList[index + 1]
                    currentMusic = nextMusic
                    localPlaybackManager.play(nextMusic.filename)
                }
            }
        }
    }
    var currentTrackIndex = 0
    var isShuffleEnabled = false

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = null,
                onGroupsClick = { context.startActivity(Intent(context, DiscoverGroupsActivity::class.java)) },
                onHomeClick = { context.startActivity(Intent(context, MainActivity::class.java)) },
                onStatsClick = { context.startActivity(Intent(context, StatisticsActivity::class.java)) }
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

            // ✅ Top Bar
            TopActionBar(
                onProfileClick = {
                    val intent = Intent(context, ProfileActivity::class.java).apply {
                        putExtra("userId", userId)
                    }
                    context.startActivity(intent)
                },
                onSettingsClick = {
                    val intent = Intent(context, SettingsActivity::class.java).apply {
                        putExtra("userId", userId)
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Subject Title
            Box(
                modifier = Modifier
                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 50.dp, vertical = 10.dp)
            ) {
                Text(subject, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ✅ Timer Arc
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(360.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 50f
                    drawArc(
                        color = Color.LightGray,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                    drawArc(
                        color = timerColor,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }
                Text(
                    text = formattedTime,
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 90.dp)
                )
            }

            // Buttons + Music Section
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = (-200).dp)
            ) {
                val (stopPauseRow, musicSection) = createRefs()

                // Stop / Pause Buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.constrainAs(stopPauseRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val timeLeftBeforeStop = timeLeft  // Save before overwriting!
                                timeLeft = 0
                                isRunning = false
                                isStopped = true

                                val endTime = System.currentTimeMillis()
                                val endTimestamp = Timestamp(endTime / 1000, ((endTime % 1000) * 1000000).toInt())
                                val updates = mapOf("endedAt" to endTimestamp, "status" to "completed")
                                studySessionVM.updateStudySession(currentSessionId, updates)

                                Toast.makeText(context, "Stopped! timeLeft: $timeLeftBeforeStop", Toast.LENGTH_SHORT).show()
                                Log.d("OnStop Button", "Stopped! timeLeft: $timeLeftBeforeStop")

                                val elapsedMinutes = calculateActualElapsedSeconds(
                                    context = context,
                                    totalTime = totalTime,
                                    timeLeft = timeLeftBeforeStop
                                )
                                val minutesPart = (elapsedMinutes % 3600) / 60
                                val secondsPart = (elapsedMinutes % 60)
                                val elapsedTimeOnStop = (minutesPart*60) + secondsPart

                                Toast.makeText(context,  "Elapsed (actual): ${elapsedMinutes}, ${minutesPart}m ${secondsPart}s", Toast.LENGTH_SHORT).show()
                                Log.d("OnStop Button", "Elapsed (actual): ${elapsedMinutes}, ${elapsedTimeOnStop}, ${minutesPart}m ${secondsPart}s")

                                dailyStudyHistoryVM.updateDailyHistory(
                                    userId = userId,
                                    date = getCurDate(),
                                    moodId = moodId,
                                    additionalMinutes = (elapsedMinutes / 60).toFloat(),
                                    studyMode = "Individual"
                                )
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
                        modifier = Modifier
                            .width(100.dp)
                            .height(35.dp)
                    ) {
                        Text("Stop", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Button(
                        onClick = {
                            val startTimestamp = Timestamp.now()
                            coroutineScope.launch {
                                if (isStopped || timeLeft == 0) {
                                    // Record previous session before repeating
                                    val endTime = System.currentTimeMillis()
                                    val endTimestamp = Timestamp(
                                        endTime / 1000,
                                        ((endTime % 1000) * 1000000).toInt()
                                    )
                                    val updates = mapOf("endedAt" to endTimestamp, "status" to "completed")
                                    studySessionVM.updateStudySession(currentSessionId, updates)

                                    val elapsedMinutes = calculateActualElapsedSeconds(
                                        context = context,
                                        totalTime = totalTime,
                                        timeLeft = timeLeft
                                    )
                                  //elapsedTimeInSeconds%3600)/60
                                    val minutesPart = (elapsedMinutes % 3600) / 60
                                    val secondsPart = elapsedMinutes % 60
                                    Toast.makeText(context, "Elapsed: ${minutesPart}m ${secondsPart}s", Toast.LENGTH_SHORT).show()
                                    Log.d("Repeat/Pause/Resume", "Elapsed: ${minutesPart}m ${secondsPart}s")

                                    val currentDate = getCurDate()

                                    val newSession = StudySession(
                                        userId = userId,
                                        courseId = subject,
                                        hourSet = hours,
                                        minuteSet = minutes,
                                        moodId = moodId,
                                        sessionDate = getCurDate(),
                                        startedAt = startTimestamp,
                                        status = "active"
                                    )
                                    studySessionVM.createStudySessionAndGetId(newSession) { newId ->
                                        currentSessionId = newId
                                        currentStartedAt = startTimestamp
                                        timeLeft = totalTime
                                        pauseTimestamps.clear()
                                        resumeTimestamps.clear()
                                        isRunning = true
                                        isStopped = false

                                    }
                                } else {
                                    isRunning = !isRunning
                                    coroutineScope.launch {
                                        val updates =
                                            mapOf("status" to if (isRunning) "active" else "paused")
                                        studySessionVM.updateStudySession(currentSessionId, updates)

                                        if (isRunning) {
                                            val nowInSeconds = timeLeft
                                            resumeTimestamps.add(System.currentTimeMillis() / 1000)
                                            Toast.makeText(context, "Resumed at: $nowInSeconds ", Toast.LENGTH_SHORT).show()
                                            Log.d("Repeat/Pause/Resume", "Resumed at: $nowInSeconds")
                                            val pairedCount =
                                                minOf(pauseTimestamps.size, resumeTimestamps.size)
                                            pauseTimestamps.take(pairedCount)
                                                .zip(resumeTimestamps.take(pairedCount))
                                                .forEachIndexed { index, (pausedAt, resumedAt) ->
                                                    val duration = resumedAt - pausedAt
                                                    Toast.makeText(context, "Pause[$index] duration: ${(duration % 3600) / 60} min, ${duration % 60} seconds", Toast.LENGTH_SHORT).show()
                                                    Log.d("Repeat/Pause/Resume", "Pause[$index] duration: ${(duration % 3600) / 60} min, ${duration % 60} seconds")
                                                }

                                        } else {
                                            val nowInSeconds = timeLeft
                                            pauseTimestamps.add(System.currentTimeMillis() / 1000)
                                            Toast.makeText(context, "Paused  at: $nowInSeconds ", Toast.LENGTH_SHORT).show()
                                            Log.d("Repeat/Pause/Resume", "Paused at: $nowInSeconds")
                                        }
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
                        Text(if (isStopped || timeLeft == 0) "Repeat" else if (isRunning) "Pause" else "Resume")
                    }
                }


                Spacer(modifier = Modifier.width(30.dp))
                // Music Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.constrainAs(musicSection) {
                        top.linkTo(stopPauseRow.bottom, margin = 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { playbackMode = PlaybackMode.IN_APP },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
                        ) {
                            Text("In-App")
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = { playbackMode = PlaybackMode.SPOTIFY },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
                        ) {
                            Text("Spotify")
                        }
                    }

                    var isPlaying by remember { mutableStateOf(false) }
                    var isRepeatEnabled by remember { mutableStateOf(false) }
                    if (musicList.isEmpty()) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        if (currentMusic == null) {
                            currentMusic = musicList.first()
                        }

                        when (playbackMode) {
                            PlaybackMode.IN_APP -> {
                                currentMusic?.let { safeMusic ->

                                    // UI Composable
                                    AudioPlayerCard(
                                        currentMusic = safeMusic,
                                        progress = localPlaybackManager.getCurrentPosition().toFloat() / localPlaybackManager.getDuration().coerceAtLeast(1),
                                        isPlaying = isPlaying,
                                        isRepeatEnabled = isRepeatEnabled,
                                        onShuffle = {
                                            isShuffleEnabled = !isShuffleEnabled
                                            if (isShuffleEnabled) {
                                                val shuffled = musicList.shuffled()
                                                val newTrack = shuffled.firstOrNull()
                                                newTrack?.let {
                                                    currentTrackIndex = musicList.indexOf(it)
                                                    currentMusic = it
                                                    localPlaybackManager.play(it.filename)
                                                }
                                            }
                                        },
                                        onPrevious = {
                                            if (musicList.isNotEmpty()) {
                                                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else musicList.lastIndex
                                                currentMusic = musicList[currentTrackIndex]
                                                currentMusic?.let { localPlaybackManager.play(it.filename) }
                                            }
                                        },
                                        onPlayPause = {
                                            if (isPlaying) {
                                                localPlaybackManager.pause()
                                            } else {
                                                if (localPlaybackManager.getCurrentPosition() > 0) {
                                                    localPlaybackManager.resume()
                                                } else {
                                                    safeMusic.let {
                                                        localPlaybackManager.play(it.filename)
                                                    }
                                                }
                                            }
                                            isPlaying = !isPlaying
                                        },
                                        onNext = {
                                            if (musicList.isNotEmpty()) {
                                                if (isShuffleEnabled) {
                                                    val shuffled = musicList.shuffled()
                                                    val newTrack = shuffled.firstOrNull()
                                                    newTrack?.let {
                                                        currentTrackIndex = musicList.indexOf(it)
                                                        currentMusic = it
                                                        localPlaybackManager.play(it.filename)
                                                    }
                                                } else {
                                                    currentTrackIndex = if (currentTrackIndex < musicList.lastIndex) currentTrackIndex + 1 else 0
                                                    currentMusic = musicList[currentTrackIndex]
                                                    currentMusic?.let { localPlaybackManager.play(it.filename) }
                                                }
                                            }
                                        },
                                        onRepeat = {
                                            isRepeatEnabled = !isRepeatEnabled
                                            localPlaybackManager.setRepeatMode(isRepeatEnabled)
                                        }
                                    )

                                    // Track-ended listener setup (runs only once per currentMusic change)
                                    LaunchedEffect(currentMusic, isRepeatEnabled, musicList) {
                                        localPlaybackManager.setOnTrackEndedListener {
                                            val currentIndex = musicList.indexOf(currentMusic)

                                            if (isRepeatEnabled) {
                                                currentMusic?.let { localPlaybackManager.play(it.filename) }
                                            } else if (currentIndex < musicList.lastIndex) {
                                                currentMusic = musicList[currentIndex + 1]
                                                currentMusic?.let { localPlaybackManager.play(it.filename) }
                                            } else {
                                                isPlaying = false
                                                context.startActivity(Intent(context, MainActivity::class.java))
                                                (context as Activity).finish()
                                            }
                                        }
                                    }
                                }
                            }


                            PlaybackMode.SPOTIFY -> {
                                AudioPlayerCardSpotify(
                                    trackTitle = spotifyTitle.value,
                                    trackArtist = spotifyArtist.value,
                                    albumArtBitmap = spotifyAlbumArtBitmap.value,
                                    isPlaying = spotifyIsPlaying.value,
                                    progress = spotifyProgress.value,
                                    onPlayPause = {
                                        // when play create also a MusicSession
                                        spotifyPlaybackManager.getCurrentPlayerState { state ->
                                            if (state?.isPaused == true) spotifyPlaybackManager.resume()
                                            else spotifyPlaybackManager.pause()
                                        }
                                    },
                                    onNext = { spotifyPlaybackManager.skipToNext() },
                                    onPrevious = { spotifyPlaybackManager.skipToPrevious() },
                                    onShuffle = { },
                                    onRepeat = {  }
                                )
                            }

                        }

                    }
                }

            }
        }
    }


    if (showHatchCard){
            Box(
                modifier = Modifier.fillMaxSize().size(500.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentCompanionName != null) {
                    if (currentCompanionDrawableRes != null) {
                        HatchCard(currentCompanionName, currentCompanionDrawableRes)
                    } else {
                        Log.d("Hatch Card UI:", "current companion drawable is null.")
                    }
                } else {
                    Log.d("Hatch Card UI:", "current companion name is null.")
                }
            }
        }
     else {
        // Log.d("HatchCard", "showHatchCard = false")
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
    } else {
        // Log.d("HatchCard", "showNewEggCard = false")
    }
}


fun calculateActualElapsedSeconds(
    context: Context,
    totalTime: Int, // in seconds
    timeLeft: Int,  // in seconds
): Float  {

    val elapsedTimeInSeconds = (totalTime - timeLeft).toFloat() //- totalPausedSeconds

    Toast.makeText(context, "Actual elapsed: ${(elapsedTimeInSeconds%3600)/60} min, ${elapsedTimeInSeconds%60} secs", Toast.LENGTH_SHORT).show()
    return elapsedTimeInSeconds
}







