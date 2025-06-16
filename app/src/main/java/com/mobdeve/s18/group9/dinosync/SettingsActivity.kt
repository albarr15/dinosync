package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyWhite
import com.mobdeve.s18.group9.dinosync.ui.theme.Lime

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)

        setContent {
            DinoSyncTheme {
                SettingsActivityScreen(/*userId = userId*/)
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

@Preview
@Composable
fun SettingsActivityScreen() {
    val context = LocalContext.current
    val userId = initializeUsers().random().userId
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = null,
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
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(top = 25.dp, start = 25.dp, end = 25.dp, bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TopActionBar(
                        onProfileClick = {
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("userId", userId)
                            context.startActivity(intent)
                        },
                        onNotificationsClick = { /* Show notifications */ },
                        onSettingsClick = {
                            val intent = Intent(context, SettingsActivity::class.java)
                            intent.putExtra("userId", userId)
                            context.startActivity(intent)
                        }
                    )
                    //Settings
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Appearance Section
                    SettingsSection(title = "Appearance") {
                        SettingRow("Enable Dark Mode", Icons.Outlined.DarkMode ,hasSwitch = true)
                        SettingRow("Font size preferences", Icons.Outlined.TextFields)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account & Profile Section
                    SettingsSection(title = "Account & Profile") {
                        SettingRow("Edit Profile", Icons.Outlined.ManageAccounts)
                        SettingRow("Spotify / YouTube connection", Icons.Outlined.Person)
                        SettingRow("Profile Visibility", Icons.Outlined.Person)
                        SettingRow("Change Password", Icons.Outlined.Lock)
                        SettingRow("Change Email", Icons.Outlined.Mail)
                        SettingRow("Delete Account", Icons.Outlined.Delete)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer & Focus Section
                    SettingsSection(title = "Timer & Focus") {
                        SettingRow("Default timer durations", Icons.Outlined.Timer)
                        SettingRow("Break reminders", Icons.Outlined.Notifications)
                        SettingRow("Lock apps on Focus", Icons.Outlined.FrontHand, true)
                        SettingRow("App blocking whitelist", Icons.Outlined.FrontHand)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About This App Section
                    SettingsSection(title = "About This App") {
                        SettingRow("App Info")
                        SettingRow("Privacy Policy")
                        SettingRow("Credits")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DirtyWhite)
            ) {
                content()
            }

        }
    }
}

@Composable
fun SettingRow(label: String, icon: ImageVector? = null, hasSwitch: Boolean = false) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.drawBehind {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
            }.fillMaxWidth()
            .height(56.dp)
            .clickable { /* Handle item click */ }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            icon?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).padding(end = 5.dp),
                    tint = Color.DarkGray,

                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }

        if (hasSwitch) {
            Switch(modifier = Modifier.padding(end = 5.dp),
                checked = checked, onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = DarkGreen,
                    uncheckedThumbColor = Color.DarkGray,
                    uncheckedTrackColor = DirtyWhite,
                ))
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}



