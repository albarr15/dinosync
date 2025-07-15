package com.mobdeve.s18.group9.dinosync.model

data class GroupSession(
    val endedAt: String? = "",
    val groupId: String = "",
    val hostId: String = "",
    val participants: List<String> = emptyList(),
    val status: String = "active",
)
