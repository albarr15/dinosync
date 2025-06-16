package com.mobdeve.s18.group9.dinosync.model
import java.util.Date

data class FeelingEntry(
    val entryId: Int = 0,
    val userId: Int,
    val moodId: Int,
    val journalEntry: String,
    val entryDate: Date
)
