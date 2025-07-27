
package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.GreenGray
import com.mobdeve.s18.group9.dinosync.viewmodel.AuthViewModel
import com.mobdeve.s18.group9.dinosync.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user!")

        setContent {
            DinoSyncTheme {
                DinoSyncTheme {
                    SettingsActivityScreen(userId = userId)
                }
            }
        }
    }
    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("SettingsActivity onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("SettingsActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("SettingsActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("SettingsActivity onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("SettingsActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("SettingsActivity onDestroy()")
    }
}

@Composable
fun SettingsActivityScreen(userId: String) {
    val context = LocalContext.current

    val userVM: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val authVM: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    LaunchedEffect(userId) {
        userVM.loadUser(userId)
    }

    // Modal states
    var showEditProfileModal by remember { mutableStateOf(false) }
    var showAppInfoModal by remember { mutableStateOf(false) }
    var showCreditsModal by remember { mutableStateOf(false) }
    var showLogoutModal by remember { mutableStateOf(false) }

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

                    // Account Section
                    SettingsSection(title = "Account") {
                        SettingRow("Edit Profile", Icons.Outlined.ManageAccounts,
                            onClick = { showEditProfileModal = true })
                        SettingRow("Logout", Icons.AutoMirrored.Outlined.Logout,
                            onClick = { showLogoutModal = true })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App Section
                    SettingsSection(title = "App") {
                        SettingRow("App Info", onClick = {showAppInfoModal = true} )
                        SettingRow("Credits", onClick = {showCreditsModal = true} )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showEditProfileModal) {
        EditProfileModal(onDismiss = { showEditProfileModal = false }, userVM)
    }

    if (showLogoutModal) {
        LogoutModal(onDismiss = { showLogoutModal = false }, authVM)
    }

    if (showAppInfoModal) {
        AppInfoModal(onDismiss = { showAppInfoModal = false })
    }

    if (showCreditsModal) {
        CreditsModal(onDismiss = { showCreditsModal = false })
    }
}

@Composable
fun EditProfileModal(onDismiss: () -> Unit,
                     userVM: UserViewModel) {
    val user by userVM.user.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var userName by remember(user) { mutableStateOf(user?.userName ?: "") }
    var userBio by remember(user) { mutableStateOf(user?.userBio ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        containerColor = Color.White,
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("@", color = Color.Gray) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userBio,
                    onValueChange = { userBio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    supportingText = { Text("${userBio.length}/150") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (userName.isBlank() || userBio.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }

                    if (userBio.length > 150) {
                        Toast.makeText(
                            context,
                            "Bio must be 150 characters or less",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }

                    coroutineScope.launch {
                        val result = userVM.updateUser(userName, userBio)
                        result
                            .onSuccess {
                                Toast.makeText(
                                    context,
                                    "Succesfully updated profile!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDismiss()
                            }
                            .onFailure { e ->
                                Toast.makeText(
                                    context,
                                    e.message ?: "Failed to update profile",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("EditProfileModal", "Error: ${e.message}", e)
                            }
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LogoutModal(onDismiss: () -> Unit, authVM: AuthViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        containerColor = Color.White,
        text = {
            Column {
                Text(
                    text = "Are you sure you want to logout?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        authVM.logout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        onDismiss()
                        // Navigate to login screen and clear back stack
                        val intent = Intent(context, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                }, colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )) {
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
                    Text("20250728")
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
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )) {
                Text("OK")
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
                    "• Albarracin, Clarissa\n• Garcia, Reina Althea\n• Santos, Miko",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Special Thanks",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Sir Oliver Berris",
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
                    modifier = Modifier.padding(bottom = 8.dp)
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
                            "• Gstudioimagen / Freepik\n" +
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
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DarkGreen
                )) {
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
