package com.mobdeve.s18.group9.dinosync.model

data class DailyStudyHistory(
    val date: String = "",
    val hasStudied: Boolean = false,
    val moodEntryId: String = "",
    val totalGroupStudyHours: Int = 0,
    val totalIndividualHours: Int = 0,
    val userId: String = ""
)

