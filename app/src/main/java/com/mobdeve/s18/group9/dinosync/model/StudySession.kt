package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class StudySession(
    val courseId: String? = "",
    val endedAt: Timestamp? = null,
    val hourSet: Int = 0,
    val minuteSet: Int = 0,
    val moodId : String = "",
    val sessionDate: String = "",
    val startedAt: Timestamp? = null,
    val status: String = "active", // active, pause, reset, completed
    val userId: String = ""
)


