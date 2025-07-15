package com.mobdeve.s18.group9.dinosync.model

data class Achievement(
    val dateAwarded: String = "",
    val dinoId: String = "",        // Connected to an instance of Dino_Catalog
    val userId: String = "",        // Connected to specific user
)

