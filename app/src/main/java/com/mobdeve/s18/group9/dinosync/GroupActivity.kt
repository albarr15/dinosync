
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.DailyStudyHistory
import com.mobdeve.s18.group9.dinosync.model.GroupMember
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.mobdeve.s18.group9.dinosync.model.User
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.LightGray
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.DailyStudyHistoryViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.GroupMemberViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudyGroupViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudySessionViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.UserViewModel
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


        setContent {
            val allUsers by userVM.allUsers.collectAsState()
            val allGroups by groupVM.studyGroups.collectAsState()
            val allMembers by memberVM.members.collectAsState()
            val allHistory by historyVM.dailyHistory.collectAsState()
            val studySessionList = studySessionVM.studySessions.collectAsState()

            LaunchedEffect(Unit) {
                userVM.loadAllUsers()
                groupVM.loadStudyGroups()
                memberVM.loadAllMembers()
                historyVM.loadDailyHistory(userId)
                studySessionVM.loadStudySessions(userId)
            }

            val selectedGroup = allGroups.find { it.groupId == groupId }
            val groupMembers = allMembers.filter { it.groupId == groupId }
            val groupHistory = allHistory.filter { member ->
                groupMembers.any { it.userId == member.userId }
            }

            DinoSyncTheme {
                GroupActivityScreen(
                    userId = userId,
                    group = selectedGroup,
                    groupMembers = groupMembers,
                    allUsers = allUsers,
                    dailyStudyHistory = groupHistory,
                    studySessions = studySessionList.value
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
    group: StudyGroup?,
    groupMembers: List<GroupMember>,
    allUsers: List<User>,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
) {

    Log.d("GroupActivityScreen", "userId: $userId")
    Log.d("GroupActivityScreen", "group: ${group?.groupId}, name: ${group?.name}")

    Log.d("GroupActivityScreen", "groupMembers size: ${groupMembers.size}")
    groupMembers.forEachIndexed { index, gm ->
        Log.d("GroupActivityScreen", "groupMember[$index] -> userId: ${gm.userId}, groupId: ${gm.groupId}, minutes: ${gm.currentGroupStudyMinutes}")
    }

    Log.d("GroupActivityScreen", "allUsers size: ${allUsers.size}")
    allUsers.forEachIndexed { index, user ->
        Log.d("GroupActivityScreen", "user[$index] -> userId: ${user.userId}, name: ${user.userName}")
    }


    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Group Activity") }

    val memberUsers = groupMembers
        .filter { it.groupId == group?.groupId }
        .mapNotNull { gm ->
            val user = allUsers.find { it.userId == gm.userId }
            user?.let { user -> user to gm }
        }


    Log.d("GroupActivityScreen", "memberUsers size: ${memberUsers.size}")
    memberUsers.forEachIndexed { index, (user, gm) ->
        Log.d("GroupActivityScreen", "memberUser[$index] -> userId: ${user.userId}, name: ${user.userName}, minutes: ${gm.currentGroupStudyMinutes}")
    }

    val topMembers = memberUsers.sortedByDescending { it.second.currentGroupStudyMinutes }.map { it.first }.take(3)
    Log.d("GroupActivityScreen", "topMembers size: ${topMembers.size}")
    topMembers.forEachIndexed { index, user ->
        Log.d("GroupActivityScreen", "topMember[$index] -> userId: ${user.userId}, name: ${user.userName}")
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
                            text = group?.name ?: "Sample group name",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = group?.bio ?: "Sample bio",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rank ${group?.rank} out of 5",
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
                        group?.groupId.toString(),
                        dailyStudyHistory,
                        studySessions)
                }
            }
        }
    }
}


@Composable
fun OnClickGroupActivityBtn(memberUsers: List<Pair<User, GroupMember>>) {
    // Log for printing values of memberUsers
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
    selectedGroup: String,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
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
                            painter = rememberAsyncImagePainter(user.userProfileImage),
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
        //GroupSessionsLineChart(selectedGroup, dailyStudyHistory, studySessions)
    }
}

