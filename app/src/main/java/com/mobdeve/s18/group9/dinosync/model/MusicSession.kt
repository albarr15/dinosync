package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class MusicSession (
    val artist: String = "",
    val endTime: Timestamp? = null,
    val musicPlatform: String = "",
    val musicTitle: String = "",
    val musicUri: String = "",
    val studySessionId: String = "",
    val startTime: Timestamp? = null,
    val userId: String = "",
)