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
import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyWhite
import com.mobdeve.s18.group9.dinosync.ui.theme.GreenGray
import com.mobdeve.s18.group9.dinosync.ui.theme.LightGray
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen

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

    // Modal states
    var showProfileVisibilityModal by remember { mutableStateOf(false) }
    var showChangePasswordModal by remember { mutableStateOf(false) }
    var showChangeEmailModal by remember { mutableStateOf(false) }
    var showDeleteAccountModal by remember { mutableStateOf(false) }
    var showTimerDurationModal by remember { mutableStateOf(false) }
    var showBreakRemindersModal by remember { mutableStateOf(false) }
    var showEditProfileModal by remember { mutableStateOf(false) }
    var showSpotifyYouTubeModal by remember { mutableStateOf(false) }
    var showAppBlockingModal by remember { mutableStateOf(false) }
    var showAppInfoModal by remember { mutableStateOf(false) }
    var showPrivacyPolicyModal by remember { mutableStateOf(false) }
    var showCreditsModal by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(bottom = 5.dp),
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
                        SettingRow("Enable Dark Mode", Icons.Outlined.DarkMode, hasSwitch = true)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account & Profile Section
                    SettingsSection(title = "Account & Profile") {
                        SettingRow("Edit Profile", Icons.Outlined.ManageAccounts,
                            onClick = { showEditProfileModal = true })
                        SettingRow("Spotify / YouTube connection", Icons.Outlined.Person,
                            onClick = {showSpotifyYouTubeModal = true} )
                        SettingRow(
                            "Profile Visibility",
                            Icons.Outlined.Person,
                            onClick = { showProfileVisibilityModal = true }
                        )
                        SettingRow(
                            "Change Password",
                            Icons.Outlined.Lock,
                            onClick = { showChangePasswordModal = true }
                        )
                        SettingRow(
                            "Change Email",
                            Icons.Outlined.Mail,
                            onClick = { showChangeEmailModal = true }
                        )
                        SettingRow(
                            "Delete Account",
                            Icons.Outlined.Delete,
                            onClick = { showDeleteAccountModal = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer & Focus Section
                    SettingsSection(title = "Timer & Focus") {
                        SettingRow(
                            "Default timer durations",
                            Icons.Outlined.Timer,
                            onClick = { showTimerDurationModal = true }
                        )
                        SettingRow(
                            "Break reminders",
                            Icons.Outlined.Notifications,
                            onClick = { showBreakRemindersModal = true }
                        )
                        SettingRow("Lock apps on Focus", Icons.Outlined.FrontHand, hasSwitch = true)
                        SettingRow("App blocking whitelist", Icons.Outlined.FrontHand,
                            onClick = { showAppBlockingModal = true } )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About This App Section
                    SettingsSection(title = "About This App") {
                        SettingRow("App Info", onClick = {showAppInfoModal = true} )
                        SettingRow("Privacy Policy", onClick = {showPrivacyPolicyModal = true} )
                        SettingRow("Credits", onClick = {showCreditsModal = true} )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showProfileVisibilityModal) {
        ProfileVisibilityModal(onDismiss = { showProfileVisibilityModal = false })
    }

    if (showChangePasswordModal) {
        ChangePasswordModal(onDismiss = { showChangePasswordModal = false })
    }

    if (showChangeEmailModal) {
        ChangeEmailModal(onDismiss = { showChangeEmailModal = false })
    }

    if (showDeleteAccountModal) {
        DeleteAccountModal(onDismiss = { showDeleteAccountModal = false })
    }

    if (showTimerDurationModal) {
        TimerDurationModal(onDismiss = { showTimerDurationModal = false })
    }

    if (showBreakRemindersModal) {
        BreakRemindersModal(onDismiss = { showBreakRemindersModal = false })
    }

    if (showEditProfileModal) {
        EditProfileModal(onDismiss = { showEditProfileModal = false })
    }

    if (showSpotifyYouTubeModal) {
        SpotifyYouTubeModal(onDismiss = { showSpotifyYouTubeModal = false })
    }

    if (showAppBlockingModal) {
        AppBlockingModal(onDismiss = { showAppBlockingModal = false })
    }

    if (showAppInfoModal) {
        AppInfoModal(onDismiss = { showAppInfoModal = false })
    }

    if (showPrivacyPolicyModal) {
        PrivacyPolicyModal(onDismiss = { showPrivacyPolicyModal = false })
    }

    if (showCreditsModal) {
        CreditsModal(onDismiss = { showCreditsModal = false })
    }
}


@Composable
fun ProfileVisibilityModal(onDismiss: () -> Unit) {
    var selectedVisibility by remember { mutableStateOf("Public") }
    val visibilityOptions = listOf("Public", "Friends Only", "Private")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profile Visibility") },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "Choose who can see your profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                visibilityOptions.forEach { visibility ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVisibility = visibility }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedVisibility == visibility,
                            onClick = { selectedVisibility = visibility }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(visibility, fontWeight = FontWeight.Medium)
                            Text(
                                when (visibility) {
                                    "Public" -> "Anyone can see your profile"
                                    "Friends Only" -> "Only your friends can see your profile"
                                    "Private" -> "Only you can see your profile"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChangePasswordModal(onDismiss: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPWVisible by remember { mutableStateOf(false) }
    var isPWVisible_new by remember { mutableStateOf(false) }
    var isPWVisible_confirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        containerColor = Color.White,
        text = {
            Column {
                OutlinedSecureTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Password") },
                    trailingIcon = @Composable {
                        IconButton(onClick = {
                            isPWVisible = !isPWVisible
                        }) {
                            if (isPWVisible) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "Show Password",
                                    tint = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = "Hide Password",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    textObfuscationMode = if (isPWVisible) {
                        TextObfuscationMode.Visible} else {TextObfuscationMode.RevealLastTyped},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedSecureTextField(
                    state = rememberTextFieldState(),
                    label = { Text("New Password") },
                    trailingIcon = @Composable {
                        IconButton(onClick = {
                            isPWVisible_new = !isPWVisible_new
                        }) {
                            if (isPWVisible_new) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "Show Password",
                                    tint = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = "Hide Password",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    textObfuscationMode = if (isPWVisible_new) {
                        TextObfuscationMode.Visible} else {TextObfuscationMode.RevealLastTyped},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedSecureTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Confirm Password") },
                    trailingIcon = @Composable {
                        IconButton(onClick = {
                            isPWVisible_confirm = !isPWVisible_confirm
                        }) {
                            if (isPWVisible_confirm) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "Show Password",
                                    tint = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = "Hide Password",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    textObfuscationMode = if (isPWVisible_confirm) {
                        TextObfuscationMode.Visible} else {TextObfuscationMode.RevealLastTyped},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        newPassword == confirmPassword
            ) {
                Text("Update Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChangeEmailModal(onDismiss: () -> Unit) {
    var currentEmail by remember { mutableStateOf("user@example.com") }
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Email Address") },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "Current Email: $currentEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("New Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = newEmail.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Update Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteAccountModal(onDismiss: () -> Unit) {
    var confirmText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Delete Account",
                color = MaterialTheme.colorScheme.error
            )
        },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "⚠️ This action cannot be undone!",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "All your data, including study sessions, groups, and statistics will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Type 'DELETE' to confirm:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    placeholder = { Text("DELETE") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = confirmText == "DELETE" && password.isNotEmpty(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimerDurationModal(onDismiss: () -> Unit) {
    var duration by remember { mutableStateOf("25") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Default Timer Durations") },
        containerColor = Color.White,
        text = {
            Column {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Timer Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BreakRemindersModal(onDismiss: () -> Unit) {
    var enableReminders by remember { mutableStateOf(true) }
    var reminderInterval by remember { mutableStateOf("30") }
    var reminderSound by remember { mutableStateOf(true) }
    var reminderVibration by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Break Reminders") },
        containerColor = Color.White,
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable break reminders")
                    Switch(
                        checked = enableReminders,
                        onCheckedChange = { enableReminders = it }
                    )
                }

                if (enableReminders) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = reminderInterval,
                        onValueChange = { reminderInterval = it },
                        label = { Text("Reminder interval (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound notification")
                        Switch(
                            checked = reminderSound,
                            onCheckedChange = { reminderSound = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration")
                        Switch(
                            checked = reminderVibration,
                            onCheckedChange = { reminderVibration = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditProfileModal(onDismiss: () -> Unit) {
    var displayName by remember { mutableStateOf("John Doe") }
    var username by remember { mutableStateOf("johndoe123") }
    var bio by remember { mutableStateOf("Focused student passionate about productivity") }
    var location by remember { mutableStateOf("Manila, Philippines") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        containerColor = Color.White,
        text = {
            Column {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("@", color = Color.Gray) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    supportingText = { Text("${bio.length}/150") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = displayName.isNotEmpty() && username.isNotEmpty()
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SpotifyYouTubeModal(onDismiss: () -> Unit) {
    var spotifyConnected by remember { mutableStateOf(false) }
    var youtubeConnected by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Music Service Connections") },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "Connect your music services to sync your listening activity during study sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Spotify Connection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (spotifyConnected) Color(0xFF1DB954) else Color.LightGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_spotify),
                                contentDescription = "Spotify",
                                modifier = Modifier.size(24.dp),
                                tint = if (spotifyConnected) Color.White else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Spotify",
                                    fontWeight = FontWeight.Bold,
                                    color = if (spotifyConnected) Color.White else Color.Black
                                )
                                Text(
                                    if (spotifyConnected) "Connected" else "Not connected",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (spotifyConnected) Color.White else Color.Gray
                                )
                            }
                        }
                        Button(
                            onClick = { spotifyConnected = !spotifyConnected },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (spotifyConnected) Color.White else Color(0xFF1DB954),
                                contentColor = if (spotifyConnected) Color(0xFF1DB954) else Color.White
                            )
                        ) {
                            Text(if (spotifyConnected) "Disconnect" else "Connect")
                        }
                    }
                }

                // YouTube Music Connection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (youtubeConnected) Color(0xFFFF0000) else Color.LightGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_youtube),
                                contentDescription = "YouTube Music",
                                modifier = Modifier.size(24.dp),
                                tint = if (youtubeConnected) Color.White else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "YouTube Music",
                                    fontWeight = FontWeight.Bold,
                                    color = if (youtubeConnected) Color.White else Color.Black
                                )
                                Text(
                                    if (youtubeConnected) "Connected" else "Not connected",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (youtubeConnected) Color.White else Color.Gray
                                )
                            }
                        }
                        Button(
                            onClick = { youtubeConnected = !youtubeConnected },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (youtubeConnected) Color.White else Color(0xFFFF0000),
                                contentColor = if (youtubeConnected) Color(0xFFFF0000) else Color.White
                            )
                        ) {
                            Text(if (youtubeConnected) "Disconnect" else "Connect")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun AppBlockingModal(onDismiss: () -> Unit) {
    var whitelistedApps by remember { mutableStateOf(listOf("Calculator", "Notes", "Dictionary")) }
    var newAppName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Blocking Whitelist") },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "Apps in this list will remain accessible during focus sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Add new app
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newAppName,
                        onValueChange = { newAppName = it },
                        label = { Text("Add app") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newAppName.isNotEmpty()) {
                                whitelistedApps = whitelistedApps + newAppName
                                newAppName = ""
                            }
                        },
                        enabled = newAppName.isNotEmpty()
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Whitelisted apps
                Text(
                    "Whitelisted Apps:",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                whitelistedApps.forEach { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app)
                            IconButton(
                                onClick = {
                                    whitelistedApps = whitelistedApps.filter { it != app }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AppInfoModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Information") },
        containerColor = Color.White,
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("App Name:", fontWeight = FontWeight.Medium)
                    Text("DinoSync")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Version:", fontWeight = FontWeight.Medium)
                    Text("1.0.0")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Build:", fontWeight = FontWeight.Medium)
                    Text("20250617")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Developer:", fontWeight = FontWeight.Medium)
                    Text("Group 9")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "DinoSync is a collaborative study timer app that helps you stay focused and motivated with friends. Track your study sessions, join groups, and achieve your academic goals together.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "For support, contact us at support@dinosync.app",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun PrivacyPolicyModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Privacy Policy") },
        containerColor = Color.White,
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Last updated: June 17, 2025",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Data Collection",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "We collect information you provide directly to us, such as when you create an account, join study groups, or contact us for support. This includes your name, email address, profile information, and study session data.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "How We Use Your Information",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "We use the information we collect to provide, maintain, and improve our services, including tracking your study progress, facilitating group study sessions, and sending you relevant notifications.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Data Security",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "We implement appropriate security measures to protect your personal information against unauthorized access, alteration, disclosure, or destruction.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CreditsModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Credits") },
        containerColor = Color.White,
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Development Team",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Albarracin, Clarissa M.\n• Garcia, Reina Althea\n• Santos, Miko",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Special Thanks",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Sir Oliver Berris\n• Beta testers and early users",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Third-Party Libraries",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Jetpack Compose - UI Toolkit\n• Material Design 3 - Design System\n" +
                            "• Compose Charts by ehsannarmani\n" +
                            "• Vico by patrykandpatrick\n",
                            style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Icons & Assets",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Material Design Icons by Google\n\nIllustrations designed by: \n" +
                            "• pch.vector / Freepik\n" +
                            "• pikisuperstar / Freepik\n" +
                            "• macrovector / Freepik\n" +
                            "• Orin zuu / the Noun Project\n" +
                            "• etika ariatna / the Noun Project\n",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "© 2025 DinoSync - Group 9\nMade with ❤️ in PH",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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
                    .background(GreenGray)
            ) {
                content()
            }

        }
    }
}

@Composable
fun SettingRow(label: String, icon: ImageVector? = null, hasSwitch: Boolean = false,
               onClick: (() -> Unit)? = null) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .fillMaxWidth()
            .height(56.dp)
            .clickable { if (!hasSwitch) onClick?.invoke() }
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
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 5.dp),
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
                    uncheckedTrackColor = GreenGray,
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