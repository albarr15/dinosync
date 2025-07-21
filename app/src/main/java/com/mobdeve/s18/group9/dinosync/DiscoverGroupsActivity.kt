
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.GroupMemberViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudyGroupViewModel
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toSize
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen
import com.mobdeve.s18.group9.dinosync.viewmodel.UniversityViewModel

class DiscoverGroupsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId") ?: "-1"
        Log.d("CurrentUser", "Logged in userId in DiscoverGroupsActivity = $userId")

        setContent {
            DinoSyncTheme {
                DiscoverGroupsScreen(userId)
            }
        }
    }

    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() { super.onStart(); println("DiscoverGroupsActivity onStart()") }
    override fun onResume() { super.onResume(); println("DiscoverGroupsActivity onResume()") }
    override fun onPause() { super.onPause(); println("DiscoverGroupsActivity onPause()") }
    override fun onStop() { super.onStop(); println("DiscoverGroupsActivity onStop()") }
    override fun onRestart() { super.onRestart(); println("DiscoverGroupsActivity onRestart()") }
    override fun onDestroy() { super.onDestroy(); println("DiscoverGroupsActivity onDestroy()") }
}


@Composable
fun DiscoverGroupsScreen(userId: String) {
    val context = LocalContext.current

    val studyGroupViewModel = remember { StudyGroupViewModel() }
    val memberViewModel = remember { GroupMemberViewModel() }

    val studyGroups by studyGroupViewModel.studyGroups.collectAsState()
    val groupMembers by memberViewModel.members.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        studyGroupViewModel.loadStudyGroups()
        memberViewModel.loadAllMembers()
    }

    val filteredGroups by remember {
        derivedStateOf {
            if (searchQuery.isBlank()) studyGroups
            else studyGroups.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "Groups",
                onGroupsClick = {},
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
        },
        containerColor = Color.White
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = YellowGreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(30.dp, 40.dp)

            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        }

        if (showDialog) {
            CreateStudyGroupDialog(
                hostId = userId,
                onDismiss = { showDialog = false },
                onCreate = { hostId, groupName, bio, university ->
                    studyGroupViewModel.createGroup(hostId, groupName, bio, university)
                    showDialog = false
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp, bottom = 5.dp)
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
                text = "Groups",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            /** Groups **/
            Text(
                text = "Your Groups",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))


            val userGroupIds = remember(groupMembers) {
                groupMembers
                    .filter { it.userId.trim() == userId.toString().trim() }
                    .map { it.groupId }
                    .toSet()
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(studyGroups.filter { it.groupId in userGroupIds }.take(5)) { group ->
                    val imageResId = remember(group.image) {
                        context.resources.getIdentifier(group.image, "drawable", context.packageName)
                    }
                    val isMember = userGroupIds.contains(group.groupId)

                    LaunchedEffect(group.groupId, userGroupIds) {
                        Log.d(
                            "DiscoverGroups",
                            "GroupId=${group.groupId}, GroupName=${group.name}, isMember=$isMember, userGroupIds=$userGroupIds"
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center)
                            )

                            if (isMember) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Joined",
                                    tint = DarkGreen,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .padding(15.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(group.name, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            /** Discover Groups **/
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Discover Groups",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    fontSize = 23.sp
                )

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search") },
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(filteredGroups) { group ->
                    val members = groupMembers.count { it.groupId == group.groupId }
                    val isMember = groupMembers.any { it.groupId == group.groupId && it.userId == userId }
                    val isHost = group.hostId == userId
                    DiscoverGroupItem(
                        group = group,
                        members = members,
                        isMember = isMember,
                        onGroupClick = {
                            val intent = Intent(context, GroupActivity::class.java)
                            intent.putExtra("groupId", group.groupId)
                            intent.putExtra("userId", userId)
                            context.startActivity(intent)
                        },
                        onDeleteClick = if (isHost) {
                            {
                                studyGroupViewModel.deleteGroup(group.groupId)
                            }
                        } else null
                    )
                }
                }
            }

            LaunchedEffect(groupMembers) {
                groupMembers.forEach {
                    Log.d("CheckGroupMembers", "UserId=${it.userId}, GroupId=${it.groupId}")
                }
            }
            Log.d("CurrentUser", "Logged in userId in DiscoverGroupsScreen= $userId")

        }
    }


@Composable
fun DiscoverGroupItem(
    group: StudyGroup,
    members: Int,
    isMember: Boolean,
    onGroupClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onGroupClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageResId = remember(group.image) {
                    context.resources.getIdentifier(group.image, "drawable", context.packageName)
                }
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(group.bio, fontSize = 12.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(members) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9CCC65))
                                .padding(2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text("$members Members", fontSize = 12.sp)
                }
            }

            // JOIN / JOINED badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(end = if (onDeleteClick != null) 4.dp else 0.dp)
                ) {
                    Text(
                        text = if (isMember) "JOINED" else "JOIN",
                        color = if (isMember) DarkGreen else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(
                                color = if (isMember) Color.Transparent else DarkGreen,
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = if (!isMember) 1.dp else 0.dp,
                                color = DarkGreen,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Delete icon
                onDeleteClick?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier
                            .align(Alignment.Top)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Group",
                            tint = Color.Gray
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun CreateStudyGroupDialog(
    hostId: String,
    onDismiss: () -> Unit,
    onCreate: (hostId: String, name: String, bio: String, university: String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val universityViewModel  = remember { UniversityViewModel() }
    val universityOptions by universityViewModel.universityList



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Study Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        label = { Text("University") },
                        trailingIcon = {
                            Icon(icon, contentDescription = null,
                                Modifier.clickable { expanded = !expanded })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    ) {
                        universityOptions.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    university = option
                                    expanded = false
                                },
                                text = { Text(option) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreate(hostId, groupName.trim(), description.trim(), university.trim())
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}