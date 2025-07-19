package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class DailyStudyHistory(
    val date: String = "",
    val hasStudied: Boolean = false,
    val moodEntryId: String = "",
    val totalGroupStudyMinutes: Long = 0,
    val totalIndividualMinutes: Long = 0,
    val userId: String = ""
)

