package com.mobdeve.s18.group9.dinosync.model

data class StudySession(
    val courseId: String? = "",
    val endedAt: String = "",
    val hourSet: Int = 0,
    val minuteSet: Int = 0,
    val sessionDate: String = "",
    val startedAt: String = "",
    val status: String = "ongoing", // ongoing, pause, reset, completed
    val userId: String = ""
)


