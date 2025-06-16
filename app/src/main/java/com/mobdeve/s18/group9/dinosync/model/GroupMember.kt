package com.mobdeve.s18.group9.dinosync.model

data class GroupMember(
    val groupMemberId: Int = 0,
    val groupId: Int,
    val userId: Int,
    val isOnBreak: Boolean = false,
    val currentGroupStudyMinutes: Int = 0
)
