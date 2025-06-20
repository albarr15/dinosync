package com.mobdeve.s18.group9.dinosync
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.zIndex
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeDailyStudyHistory
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeGroupMembers
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeStudyGroups
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeStudySessions
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.LightGray
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import kotlin.collections.take

import com.mobdeve.s18.group9.dinosync.model.User
import com.mobdeve.s18.group9.dinosync.model.DailyStudyHistory
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.model.GroupMember

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import androidx.compose.runtime.LaunchedEffect
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeAchievements
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.StudySession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class GroupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)
        val groupId = intent.getIntExtra("groupId", -1)

        val userList = initializeUsers()
        val groupList = initializeStudyGroups()
        val groupMemberList = initializeGroupMembers()
        val studySessionList = initializeStudySessions()
        val dailyStudyHistoryList = initializeDailyStudyHistory()

        // Find selected group
        val selectedGroup = groupList.find { it.groupId == groupId }

        if (selectedGroup == null) {
            // If groupId is invalid, exit gracefully
            finish()
            return
        }

        // Get members of this group
        val groupMembers = groupMemberList.filter { it.groupId == selectedGroup.groupId }

        // Filter study history for group members
        val dailyStudyHistory = dailyStudyHistoryList.filter { session ->
            groupMembers.any { it.userId == session.userId }
        }

        setContent {
            DinoSyncTheme {
                GroupActivityScreen(
                    userId = userId,
                    group = selectedGroup,
                    groupMembers = groupMembers,
                    allUsers = userList,
                    dailyStudyHistory = dailyStudyHistory
                )
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
fun GroupActivityScreen(
    userId: Int,
    group: StudyGroup,
    groupMembers: List<GroupMember>,
    allUsers: List<User>,
    dailyStudyHistory: List<DailyStudyHistory>) {

    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Group Activity") }

    val studySessionList = initializeStudySessions()


    // Merge GroupMember with User
    val memberUsers = groupMembers.mapNotNull { gm ->
        val user = allUsers.find { it.userId == gm.userId }
        user?.let { user -> user to gm }
    }

    val topMembers = memberUsers.sortedByDescending { it.second.currentGroupStudyMinutes }
        .map { it.first }
        .take(3)

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
        }
    ) { padding ->

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
                    context.startActivity(intent)},
                onSettingsClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2F6437))
                    .padding(20.dp)
            ) {

                Column {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .height(25.dp)
                            .align(Alignment.End)
                            .background(YellowGreen)
                    ){
                        Text(
                            text = "JOIN",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = group.name,
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                    }
                    Text(
                        text = group.bio,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rank ${group.rank} out of 5",
                            color = Color.White,
                            fontStyle = FontStyle.Italic,
                            fontFamily = fontFamily
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
                                        painter = painterResource(user.userProfileImage),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedTab = "Group Activity" },
                    colors = ButtonDefaults.buttonColors(if (selectedTab == "Group Activity") YellowGreen else LightGray),
                    modifier = Modifier
                        .width(170.dp)
                        .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                    shape = RectangleShape
                ) {
                    Text("Group Activity", color = Color.Black, fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)
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
                    Text("Stats", color = Color.Black, fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    "Group Activity" -> OnClickGroupActivityBtn(memberUsers)
                    "Stats" -> OnClickGroupStatsActivityBtn(
                        topMembers = ArrayList(topMembers),
                        dailyStudyHistory = ArrayList(dailyStudyHistory),
                        studySessionList = ArrayList(studySessionList),
                        targetGroupId = group.groupId,
                        modifier = Modifier)
                }
            }
        }
    }
}


@Composable
fun OnClickGroupActivityBtn(memberUsers: List<Pair<User, GroupMember>>) {
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
                UserCard(user, groupMember)
            }
        }
    }
}


@Composable
fun UserCard(user: User, groupMember: GroupMember) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when {
                groupMember.isOnBreak -> R.drawable.inactive
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

        val studyText = when {
            groupMember.isOnBreak -> "On Break"
            groupMember.currentGroupStudyMinutes >= 240 -> "≥ 4 hours"
            groupMember.currentGroupStudyMinutes >= 60 -> "≥ 1 hour"
            else -> "< 1 hour"
        }

        Text(studyText, fontSize = 12.sp)
    }
}

@Composable
fun OnClickGroupStatsActivityBtn(
    topMembers: ArrayList<User>,
    dailyStudyHistory: ArrayList<DailyStudyHistory>,
    studySessionList: ArrayList<StudySession>,
    targetGroupId: Int,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Day") }

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
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            topMembers.take(3).forEach { user ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(user.userProfileImage),
                            contentDescription = user.userName,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                        )
                    }
                    Text(user.userName, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sessions Section
        Text(
            text = "Sessions",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Tab Row (Day, Week, Month, Year)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Day", "Week", "Month", "Year").forEach { label ->
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = if (selectedTab == label) Color.Black else Color.Gray,
                    modifier = Modifier
                        .clickable { selectedTab = label }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEFEFEF)),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                "Day" -> GroupStudyDayLineChart(dailyStudyHistory, studySessionList, targetGroupId)
                "Week" -> GroupStudyWeeklyLineChart(dailyStudyHistory, studySessionList, targetGroupId)
                "Month" -> GroupStudyMonthlyLineChart(dailyStudyHistory, studySessionList, targetGroupId)
                "Year" -> GroupStudyYearlyLineChart(dailyStudyHistory, studySessionList, targetGroupId)
            }
        }
    }
}

@Composable
fun GroupStudyDayLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    targetGroupId: Int
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(targetGroupId, dailyStudyHistory, studySessions) {
        val filteredSessions = studySessions.filter { session ->
            session.groupId == targetGroupId && session.status == "done"
        }
        val todayDate = Date()
        val chartData = aggregateHourlyChartData(filteredSessions, todayDate, targetGroupId)
        val fullSeries = (1..24).map { hour -> chartData[hour] ?: 0 }
        modelProducer.runTransaction {
            lineSeries { series(fullSeries) }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    )
}

// Filtering Logic
fun filterDailyHistoryByGroup(
    targetGroupId: Int,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
): List<DailyStudyHistory> {
    return dailyStudyHistory.filter { history ->
        studySessions.any { session ->
            session.userId == history.userId &&
                    session.groupId == targetGroupId &&
                    session.status == "done"
        }
    }
}

// Aggregation Logic
fun aggregateHourlyChartData(
    sessions: List<StudySession>,
    targetDate: Date,
    targetGroupId: Int
): Map<Int, Int> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val targetDateStr = formatter.format(targetDate)

    val hourlyMap = mutableMapOf<Int, Int>()

    sessions.filter { session ->
        session.groupId == targetGroupId &&
                session.status == "done" &&
                session.sessionDate == targetDateStr
    }.forEach { session ->
        val startHour = 12
        val totalMinutes = session.hourSet * 60 + session.minuteSet
        hourlyMap[startHour] = (hourlyMap[startHour] ?: 0) + totalMinutes
    }
    return hourlyMap.toSortedMap()
}

@Composable
fun GroupStudyWeeklyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    targetGroupId: Int
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, targetGroupId) {
        val filteredHistory = filterDailyHistoryByGroup(
            targetGroupId,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val weeklyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.date
                val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)
                year * 100 + weekOfYear
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalStudyHours }
            }
            .toSortedMap()

        val chartData = weeklyData.values.map { it.toFloat() }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun GroupStudyMonthlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    targetGroupId: Int
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, targetGroupId) {
        val filteredHistory = filterDailyHistoryByGroup(
            targetGroupId,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val monthlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.date
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                year * 100 + month
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalStudyHours }
            }
            .toSortedMap()

        val chartData = monthlyData.values.map { it.toFloat() }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GroupStudyYearlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    targetGroupId: Int
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, targetGroupId) {
        val filteredHistory = filterDailyHistoryByGroup(
            targetGroupId,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val yearlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.date
                calendar.get(Calendar.YEAR)
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalStudyHours }
            }
            .toSortedMap()

        val chartData = yearlyData.values.map { it.toFloat() }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}








