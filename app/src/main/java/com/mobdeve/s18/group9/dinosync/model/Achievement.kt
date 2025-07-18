package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp
import com.mobdeve.s18.group9.dinosync.R

// achievement is represented as dino
data class Achievement(
    val title: String = "",
    val awardedForHours: Int = 0,
    val dateAwarded: Timestamp? = null,
    val imageKey: String = "", // <-- Store this in Firestore
    val userId: String = "",
) {
    fun getDrawableRes(): Int {
        return when (imageKey) {
            "dino1" -> R.drawable.dino1
            "dino2" -> R.drawable.dino2
            "dino3" -> R.drawable.dino3
            "dino4" -> R.drawable.dino4
            "dino5" -> R.drawable.dino5
            "dino6" -> R.drawable.dino6
            else -> R.drawable.logoblack
        }
    }
}