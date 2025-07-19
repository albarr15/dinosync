package com.mobdeve.s18.group9.dinosync.spotify

object SpotifyConstants {
    const val CLIENT_ID = "93599608efb84a51b0be69833bf25223"
    const val REDIRECT_URI = "dinosync://callback"
    const val REQUEST_CODE = 1337

    val SCOPES = arrayOf(
        "user-read-private",
        "user-read-email",
        "user-read-playback-state",
        "user-modify-playback-state",
        "user-read-currently-playing",
        "playlist-read-private",
        "playlist-read-collaborative",
        "playlist-modify-public",
        "playlist-modify-private",
        "streaming" // required if you’ll use App Remote later
    )

}