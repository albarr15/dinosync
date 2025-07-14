package com.mobdeve.s18.group9.dinosync.model

data class GroupMember(
    val currentGroupStudyMinutes: Int = 0,
    val groupId:String = "",
    val isOnBreak: Boolean = false,
    val userId: String = "",
)
