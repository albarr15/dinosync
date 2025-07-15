package com.mobdeve.s18.group9.dinosync.model

data class User(
    val createdAt: String = "",
    val userBio: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val userTotalStudyHoursSpent: Int = 0,
    val userTotalStudyHoursSpentIndividually: Int = 0,
    val userTotalStudyHoursSpentWithGroup: Int = 0,
)

