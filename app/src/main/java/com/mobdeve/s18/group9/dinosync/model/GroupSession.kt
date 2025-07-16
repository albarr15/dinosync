package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class GroupSession(
    val endedAt: Timestamp? = null,
    val groupId: String = "",
    val hostId: String = "",
    val participants: List<String> = emptyList(),
    val status: String = "active",
    val startedAt: Timestamp? = null
)
