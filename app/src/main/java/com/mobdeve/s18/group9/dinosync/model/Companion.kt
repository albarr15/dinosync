package com.mobdeve.s18.group9.dinosync.model
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.mobdeve.s18.group9.dinosync.R

// originally Achievements + DinoCatalog
// represents egg + hatched dinos
data class Companion(
    var requiredHatchTime: Int = 3600, // how many seconds need to hatch; default to 1 hr
    var remainingHatchTime: Int = -1, // sentinel value; will be set to requiredHatchTime if not provided
    var name: String = "",
    var dateAwarded: Timestamp? = null,
    var imageKey: String = "", // <-- Store this in Firestore
    var userId: String = "",
) {
    init {
        if (remainingHatchTime == -1) {
            remainingHatchTime = requiredHatchTime
        }
    }

    fun isReadyToHatch(): Boolean = remainingHatchTime <= 0 && !isHatched()

    // if no recorded date for award, not yet hatched
    fun isHatched(): Boolean = dateAwarded != null

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

    fun getBGColor(): Color {
        return when (imageKey) {
            "dino1" -> Color(0xFFC2F4ED)
            "dino2" -> Color(0xFFFFD3E7)
            "dino3" -> Color(0xFFE9A1A4)
            "dino4" -> Color(0xFFD7B593)
            "dino5" -> Color(0xFFD3E6A8)
            "dino6" -> Color(0xFFB4ECBB)
            else -> Color.LightGray
        }
    }
}