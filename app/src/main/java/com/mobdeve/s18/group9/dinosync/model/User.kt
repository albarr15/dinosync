package com.mobdeve.s18.group9.dinosync.model

data class User(
    val userId: Int,
    val userName: String,
    val userProfileImage: Int,
    val userBio: String,
    val userTotalStudyHoursSpent: Int = 0,
    val userTotalStudyHoursSpentIndividually: Int= 0,
    val userTotalStudyHoursSpentWithGroup: Int= 0
)
