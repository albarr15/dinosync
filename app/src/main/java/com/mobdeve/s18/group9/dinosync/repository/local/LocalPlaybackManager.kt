package com.mobdeve.s18.group9.dinosync.repository.local

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class LocalPlaybackManager(
    private val context: Context,
    private val onCompletion: (() -> Unit)? = null
) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: String? = null
    private var repeatMode = false
    private var isPaused = false
    private var onTrackEnded: (() -> Unit)? = null
    private var shuffleEnabled = false


    fun setOnTrackEndedListener(listener: () -> Unit) {
        onTrackEnded = listener
    }

    fun setShuffleMode(enabled: Boolean) {
        shuffleEnabled = enabled
    }

    fun setRepeatMode(enabled: Boolean) {
        repeatMode = enabled
        mediaPlayer?.isLooping = enabled
    }

    fun play(trackFileName: String) {
        try {
            if (trackFileName == currentTrack && isPaused) {
                resume()
                return
            }

            if (trackFileName == currentTrack && mediaPlayer?.isPlaying == true) return

            stop()

            val afd = context.assets.openFd("Local_Music/$trackFileName")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                isLooping = repeatMode
                start()
                setOnCompletionListener {
                    Log.d("LocalPlaybackManager", "Playback finished.")
                    currentTrack = null
                    onTrackEnded?.invoke() ?: onCompletion?.invoke() // 🔥 this triggers next song
                }
            }

            currentTrack = trackFileName
            isPaused = false

        } catch (e: Exception) {
            Log.e("LocalPlaybackManager", "Error playing $trackFileName", e)
        }
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.apply {
            pause()
            isPaused = true
        }
    }

    fun resume() {
        mediaPlayer?.start()
        isPaused = false
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrack = null
        isPaused = false
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun getDuration(): Int = mediaPlayer?.duration ?: 0
}
