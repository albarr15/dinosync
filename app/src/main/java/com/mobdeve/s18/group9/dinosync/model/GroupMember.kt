package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class GroupMember(
    val startedAt: String = "",
    var endedAt: String = "",
    val currentGroupStudyMinutes: Float = 0f,
    val groupId:String = "",
    val isOnBreak: Boolean = false,
    val userId: String = "",
)
