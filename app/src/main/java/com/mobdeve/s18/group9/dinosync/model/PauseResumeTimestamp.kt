package com.mobdeve.s18.group9.dinosync.model

import com.google.firebase.Timestamp

data class PauseResumeTimestamp(
    val pausedAt: Timestamp = Timestamp.now(),
    val resumedAt: Timestamp? = null
)
