package com.mobdeve.s18.group9.dinosync.model
import com.google.firebase.Timestamp
import com.mobdeve.s18.group9.dinosync.R
import com.google.firebase.firestore.PropertyName

// originally Achievements + DinoCatalog
// represents egg + hatched dinos
data class Companion(
    var requiredHatchTime: Int = 1, // how many seconds need to hatch
    var remainingHatchTime: Int = -1, // sentinel value; will be set to requiredHatchTime if not provided
    var typeString: String = DinoType.entries.random().name, // store as string in Firestore
    var name: String = "", // will be set according to type
    var dateAwarded: Timestamp? = null,
    var userId: String = "",
    var current: Boolean = false, // set as current on egg creation
    var dateCreated: Timestamp? = null
) {
    val type: DinoType
        get() = DinoType.valueOf(typeString)

    init {
        if (remainingHatchTime == -1) {
            remainingHatchTime = requiredHatchTime
        }
        // Set name according to type if not already set
        if (name.isBlank()) {
            name = when (type) {
                DinoType.STEGO -> "Stegosaurus"
                DinoType.PLESIO -> "Plesiosaurus"
                DinoType.RAPTOR -> "Velociraptor"
                DinoType.PTERO -> "Pterodactyl"
                DinoType.BRONTO -> "Brontosaurus"
                DinoType.TRICERA -> "Triceratops"
            }
        }
    }

    enum class DinoType {
        STEGO, PLESIO, RAPTOR, PTERO, BRONTO, TRICERA
    }

    fun isReadyToHatch(): Boolean = remainingHatchTime <= 0 && !isHatched()

    // if no recorded date for award, not yet hatched
    fun isHatched(): Boolean = dateAwarded != null

    fun getDrawableRes(): Int {
        return when (type) {
            DinoType.STEGO -> R.drawable.dino1
            DinoType.PLESIO -> R.drawable.dino2
            DinoType.RAPTOR -> R.drawable.dino3
            DinoType.PTERO -> R.drawable.dino4
            DinoType.BRONTO -> R.drawable.dino5
            DinoType.TRICERA -> R.drawable.dino6
            else -> R.drawable.logoblack
        }
    }
}