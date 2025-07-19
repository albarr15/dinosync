package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class DailyStudyHistory(
    val date: String = "",
    val hasStudied: Boolean = false,
    val moodEntryId: String = "",
    val totalGroupStudyMinutes: Float = 0f,
    val totalIndividualMinutes: Float = 0f,
    val userId: String = ""
){
    constructor() : this("", false ,"", 0f, 0f)
}

