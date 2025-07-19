package com.mobdeve.s18.group9.dinosync.spotify
import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.*
import com.spotify.protocol.types.PlayerState

class SpotifyPlaybackManager(private val context: Context) {
    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionParams = ConnectionParams.Builder(SpotifyConstants.CLIENT_ID)
        .setRedirectUri(SpotifyConstants.REDIRECT_URI)
        .showAuthView(true)
        .build()

    fun connect(callback: (Boolean) -> Unit) {
        SpotifyAppRemote.connect(
            context,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("SpotifyRemote", "Connected successfully")
                    callback(true)
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("SpotifyRemote", "Connection failed", throwable)
                    callback(false)
                }
            }
        )
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
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

    fun skipToNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun skipToPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun getCurrentPlayerState(callback: (PlayerState?) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { playerState ->
            callback(playerState)
        }
    }
    fun subscribeToPlayerState(onPlayerStateChanged: (PlayerState) -> Unit) {
        spotifyAppRemote?.playerApi
            ?.subscribeToPlayerState()
            ?.setEventCallback { playerState ->
                onPlayerStateChanged(playerState)
            }
            ?.setErrorCallback { error ->
                Log.e("SpotifyPlaybackManager", "Error subscribing to player state", error)
            }
    }



}