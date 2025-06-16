package com.mobdeve.s18.group9.dinosync.model

data class Music(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Long,             // duration in milliseconds
    val albumArtUri: String? = null // optional cover image
)
