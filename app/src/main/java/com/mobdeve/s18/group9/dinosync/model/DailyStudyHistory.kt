package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp
import com.mobdeve.s18.group9.dinosync.model.Companion.DinoType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyStudyHistory(
    val date: String = "",
    val hasStudied: Boolean = false,
    val moodEntryId: String = "",
    val totalGroupStudyMinutes: Float = 0f,
    val totalIndividualMinutes: Float = 0f,
    val userId: String = ""
){
    constructor() : this("", false ,"", 0f, 0f)

    val parsedDate: Date?
        get() = try {
            dateFormatter.parse(date)
        } catch (e: Exception) {
            null
        }

    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}

