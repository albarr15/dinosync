
package com.mobdeve.s18.group9.dinosync

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCard
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCardSpotify
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.Mood
import com.mobdeve.s18.group9.dinosync.model.Music
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.mobdeve.s18.group9.dinosync.model.TodoDocument
import com.mobdeve.s18.group9.dinosync.model.TodoItem
import com.mobdeve.s18.group9.dinosync.model.User
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.GreenGray
import com.mobdeve.s18.group9.dinosync.ui.theme.Lime
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.CourseViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.DailyStudyHistoryViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.MoodViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.MusicViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.TodoViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.UserViewModel
import java.util.Date
import com.mobdeve.s18.group9.dinosync.spotify.SpotifyConstants
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track

import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import androidx.compose.runtime.State
import com.mobdeve.s18.group9.dinosync.repository.local.LocalPlaybackManager
import com.mobdeve.s18.group9.dinosync.spotify.SpotifyPlaybackManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


enum class PlaybackMode {
    IN_APP,
    SPOTIFY
}


fun fetchCurDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Manila")
    return sdf.format(Date())
}


class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val spotifyTitleState = mutableStateOf("Spotify Track")
    private val spotifyArtistState = mutableStateOf("Artist")
    private val spotifyAlbumArtBitmap  = mutableStateOf<Bitmap?>(null)
    private val spotifyIsPlayingState = mutableStateOf(false)
    private val spotifyProgressState = mutableStateOf(0f)
    private lateinit var spotifyPlaybackManager: SpotifyPlaybackManager
    private lateinit var playbackManager: LocalPlaybackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user!")

        spotifyPlaybackManager = SpotifyPlaybackManager(this)
        playbackManager = LocalPlaybackManager(applicationContext)

        setContent {
            DinoSyncTheme {
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
                Log.d("MainActivity", "First launch: $isFirstLaunch")

                var showRequireSpotifyModal by remember {
                    mutableStateOf(isFirstLaunch && !isSpotifyInstalled(this@MainActivity))
                }

                MainScreen(userId = userId)

                if (showRequireSpotifyModal) {
                    requireSpotifyModal {
                        showRequireSpotifyModal = false
                        prefs.edit().putBoolean("is_first_launch", false).apply()
                    }
                } else if (isFirstLaunch) {
                    // If Spotify is installed on first launch, still mark as not first launch
                    LaunchedEffect(Unit) {
                        prefs.edit().putBoolean("is_first_launch", false).apply()
                    }
                }
            }
        }
    }

    /**
     * Called when the activity is becoming visible to the user.
     *
     * This is typically where the Spotify connection is initiated by calling {@link #connectToSpotify()}.
     * Ensures the app reconnects to Spotify when the activity resumes.
     */

    override fun onStart() {
        super.onStart()
        println("MainActivity onStart()")

        if (!isSpotifyInstalled(this)) {
            return
        }

        val prefs = getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        val authCode = prefs.getString("authorization_code", null)

        if (authCode == null) {
            Toast.makeText(this, "Logging in to Spotify...", Toast.LENGTH_SHORT).show()
            val request = AuthorizationRequest.Builder(
                SpotifyConstants.CLIENT_ID,
                AuthorizationResponse.Type.CODE,
                SpotifyConstants.REDIRECT_URI
            )
                .setScopes(SpotifyConstants.SCOPES)
                .build()

            AuthorizationClient.openLoginActivity(this, SpotifyConstants.REQUEST_CODE, request)
            //Toast.makeText(this, "Connected to Spotify!", Toast.LENGTH_SHORT).show()
        } else{
            connectToSpotify()
            //Toast.makeText(this, "Connected to Spotify!", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Initiates a connection to the Spotify app using SpotifyAppRemote.
     *
     * This method sets the required connection parameters such as CLIENT_ID and REDIRECT_URI,
     * and attempts to establish a remote session. If successful, it calls {@link #connected()}.
     * On failure, the error is logged or handled appropriately.
     */

    private fun connectToSpotify() {
            spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }

            val connectionParams = ConnectionParams.Builder(SpotifyConstants.CLIENT_ID)
                .setRedirectUri(SpotifyConstants.REDIRECT_URI)
                .showAuthView(true)
                .build()

            SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
                override fun onConnected(remote: SpotifyAppRemote) {
                    spotifyAppRemote = remote
                    Log.d("♫ Spotify connectToSpotify", "Connected to Spotify App Remote")
                    connected()
                }

                override fun onFailure(error: Throwable) {
                    Log.e("♫ Spotify connectToSpotify", "Connection failed", error)
                    when (error) {
                        is NotLoggedInException, is UserNotAuthorizedException -> {
                            Log.e("♫ Spotify connectToSpotify", "User not logged in or authorized.")
                        }
                    }
                }
            })
            spotifyPlaybackManager.connect { success ->
                if (success) {
                    spotifyPlaybackManager.subscribeToPlayerState(
                        onPlayerStateChanged = { playerState ->
                            Log.d("♫ Spotify connectToSpotify", "${playerState.track.name} by ${playerState.track.artist.name}")
                        }
                    )
                }
            }
    }

    /**
     * Called when the activity is no longer visible to the user.
     *
     * This method is used to clean up resources, including disconnecting from Spotify
     * using SpotifyAppRemote.disconnect() to prevent memory leaks and save battery.
     */

    override fun onStop() {
        super.onStop()
        println("MainActivity onStop()")

        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
            Log.d("♫ Spotify", "Disconnected from App Remote")
        }
        spotifyPlaybackManager.disconnect() //Unresolved reference 'spotifyPlaybackManager'.
    }

    /**
     * Callback method invoked after a successful connection to Spotify.
     *
     * Use this method to interact with Spotify, such as fetching player state,
     * controlling playback, or updating the UI based on user session.
     *
     * This function is typically called from within the onConnected() method of SpotifyAppRemote.
     */

    private fun connected() {
        //Toast.makeText(this, "About to play music! connected() ", Toast.LENGTH_SHORT).show()
        spotifyAppRemote?.apply {
            // Uncomment to instantly play 'Sun Bleached Flies' upon logging in to Spotify.
            //playerApi.play("spotify:track:6fKIyDJHZ9m84jRhSmpuwS")

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
    /**
     * Handles the result returned from the Spotify authorization activity.
     *
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param intent The data returned from the Spotify login activity.
     *
     * This method extracts the AuthorizationResponse from the intent data and processes the result
     * based on its type (e.g., TOKEN, CODE, or ERROR).
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == SpotifyConstants.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    val authorizationCode = response.code
                    Log.d("♫ Spotify onActivityResult", "Authorization code received: $authorizationCode")

                    // Store code
                    val prefs = getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("authorization_code", authorizationCode).apply()

                    connectToSpotify() // If this relies only on AppRemote, this is fine

                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e("♫ Spotify onActivityResult", "Auth error: ${response.error}")
                    Toast.makeText(this, "Spotify authentication failed", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Log.w("♫ SpotifyAuth", "Auth cancelled or unknown response.")
                }
            }
        }
    }




    /******** ACTIVITY LIFE CYCLE ******** */

    override fun onResume() {
        super.onResume()
        println("MainActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("MainActivity onPause()")
    }

    override fun onRestart() {
        super.onRestart()
        println("MainActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MainActivity onDestroy()")
    }
}

    /******** MAIN SCREEN *********/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String
) {
    val context = LocalContext.current

    var showSpotifyRequiredModal by remember { mutableStateOf(false) }
    if (showSpotifyRequiredModal) {
       requireSpotifyModal(onDismiss = { showSpotifyRequiredModal = false })
    }

    // Collect ViewModels
    val moodVM: MoodViewModel = viewModel()
    val studySessionVM: StudySessionViewModel = viewModel()
    val dailyHistoryVM: DailyStudyHistoryViewModel = viewModel()
    val userVM: UserViewModel = viewModel()
    val courseVM: CourseViewModel = viewModel()
    val musicVM: MusicViewModel = viewModel()

    val musicList by musicVM.musicList.collectAsState()
    var currentMusic by remember { mutableStateOf<Music?>(null) }

    val todoVM: TodoViewModel = viewModel()
    val todoDocs by todoVM.todos.collectAsState()

    LaunchedEffect(musicList) {
        if (musicList.isNotEmpty() && currentMusic == null) {
            currentMusic = musicList.first()
        }
    }

    LaunchedEffect(Unit) {
        moodVM.loadMoods()
        studySessionVM.loadStudySessions(userId)
        dailyHistoryVM.loadDailyHistory(userId)
        userVM.loadUser(userId)
        courseVM.loadCourses()
        musicVM.loadMusic()
    }

    val moods by moodVM.moods.collectAsState()
    var hoursSet by remember { mutableStateOf("") }
    var minutesSet by remember { mutableStateOf("") }
    var showMoodDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    val courseList by courseVM.courses.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Home",
                onGroupsClick = {
                    //Log.d("CurrentUser", "Logged in userId in onGroupsClick = $userId")
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

            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            /******** Editable Course Dropdown ********/
            val courseNames = courseList.map { it.name }
            var expanded by remember { mutableStateOf(false) }
            var inputText by remember { mutableStateOf("") }
            var selectedCourse by remember { mutableStateOf("") }

            val filteredCourses = if (inputText.isBlank()) {
                courseNames
            } else {
                courseNames.filter {
                    it.contains(inputText, ignoreCase = true)
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .width(350.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { newValue ->
                        inputText = newValue
                    },
                    label = { Text("Select or Type Subject") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = DarkGreen,
                        focusedLeadingIconColor = DarkGreen,
                        focusedIndicatorColor = DarkGreen,
                        focusedLabelColor = DarkGreen
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded && filteredCourses.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    filteredCourses.forEach { courseName ->
                        DropdownMenuItem(
                            text = { Text(courseName) },
                            onClick = {
                                inputText = courseName
                                selectedCourse = courseName
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (inputText.isNotBlank() && !courseNames.contains(inputText)) {
                Button(
                    onClick = {
                        courseVM.addCourseIfNew(inputText.trim())
                        selectedCourse = inputText.trim()
                        inputText = ""
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Add \"$inputText\" as New Course")
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            /******** Timer Activity *********/
            TimerInput(
                hoursSet = hoursSet,
                onHoursChange = { hoursSet = it },
                minutesSet = minutesSet,
                onMinutesChange = { minutesSet = it },
                onReset = {
                    hoursSet = ""
                    minutesSet = ""
                },
                onStartClicked = { showMoodDialog = true }
            )

            if (showMoodDialog) {
                AlertDialog(
                    onDismissRequest = { showMoodDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                showMoodDialog = false
                                val hourInt = hoursSet.toIntOrNull() ?: 0
                                val minuteInt = minutesSet.toIntOrNull() ?: 0
                                if (hourInt == 0 && minuteInt == 0) return@Button

                                val moodId = selectedMood?.imageKey ?: return@Button
                                val sessionDate = getCurrentDate()
                                val startedAt = getCurrentDateTime()

                                val session = StudySession(
                                    userId = userId,
                                    courseId = selectedCourse,
                                    hourSet = hourInt,
                                    minuteSet = minuteInt,
                                    moodId = moodId,
                                    sessionDate = fetchCurDate(),
                                    startedAt = getCurrentTimestamp(),
                                    endedAt = null,
                                    status = "active" // active,pause, reset, completed
                                )
                                studySessionVM.createStudySessionAndGetId(session) { sessionId ->
                                    Toast.makeText(context, "Session logged successfully!", Toast.LENGTH_SHORT).show()

                                    // Reset fields
                                    hoursSet = ""
                                    minutesSet = ""
                                    selectedMood = null
                                    showMoodDialog = false

                                    // Start FocusStudyActivity with sessionId
                                    val intent =
                                        Intent(context, FocusStudyActivity::class.java).apply {
                                            putExtra("userId", userId)
                                            putExtra("hours", hourInt)
                                            putExtra("minutes", minuteInt)
                                            putExtra("selected_subject", selectedCourse)
                                            putExtra("study_session_id", sessionId)
                                            putExtra("moodId", moodId)
                                        }
                                    context.startActivity(intent)
                                }

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkGreen,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Log", color = Color.White)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = {
                            showMoodDialog = false
                            val hourInt = hoursSet.toIntOrNull() ?: 0
                            val minuteInt = minutesSet.toIntOrNull() ?: 0
                            if (hourInt == 0 && minuteInt == 0) return@OutlinedButton

                            val sessionDate = getCurrentDate()
                            val startedAt = getCurrentTimestamp()

                            val skippedSession = StudySession(
                                userId = userId,
                                courseId = selectedCourse,
                                hourSet = hourInt,
                                minuteSet = minuteInt,
                                moodId = "",
                                sessionDate = fetchCurDate(),
                                startedAt = startedAt,
                                endedAt = null,
                                status = "active"
                            )

                            studySessionVM.createStudySessionAndGetId(skippedSession) { sessionId ->
                                val intent = Intent(context, FocusStudyActivity::class.java).apply {
                                    putExtra("userId", userId)
                                    putExtra("hours", hourInt)
                                    putExtra("minutes", minuteInt)
                                    putExtra("selected_subject", selectedCourse)
                                    putExtra("study_session_id", sessionId)
                                    putExtra("moodId", "")
                                }
                                context.startActivity(intent)

                                // Reset state after skip
                                hoursSet = ""
                                minutesSet = ""
                                selectedMood = null
                                showMoodDialog = false
                            }
                        }) {
                            Text("Skip")
                        }
                    },
                    text = {
                        MoodInput(
                            moods = moods,
                            selectedMood = selectedMood,
                            onMoodSelected = { selectedMood = it }
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

            /******** TodoList *********/
            TodoList(
                todoItems = todoDocs,
                onAddItem = { title ->
                    val newItem = TodoItem(
                        title = title,
                        userId = userId,
                        isChecked = false
                    )
                    todoVM.addTodo(newItem)
                },
                onToggleCheck = { id, item ->
                    val updated = item.copy(isChecked = !item.isChecked)
                    todoVM.updateTodo(id, updated)
                },
                onEditTitle = { id, newTitle ->
                    val item = todoDocs.find { it.id == id }?.item ?: return@TodoList
                    todoVM.updateTodo(id, item.copy(title = newTitle))
                },
                onDelete = { id ->
                    todoVM.deleteTodo(id, userId)
                }
            )

            Spacer(modifier = Modifier.height(5.dp))

        }
    }
}

fun getCurrentDate(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}

fun getCurrentDateTime(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
    sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
    return sdf.format(java.util.Date())
}

fun getCurrentTimestamp(): Timestamp {
    return Timestamp(Date())
}

@Composable
fun MoodInput(
    moods: List<Mood>,
    selectedMood: Mood?,
    onMoodSelected: (Mood?) -> Unit
) {
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

    val moodIcons = mapOf(
        "very_dissatisfied" to Icons.Filled.SentimentVeryDissatisfied,
        "dissatisfied" to Icons.Filled.SentimentDissatisfied,
        "neutral" to Icons.Filled.SentimentNeutral,
        "satisfied" to Icons.Filled.SentimentSatisfied,
        "very_satisfied" to Icons.Filled.SentimentVerySatisfied
    )

    val moodOrder = listOf(
        "very_dissatisfied",
        "dissatisfied",
        "neutral",
        "satisfied",
        "very_satisfied"
    )

    // Map imageKey -> Mood for easy lookup
    val moodMap = moods.associateBy { it.imageKey }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(20.dp).background(Color.White)
    ) {
        Column (modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                "Log your pre-task mood",
                fontFamily = fontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                moodOrder.forEach { key ->
                    val mood = moodMap[key] ?: return@forEach
                    val icon = moodIcons[mood.imageKey] ?: Icons.Filled.SentimentNeutral
                    val isSelected = selectedMood?.imageKey == mood.imageKey

                    Box(
                        modifier = Modifier
                            .size(20.dp).scale(1.5f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(if (isSelected) YellowGreen else Color.LightGray)
                            .clickable {
                                onMoodSelected(if (isSelected) null else mood)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mood.name,
                            tint = if (isSelected) Color.White else Color.DarkGray,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun TimerInput(
    hoursSet: String,
    onHoursChange: (String) -> Unit,
    minutesSet: String,
    onMinutesChange: (String) -> Unit,
    onReset: () -> Unit,
    onStartClicked: () -> Unit
) {
    var isHoursFocused by remember { mutableStateOf(false) }
    var isMinutesFocused by remember { mutableStateOf(false) }

    val context = LocalContext.current
    //Font Dependency
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GreenGray),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // HOURS
                OutlinedTextField(
                    value = hoursSet,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 2) onHoursChange(it)
                    },
                    placeholder = {
                        Text(
                            "00",
                            fontSize = 50.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 50.sp, textAlign = TextAlign.Center, color = if (isHoursFocused) DarkGreen else Color.Black, fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .width(150.dp)
                        .onFocusChanged { focusState -> isHoursFocused = focusState.isFocused }
                        .background(if (isHoursFocused) Lime else Color.LightGray, RoundedCornerShape(10.dp))
                        .border(
                            width = 2.dp,
                            color = if (isHoursFocused) DarkGreen else Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ,
                    shape = RoundedCornerShape(10.dp),
                )

                Text(":", fontSize = 50.sp, color = Color.Black, modifier = Modifier.padding(horizontal = 8.dp))

                // MINUTES
                OutlinedTextField(
                    value = minutesSet,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 2) onMinutesChange(it)
                    },
                    placeholder = {
                        Text(
                            "00",
                            fontSize = 50.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 50.sp, textAlign = TextAlign.Center, color = if (isMinutesFocused) DarkGreen else Color.Black, fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .width(150.dp)
                        .onFocusChanged { focusState -> isMinutesFocused = focusState.isFocused }
                        .align(Alignment.CenterVertically)
                        .background(if (isMinutesFocused) Lime else Color.LightGray, RoundedCornerShape(10.dp))
                        .border(
                            width = 2.dp,
                            color = if (isMinutesFocused) DarkGreen else Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
            ) {
                Text(
                    "Hours",
                    color = Color.Black,
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(130.dp))
                Text(
                    "Minutes",
                    color = Color.Black,
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp

                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReset,
                    colors = ButtonDefaults.outlinedButtonColors(Color.Transparent),
                    modifier = Modifier.width(130.dp).height(35.dp)
                ) {
                    Text("Reset", color = Color.Black, fontFamily = fontFamily)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = onStartClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = DirtyGreen, contentColor = Color.DarkGray),
                    modifier = Modifier.width(130.dp).height(35.dp)
                ) {
                    Text("Start", fontFamily = fontFamily, color = Color.White)
                }

            }
        }
    }
}


@Composable
fun TodoList(
    todoItems: List<TodoDocument>,
    onAddItem: (String) -> Unit,
    onToggleCheck: (String, TodoItem) -> Unit,
    onEditTitle: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var newItemTitle by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (expanded) Modifier.fillMaxHeight() else Modifier.wrapContentHeight())
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .padding(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("TODO", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .size(30.dp),
                )
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newItemTitle,
                                onValueChange = { newItemTitle = it },
                                singleLine = true,
                                placeholder = { Text("Add New Task...") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedLeadingIconColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLeadingIconColor = DarkGreen,
                                    focusedIndicatorColor = DarkGreen,
                                    focusedLabelColor = DarkGreen
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                if (newItemTitle.isNotBlank()) {
                                    onAddItem(newItemTitle)
                                    newItemTitle = ""
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }

                    itemsIndexed(todoItems) { _, doc ->
                        val item = doc.item
                        var localTitle by remember(doc.id) { mutableStateOf(item.title) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            OutlinedTextField(
                                value = localTitle,
                                onValueChange = { newTitle ->
                                    localTitle = newTitle
                                },
                                singleLine = true,
                                placeholder = { Text("Task...") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedLeadingIconColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLeadingIconColor = DarkGreen,
                                    focusedIndicatorColor = DarkGreen,
                                    focusedLabelColor = DarkGreen
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { focusState ->
                                    if (!focusState.isFocused && localTitle != item.title) {
                                        onEditTitle(doc.id, localTitle)
                                    }
                                }
                            )
                            IconButton(onClick = {
                                onToggleCheck(doc.id, item)
                            }) {
                                Icon(
                                    imageVector = if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = {
                                onDelete(doc.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isSpotifyInstalled(context: Context): Boolean {
    return try {
        context.packageManager.getPackageInfo("com.spotify.music", 0)
        true
    } catch (e: Exception) {
        false
    }
}


@Composable
fun requireSpotifyModal(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Spotify Required") },
        text = {
            Text("To play music from your Spotify account during study sessions, please install the Spotify app before proceeding.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val spotifyIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
                        setPackage("com.android.vending")
                    }
                    context.startActivity(spotifyIntent)
                }
            ) {
                Text("Open Play Store")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )
}


