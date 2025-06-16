package com.mobdeve.s18.group9.dinosync.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.mobdeve.s18.group9.dinosync.GroupActivity
import com.mobdeve.s18.group9.dinosync.ProfileActivity
import com.mobdeve.s18.group9.dinosync.StatisticsActivity

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen

@Composable
fun BottomNavigationBar(selectedItem: String?,
                        onGroupsClick: () -> Unit,
                        onHomeClick: () -> Unit,
                        onStatsClick: () -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.LightGray)
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GROUPS
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val bgColor = if (selectedItem == "Groups") YellowGreen else Color.White
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable {
                            onGroupsClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = "Groups",
                        tint = Color.Black
                    )
                }
                Text("Groups", fontSize = 15.sp)
            }

            // HOME
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val bgColor = if (selectedItem == "Home") Color(0xFF9ACD32) else Color.White
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable {
                            onHomeClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = Color.Black
                    )
                }
                Text("Home", fontSize = 15.sp)
            }

            // STATS
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val bgColor = if (selectedItem == "Stats") Color(0xFF9ACD32) else Color.White
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable {
                            onStatsClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Stats",
                        tint = Color.Black
                    )
                }
                Text("Stats", fontSize = 15.sp)
            }
        }
    }
}



