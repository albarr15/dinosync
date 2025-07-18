package com.mobdeve.s18.group9.dinosync.model
import androidx.annotation.DrawableRes
import com.google.firebase.Timestamp

// achievement is represented as dino
data class Achievement(
    val title:String = "",
    val awardedForHours: Int = 0,
    val dateAwarded: Timestamp? = null,
    @DrawableRes val imageResId: Int,
    val userId: String = "",
)