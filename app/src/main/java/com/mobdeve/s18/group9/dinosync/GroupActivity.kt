
package com.mobdeve.s18.group9.dinosync

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.GroupSessionsLineChart
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.Course
import com.mobdeve.s18.group9.dinosync.model.DailyStudyHistory
import com.mobdeve.s18.group9.dinosync.model.GroupMember
import com.mobdeve.s18.group9.dinosync.model.Mood
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.mobdeve.s18.group9.dinosync.model.User
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.LightGray
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.CourseViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.DailyStudyHistoryViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.GroupMemberViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.MoodViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudyGroupViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.getValue


class GroupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId") ?: ""
        val groupId = intent.getStringExtra("groupId") ?: ""

        val userVM: UserViewModel by viewModels()
        val groupVM: StudyGroupViewModel by viewModels()
        val memberVM: GroupMemberViewModel by viewModels()
        val historyVM: DailyStudyHistoryViewModel by viewModels()
        val studySessionVM: StudySessionViewModel by viewModels()
        val moodVM: MoodViewModel by viewModels()
        val courseVM: CourseViewModel by viewModels()

        val selectedMoodState = mutableStateOf<Mood?>(null)

        setContent {
            val allUsers by userVM.allUsers.collectAsState()
            val allGroups by groupVM.studyGroups.collectAsState()
            val allMembers by memberVM.members.collectAsState()
            val allHistory by historyVM.dailyHistory.collectAsState()
            val studySessionList = studySessionVM.studySessions.collectAsState()
            val moods by moodVM.moods.collectAsState()
            val allCourses by courseVM.courses.collectAsState()


            LaunchedEffect(Unit) {
                userVM.loadAllUsers()
                groupVM.loadStudyGroups()
                memberVM.loadAllMembers()
                historyVM.loadDailyHistory(userId)
                studySessionVM.loadStudySessions(userId)
                moodVM.loadMoods()
                courseVM.loadCourses()
            }

            val selectedGroup = allGroups.find { it.groupId == groupId }
            val course = allCourses.find { it.courseId == selectedGroup?.courseId }
            val groupMembers = allMembers.filter { it.groupId == groupId }
            val groupHistory = allHistory.filter { member ->
                groupMembers.any { it.userId == member.userId }
            }

            DinoSyncTheme {
                GroupActivityScreen(
                    userId = userId,
                    course = course,
                    group = selectedGroup,
                    allGroupMembers = allMembers,
                    groupMembers = groupMembers,
                    allUsers = allUsers,
                    dailyStudyHistory = groupHistory,
                    dailyHistoryVM = historyVM,
                    studySessions = studySessionList.value,
                    moods = moods,
                    selectedMoodState = selectedMoodState,
                    onJoinGroup = { newMember ->
                        lifecycleScope.launch {
                            memberVM.addGroupMember(newMember)
                        }
                    },
                    onJoinComplete = { newMember, historyEntry ->
                        lifecycleScope.launch {
                            historyVM.updateDailyHistory(
                                userId = userId,
                                date = fetchCurDate(),
                                moodId = selectedMoodState.value?.name ?: "",
                                additionalMinutes = newMember.currentGroupStudyMinutes,
                                studyMode = "Group",
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onStart() { super.onStart(); println("GroupActivity onStart()") }
    override fun onResume() { super.onResume(); println("GroupActivity onResume()") }
    override fun onPause() { super.onPause(); println("GroupActivity onPause()") }
    override fun onStop() { super.onStop(); println("GroupActivity onStop()") }
    override fun onRestart() {  super.onRestart(); println("GroupActivity onRestart()") }
    override fun onDestroy() { super.onDestroy(); println("GroupActivity onDestroy()")  }
}


@Composable
fun GroupActivityScreen(
    userId: String,
    course: Course?,
    group: StudyGroup?,
    allGroupMembers : List<GroupMember>,
    groupMembers: List<GroupMember>,
    allUsers: List<User>,
    dailyStudyHistory: List<DailyStudyHistory>,
    dailyHistoryVM : DailyStudyHistoryViewModel,
    studySessions: List<StudySession>,
    onJoinGroup: (GroupMember) -> Unit,
    moods: List<Mood>,
    selectedMoodState: MutableState<Mood?>,
    onJoinComplete: (GroupMember, DailyStudyHistory) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Group Activity") }
    val groupMemberVM: GroupMemberViewModel = viewModel()

    // Observe StateFlow using collectAsState (this is reactive!)
    val realTimeMembers by groupMemberVM.members.collectAsState()

    LaunchedEffect(Unit) {
        groupMemberVM.loadAllMembers()
    }

    // Local stateful list mirroring current group members for recomposition
    val groupMembersState = remember { mutableStateListOf<GroupMember>() }

    // Compose derived list of users currently in this group (not left yet)
    val memberUsers by remember(groupMembersState, allUsers, group) {
        derivedStateOf {
            groupMembersState
                .filter { it.groupId == group?.groupId && it.endedAt.isNullOrEmpty() }
                .mapNotNull { gm ->
                    val user = allUsers.find { it.userId == gm.userId }
                    user?.let { user -> user to gm } // Pair<User, GroupMember>
                }
        }
    }

    // Top 3 members based on study minutes
    val topMembers =
        memberUsers.sortedByDescending { it.second.currentGroupStudyMinutes }.map { it.first }
            .take(3)
    // Setup for using Google Fonts
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

    // Boolean derived to check if current user has an active membership
    val isMemberJoined by remember(allGroupMembers, userId) {
        derivedStateOf {
            allGroupMembers.any { it.userId == userId && it.endedAt.isNullOrEmpty() }
        }
    }
    // Flag to show mood dialog
    var showJoinDialog by remember { mutableStateOf(false) }
    // Input minutes
    var targetStudyPeriodMinutes by remember { mutableStateOf("") }
    // Flag to show time dialog
    var showTargetDialog by remember { mutableStateOf(false) }


    fun onLeaveGroup(userId: String, groupId: String) {

        val members = groupMemberVM.members.value
        val activeMember = members.find {
            it.userId == userId && it.groupId == groupId && it.endedAt.isNullOrEmpty()
        }
        // Instead of basing the startedAt on groupMemberState, get it this directly form firebase from GroupMemberViewModel

        val startedAt = activeMember?.startedAt ?: ""

        if (startedAt.isNotEmpty()) {
            Log.d("onLeaveGroup", "Found activeMember with startedAt=$startedAt")
            groupMemberVM.leaveGroup(userId, groupId, startedAt, getCurrentDateTime())
        } else {
            Log.e("onLeaveGroup", "No active group member session found.")
        }
    }

    LaunchedEffect(realTimeMembers) {
        groupMembersState.clear()
        groupMembersState.addAll(realTimeMembers)
    }

    Scaffold(
        containerColor = DarkGreen,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Groups",
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

        val startedAt = getCurrentDateTime();
        val currentDate = getCurrentDate();

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
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Group Page",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
                    .padding(vertical = 5.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2F6437))
                    .padding(20.dp)
            ) {

                Column {
                    // JOIN/LEAVE button
                    Box(
                        modifier = Modifier.width(60.dp).clip(RoundedCornerShape(16.dp)).height(25.dp).align(Alignment.End).background(YellowGreen)
                            .clickable(
                                onClick = {
                                    // Leave group: update backend and UI state
                                    if (isMemberJoined) {
                                        onLeaveGroup(userId, group?.groupId ?:"")

                                        val index = groupMembersState.indexOfFirst {
                                            it.userId == userId && it.groupId == group?.groupId
                                        }
                                        if (index != -1) {
                                            val updatedMember = groupMembersState[index].copy(endedAt = getCurrentTimestamp().toString())
                                            groupMembersState[index] = updatedMember
                                        }
                                    } else {
                                        // Show mood dialog to join
                                        showJoinDialog = true
                                    }
                                }
                            )
                    ) {
                        Text(
                            text = if (isMemberJoined) "LEAVE" else "JOIN",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                    }
                    // Dialog to input target hours
                    if (showJoinDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showJoinDialog = false; selectedMoodState.value = null
                            },
                            title = { Text("Pre-Task Mood") },
                            text = {
                                Column {
                                    MoodInput(
                                        moods = moods,
                                        selectedMood = selectedMoodState.value,
                                        onMoodSelected = {
                                            selectedMoodState.value = it
                                        }
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    val mood = selectedMoodState.value
                                    if (mood != null) {
                                        showJoinDialog = false
                                        showTargetDialog =
                                            true // Trigger the next dialog
                                    }
                                }) {
                                    Text("Next")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showJoinDialog = false }) {
                                    Text("Cancel")
                                }
                            },
                            containerColor = Color.White
                        )
                    }
                    if (showTargetDialog) {
                        AlertDialog(
                            onDismissRequest = { showTargetDialog = false },
                            title = { Text("Join Study Group") },
                            text = {
                                Column {
                                    Text("Enter target study minutes:")
                                    TextField(
                                        value = targetStudyPeriodMinutes,
                                        onValueChange = { targetStudyPeriodMinutes = it },
                                        placeholder = { Text("e.g. 30") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    val minutes = targetStudyPeriodMinutes.toFloatOrNull()?: 0f
                                    if (group != null && selectedMoodState.value != null) {
                                        val newMember = GroupMember(
                                            startedAt = startedAt,
                                            endedAt = "",
                                            currentGroupStudyMinutes = minutes,
                                            groupId = group.groupId,
                                            onBreak = false,
                                            userId = userId
                                        )
                                        // validate if there's no instance yet in db before adding
                                        val historyEntry = DailyStudyHistory(
                                            date = currentDate,
                                            hasStudied = true,
                                            moodEntryId = selectedMoodState.value!!.name,
                                            totalGroupStudyMinutes = minutes,
                                            totalIndividualMinutes = 0f,
                                            userId = userId
                                        )
                                        onJoinGroup(newMember)
                                        groupMembersState.add(newMember)
                                        onJoinComplete(newMember, historyEntry)
                                        showTargetDialog = false
                                    }
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showTargetDialog = false }) {
                                    Text("Cancel")
                                }
                            },
                            containerColor = Color.White
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = group?.name ?: "Sample group name",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                    }
                    Text(
                        text = group?.bio ?: "Sample bio",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    )
                    Text(
                        text = "Course: ${course?.name ?: "Unknown Course"}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    )
                    Text(
                        text = group?.university ?: "Unknown University",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    )
                    val (rank, totalGroups) = calculateGroupRanking(group?.groupId ?: "", allGroupMembers)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rank $rank out of $totalGroups",
                            color = Color.White,
                            fontStyle = FontStyle.Italic,
                            fontFamily = fontFamily,
                            fontSize = 13.sp
                        )
                        Row(modifier = Modifier.height(30.dp)) {
                            val opacities = listOf(1f, 0.8f, 0.6f, 0.4f)
                            memberUsers.take(opacities.size).forEachIndexed { index, (user, _) ->
                                Box(
                                    modifier = Modifier
                                        .zIndex((opacities.size - index).toFloat())
                                        .offset(x = (-7 * index).dp)
                                        .size(30.dp)
                                        .alpha(opacities[index])
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(user.userProfileImage),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Show button to start session if user is a member
            if (isMemberJoined) {
                val userGroupMember = groupMembersState.find { it.userId == userId && it.endedAt.isNullOrEmpty() }

                if (userGroupMember != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Button(
                            onClick = {
                                groupMemberVM.startNewGroupSession(
                                    dailyStudyHistoryViewModel = dailyHistoryVM,
                                    userId = userId,
                                    moodId = selectedMoodState.value!!.name,
                                    groupMember = userGroupMember,
                                    additionalMinutes = targetStudyPeriodMinutes.toFloatOrNull()?: 0f,
                                    startedAt = startedAt
                                )

                                val updated = userGroupMember.copy(
                                    startedAt =startedAt,
                                    endedAt = "",
                                    onBreak = false,
                                    currentGroupStudyMinutes = targetStudyPeriodMinutes.toFloatOrNull()?: 0f
                                )
                                val idx = groupMembersState.indexOf(userGroupMember)
                                if (idx != -1) groupMembersState[idx] = updated
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text("Start New Session", color = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Tab buttons (Group Activity / Stats)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedTab = "Group Activity" },
                    colors = ButtonDefaults.buttonColors(if (selectedTab == "Group Activity") YellowGreen else LightGray),
                    modifier = Modifier.width(170.dp).clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                    shape = RectangleShape
                ) {
                    Text(
                        "Group Activity",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { selectedTab = "Stats" },
                    colors = ButtonDefaults.buttonColors(if (selectedTab == "Stats") YellowGreen else LightGray),
                    modifier = Modifier
                        .width(170.dp)
                        .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                    shape = RectangleShape
                ) {
                    Text(
                        "Stats",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Render content based on selected tab
            Box(modifier = Modifier.fillMaxWidth()) {
                when (selectedTab) {
                    "Group Activity" -> OnClickGroupActivityBtn(memberUsers, dailyHistoryVM)
                    "Stats" -> OnClickGroupStatsActivityBtn(
                        topMembers = ArrayList(topMembers),
                        group?.groupId.toString(),
                        dailyStudyHistory,
                        studySessions,
                        groupMembers                //groupMembers: List<GroupMember>
                    )
                }
            }
        }
    }
}
@Composable
fun UserCard(user: User, groupMember: GroupMember, dailyHistoryVM : DailyStudyHistoryViewModel, onSessionEnd: (GroupMember) -> Unit) {

    val currentDate = fetchCurDate()
    val studyMinutes by dailyHistoryVM.getTotalGroupStudyMinutes(user.userId, currentDate).collectAsState(initial = 0f)
    val initialTimeInSeconds = (groupMember.currentGroupStudyMinutes * 60).toInt()
    var remainingSeconds by remember { mutableStateOf(initialTimeInSeconds) }

    var isRunning = groupMember.endedAt.isNullOrEmpty() && !groupMember.onBreak && remainingSeconds > 0

    // To reset the timer upon session restart
    LaunchedEffect(groupMember.startedAt, studyMinutes) {
        remainingSeconds = initialTimeInSeconds
        isRunning = true

        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }

        if (remainingSeconds == 0) {
            isRunning = false
            onSessionEnd(groupMember)
        }
    }

    val imageRes = when {
        groupMember.onBreak || remainingSeconds == 0 -> R.drawable.inactive
        studyMinutes >= 240 -> R.drawable.greaterthanequal4hr
        studyMinutes >= 60 -> R.drawable.greaterthanequal1hr
        else -> R.drawable.lessthan1hr
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when {
                groupMember.onBreak -> R.drawable.inactive
                groupMember.currentGroupStudyMinutes >= 240 -> R.drawable.greaterthanequal4hr
                groupMember.currentGroupStudyMinutes >= 60 -> R.drawable.greaterthanequal1hr
                else -> R.drawable.lessthan1hr
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(
                    if (imageRes == R.drawable.greaterthanequal4hr) 60.dp else 50.dp
                )
            )
        }

        Text(user.userName, fontSize = 12.sp)

        // Show countdown or "On Break"
        if (!groupMember.endedAt.isNullOrEmpty() || groupMember.onBreak || remainingSeconds <= 0) {
            Text("On Break", fontSize = 12.sp)
        } else {
            Text(
                text = formatElapsedTime(remainingSeconds),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OnClickGroupActivityBtn(memberUsers: List<Pair<User, GroupMember>>, dailyHistoryVM : DailyStudyHistoryViewModel) {
    LaunchedEffect(memberUsers) {
        Log.d("OnClickGroupActivityBtn", "memberUsers size: ${memberUsers.size}")
        memberUsers.forEachIndexed { index, (user, groupMember) ->
            Log.d("OnClickGroupActivityBtn", "[$index] user=${user.userId}, groupMember=${groupMember.groupId}")
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier
                    .width(365.dp)
                    .offset(y = (-5).dp)
                    .align(Alignment.Center),
                thickness = 0.5.dp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(memberUsers.size) { index ->
                val (user, groupMember) = memberUsers[index]
                UserCard(user, groupMember, dailyHistoryVM, onSessionEnd = {})
            }
        }
    }
}

fun formatElapsedTime(seconds: Int): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hrs, mins, secs)
}

fun calculateGroupRanking(
    targetGroupId: String,
    groupMembers: List<GroupMember>
): Pair<Int, Int> {
    // Sum study minutes per group
    val totalMinutesPerGroup: Map<String, Float> = groupMembers
        .groupBy { it.groupId }
        .mapValues { entry ->
            entry.value.sumOf { it.currentGroupStudyMinutes.toDouble() }.toFloat()
        }

    // Sort groups by descending study time
    val rankedGroups: List<Pair<String, Float>> = totalMinutesPerGroup
        .toList()
        .sortedByDescending { it.second }

    // Find the rank
    val rank = rankedGroups.indexOfFirst { it.first == targetGroupId } + 1
    val totalGroups = rankedGroups.size

    return rank to totalGroups
}



@Composable
fun OnClickGroupStatsActivityBtn(
    topMembers: ArrayList<User>,
    selectedGroup: String,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    groupMembers: List<GroupMember>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 180.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier
                    .width(365.dp)
                    .offset(y = (-5).dp)
                    .align(Alignment.Center),
                thickness = 0.5.dp,
                color = Color.Black
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Members Section
            Text(
                text = "Top Members",
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                topMembers.take(2).forEach { user ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(user.userProfileImage),
                                contentDescription = user.userName,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(user.userName, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Sessions Section
            Text(
                text = "Sessions",
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GroupSessionsLineChart(selectedGroup, dailyStudyHistory, studySessions, groupMembers)
        }
    }

}

