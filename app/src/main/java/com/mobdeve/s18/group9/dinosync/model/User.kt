package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val createdAt: Timestamp? = null,
    val userBio: String = "",
    val userName: String = "",
    val userProfileImage: String = ""
)

