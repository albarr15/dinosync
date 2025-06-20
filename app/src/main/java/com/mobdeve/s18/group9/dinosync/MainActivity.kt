package com.mobdeve.s18.group9.dinosync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.TextField
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeCourses
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeMusic
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeTodo
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.DataHelper.Companion.initializeUsers
import com.mobdeve.s18.group9.dinosync.components.AudioPlayerCard
import com.mobdeve.s18.group9.dinosync.components.TopActionBar
import com.mobdeve.s18.group9.dinosync.model.Music
import com.mobdeve.s18.group9.dinosync.model.TodoItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import com.mobdeve.s18.group9.dinosync.model.Course

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DinoSyncTheme {
                val context = LocalContext.current
                MainScreen(context = context)
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
/******** MAIN SCREEN *********/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(context : Context) {
    var isRunning by remember { mutableStateOf(false) }
    var showMoodDialog by remember { mutableStateOf(false) }
    var hoursSet by remember { mutableStateOf("") }
    var minutesSet by remember { mutableStateOf("") }
    val context = LocalContext.current
    var userList = initializeUsers()
    val selectedUser = userList.random()
    var todoItems by remember { mutableStateOf( ArrayList(initializeTodo()) ) }
    var selectedMoodIcon by remember { mutableStateOf<ImageVector?>(null) }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = null,
                onGroupsClick = {
                    val intent = Intent(context, DiscoverGroupsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                },
                onHomeClick = {
                    val intent = Intent(context, RegisterActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                },
                onStatsClick = {
                    val intent = Intent(context, StatisticsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
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
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent)
                                 },
                onSettingsClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    intent.putExtra("userId", selectedUser.userId)
                    context.startActivity(intent) }
            )

            /******** Course Box *********/
            val courseList = initializeCourses()
            val subjects = courseList.map { it.name }
            var expanded by remember { mutableStateOf(false) }
            var selectedSubject by remember { mutableStateOf("") }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .width(250.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Subject") },
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
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject) },
                            onClick = {
                                selectedSubject = subject
                                expanded = false
                            }
                        )
                    }
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
                    isRunning = false
                    hoursSet = ""
                    minutesSet = ""
                },
                onStartClicked = { showMoodDialog = true }
            )

            if (showMoodDialog) {
                AlertDialog(
                    onDismissRequest = { showMoodDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showMoodDialog = false
                            val hourInt = hoursSet.toIntOrNull() ?: 0
                            val minuteInt = minutesSet.toIntOrNull() ?: 0
                            if (hourInt == 0 && minuteInt == 0) return@Button

                            val intent = Intent(context, FocusStudyActivity::class.java).apply {
                                putExtra("userId", selectedUser.userId)
                                putExtra("hours", hourInt)
                                putExtra("minutes", minuteInt)
                                putExtra("selected_subject", selectedSubject)
                            }
                            context.startActivity(intent)
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkGreen,
                                contentColor = Color.Black
                            )) {
                            Text("Log", color = Color.White)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = {
                            showMoodDialog = false
                            val hourInt = hoursSet.toIntOrNull() ?: 0
                            val minuteInt = minutesSet.toIntOrNull() ?: 0
                            if (hourInt == 0 && minuteInt == 0) return@OutlinedButton

                            val intent = Intent(context, FocusStudyActivity::class.java).apply {
                                putExtra("userId", selectedUser.userId)
                                putExtra("hours", hourInt)
                                putExtra("minutes", minuteInt)
                                putExtra("selected_subject", selectedSubject)
                            }
                            context.startActivity(intent)
                        }) {
                            Text("Skip")
                        }
                    },
                    text = {
                        MoodInput(
                            selectedIcon = selectedMoodIcon,
                            onIconSelected = { selectedMoodIcon = it }
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))


            /******** TodoList *********/

            TodoList(
                todoItems = todoItems,
                onItemsChange = { updatedList -> todoItems = ArrayList(updatedList) },
                onAddItem = {
                    val nextId = (todoItems.maxOfOrNull { it.id } ?: 0) + 1
                    val updated = ArrayList(todoItems)
                    updated.add(TodoItem(id = nextId, title = "New Task"))
                    todoItems = updated
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            /******** Music Activity *********/
            val currentMusic = initializeMusic().random()
            AudioPlayerCard(
                currentMusic = currentMusic,
                progress = 0.5f,
                onShuffle = { },
                onPrevious = { /* previous logic */ },
                onPlayPause = { /* play pause logic */ },
                onNext = { /* next logic */ },
                onRepeat = { /* repeat logic */ }
            )


        }
    }
}

@Composable
fun MoodInput(
    selectedIcon: ImageVector?,
    onIconSelected: (ImageVector?) -> Unit
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
                val icons = listOf(
                    Icons.Filled.SentimentVeryDissatisfied,
                    Icons.Filled.SentimentDissatisfied,
                    Icons.Filled.SentimentNeutral,
                    Icons.Filled.SentimentSatisfied,
                    Icons.Filled.SentimentVerySatisfied
                )

                icons.forEach { icon ->
                    val isSelected = selectedIcon == icon
                    Box(
                        modifier = Modifier
                            .size(20.dp).scale(1.5f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(if (isSelected) YellowGreen else Color.LightGray)
                            .clickable {
                                onIconSelected(if (isSelected) null else icon)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
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
    todoItems: List<TodoItem>,
    onItemsChange: (List<TodoItem>) -> Unit,
    onAddItem: () -> Unit
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
                                    val newId = if (todoItems.isEmpty()) 1 else (todoItems.maxOf { it.id } + 1)
                                    onItemsChange(todoItems + TodoItem(id = newId, title = newItemTitle))
                                    newItemTitle = ""
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }

                    itemsIndexed(todoItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            OutlinedTextField(
                                value = item.title,
                                onValueChange = {
                                    val newList = todoItems.toMutableList()
                                    newList[index] = newList[index].copy(title = it)
                                    onItemsChange(newList)
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
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                val newList = todoItems.toMutableList()
                                newList.removeAt(index)
                                onItemsChange(newList)
                            }) {
                                Icon(
                                    imageVector = if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
