package com.mobdeve.s18.group9.dinosync.model

data class Mood(
    val imageKey: String = "",
    val name: String = ""
) {
    val level: Int
        get() = when (imageKey) {
            "very_dissatisfied" -> 1
            "dissatisfied" -> 2
            "neutral" -> 3
            "satisfied" -> 4
            "very_satisfied" -> 5
            else -> 0
        }
}

