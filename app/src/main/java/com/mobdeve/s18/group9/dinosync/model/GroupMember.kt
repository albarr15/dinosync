package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class GroupMember(
    val startedAt: String = "",
    val endedAt: String = "",
    val currentGroupStudyMinutes: Int = 0,
    val groupId:String = "",
    val isOnBreak: Boolean = false,
    val userId: String = "",
)
