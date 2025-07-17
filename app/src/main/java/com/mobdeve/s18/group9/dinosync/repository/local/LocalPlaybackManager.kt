package com.mobdeve.s18.group9.dinosync.repository.local

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class LocalPlaybackManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: String? = null

    fun play(trackFileName: String) {
        try {
            if (trackFileName == currentTrack && mediaPlayer?.isPlaying == true) return

            stop() // stop existing track before playing new one

            val afd = context.assets.openFd("Local_Music/$trackFileName")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }

            currentTrack = trackFileName

            mediaPlayer?.setOnCompletionListener {
                Log.d("LocalPlaybackManager", "Playback finished.")
                currentTrack = null
            }

        } catch (e: Exception) {
            Log.e("LocalPlaybackManager", "Error playing $trackFileName", e)
        }
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrack = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
