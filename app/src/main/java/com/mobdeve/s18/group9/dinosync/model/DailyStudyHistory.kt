package com.mobdeve.s18.group9.dinosync.model
import java.util.Date

data class DailyStudyHistory(
    val dayId: Int = 0,
    val userId: Int,
    val date: Date,
    val hasStudied: Boolean,
    val totalStudyHours: Int,
    val moodEntryId: Int?
)

