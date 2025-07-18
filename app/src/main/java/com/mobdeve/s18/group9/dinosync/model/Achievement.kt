package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

// refers to time-based achievements (when user has total study hours of 1 hr, 3 hrs, 24 hrs,
// 168 hrs (week), 504 hrs (month)
data class Achievement(
    val dateAwarded: String = "",
    val userId: String = "",        // Connected to specific user
    val title: String = ""
)
