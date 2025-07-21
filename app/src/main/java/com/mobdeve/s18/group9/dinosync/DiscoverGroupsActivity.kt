
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
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
import androidx.compose.runtime.*
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.viewmodel.GroupMemberViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.StudyGroupViewModel

class DiscoverGroupsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)

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

fun DiscoverGroupsScreen(userId: Int) {
    val context = LocalContext.current

    val studyGroupViewModel = remember { StudyGroupViewModel() }
    val memberViewModel = remember { GroupMemberViewModel() }

    val studyGroups by studyGroupViewModel.studyGroups.collectAsState()
    val groupMembers by memberViewModel.members.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

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


            val userGroups = remember(studyGroups, groupMembers) {
                studyGroups.filter { group ->
                    groupMembers.any { it.groupId == group.groupId && it.userId == userId.toString() }
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(userGroups.take(5)) { group ->
                    val imageResId = remember(group.image) {
                        context.resources.getIdentifier(group.image, "drawable", context.packageName)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
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
                    DiscoverGroupItem(group = group, members = members,
                        onGroupClick = {
                            val intent = Intent(context, GroupActivity::class.java)
                            intent.putExtra("groupId", group.groupId)
                            intent.putExtra("userId", userId)
                            context.startActivity(intent)
                    })
                }
            }
        }
    }
}

@Composable
fun DiscoverGroupItem(group: StudyGroup, members: Int, onGroupClick: () -> Unit) {
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
            verticalAlignment = Alignment.CenterVertically
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
        }
    }
}
