package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class DailyStudyHistory(
    val date: Timestamp? = null,
    val hasStudied: Boolean = false,
    val moodEntryId: String = "",
    val totalGroupStudyHours: Int = 0,
    val totalIndividualHours: Int = 0,
    val userId: String = ""
)

