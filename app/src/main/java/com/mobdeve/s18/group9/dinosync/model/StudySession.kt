package com.mobdeve.s18.group9.dinosync.model

data class StudySession(
    val sessionId: Int = 0,
    val userId: Int,
    val groupId: Int?,
    val courseId: Int?,
    val hasJoinedGroup: Boolean,
    val hourSet: Int,
    val minuteSet: Int,
    val status: String,
    val sessionDate: String
)

