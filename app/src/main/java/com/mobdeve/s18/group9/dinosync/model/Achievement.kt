package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class Achievement(
    val dateAwarded: Timestamp? = null,
    val dinoId: String = "",
    val userId: String = "",
)

