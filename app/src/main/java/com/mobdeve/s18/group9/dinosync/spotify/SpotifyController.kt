package com.mobdeve.s18.group9.dinosync.spotify

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.*
import com.spotify.protocol.types.Track
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import androidx.core.net.toUri

// CONFIGURATION OF SPOTIFY API
// PROJECT STRUCTURE
// APP
// WITHIN APP, CLICK +
// IMPORT .JAR/AAR FILES
// ATTACHED COMPLETE PATH + .AAR


const val CLIENT_ID = "93599608efb84a51b0be69833bf25223"
const val REDIRECT_URI = "dinosync://callback"

class SpotifyController(private val context: Context) {
    private var spotifyAppRemote: SpotifyAppRemote? = null

    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(remote: SpotifyAppRemote) {
                    spotifyAppRemote = remote
                    Log.d("Spotify", "Connected!")
                    onConnected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("Spotify", "Connection failed", throwable)
                    onError(throwable)
                }
            })
    }

    fun subscribeToPlayerState(
        onStateUpdate: (track: Track, isPlaying: Boolean, position: Long, duration: Long) -> Unit
    ) {
        spotifyAppRemote?.playerApi
            ?.subscribeToPlayerState()
            ?.setEventCallback { playerState ->
                val track = playerState.track
                if (track != null) {
                    onStateUpdate(
                        track,
                        !playerState.isPaused,
                        playerState.playbackPosition,
                        track.duration
                    )
                }
            }
    }

    fun play(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
        }
    }
}

// Inside your Composable:
@Composable
fun SpotifyPlaybackScreen(context: Context) {
    val spotifyController = remember { SpotifyController(context) }

    var currentTrackTitle by remember { mutableStateOf("") }
    var currentTrackArtist by remember { mutableStateOf("") }
    var albumArtUri by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        spotifyController.connect(
            onConnected = {
                spotifyController.subscribeToPlayerState { track, playing, position, duration ->
                    currentTrackTitle = track.name
                    currentTrackArtist = track.artist.name
                    albumArtUri = track.imageUri.raw.toString()
                    isPlaying = playing
                    progress = position.toFloat() / duration.toFloat()
                }
            },
            onError = {
                Log.e("Spotify", "Error: ${'$'}it")
            }
        )
    }
}

// Launch Spotify auth if needed
fun launchSpotifyLogin(context: Context) {
    val uri = ("https://accounts.spotify.com/authorize"
            + "?client_id=${CLIENT_ID}"
            + "&response_type=token"
            + "&redirect_uri=${REDIRECT_URI}"
            + "&scope=app-remote-control,user-modify-playback-state,user-read-playback-state").toUri()
    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
}
