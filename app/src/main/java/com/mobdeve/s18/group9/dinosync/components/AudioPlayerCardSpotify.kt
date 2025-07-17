package com.mobdeve.s18.group9.dinosync.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.ui.theme.GreenGray

@Composable
fun AudioPlayerCardSpotify(
    trackTitle: String,
    trackArtist: String,
    albumArtBitmap: Bitmap?,
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    onShuffle: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onRepeat: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GreenGray)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Album Art
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                albumArtBitmap?.let { bmp ->
                    Image(
                        painter = BitmapPainter(bmp.asImageBitmap()),
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            // Metadata + Progress + Controls
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 15.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = trackTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        text = trackArtist,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color.Black,
                    trackColor = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onShuffle) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onRepeat) {
                        Icon(Icons.Default.Repeat, contentDescription = "Repeat", modifier = Modifier.size(30.dp))
                    }
                }
            }
        }
    }
}

/**
 * Overload that accepts a SpotifyTrack and a bitmap album art.
 */
@Composable
fun AudioPlayerCardSpotify(
    track: SpotifyTrack,
    albumArtBitmap: Bitmap?,
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    onShuffle: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onRepeat: () -> Unit = {}
) {
    AudioPlayerCardSpotify(
        trackTitle = track.name,
        trackArtist = track.artists.joinToString { it.name },
        albumArtBitmap = albumArtBitmap,
        isPlaying = isPlaying,
        progress = progress,
        modifier = modifier,
        onShuffle = onShuffle,
        onPrevious = onPrevious,
        onPlayPause = onPlayPause,
        onNext = onNext,
        onRepeat = onRepeat
    )
}


data class SpotifyTrack(
    val id: String,
    val uri: String,
    val name: String,
    val artists: List<SpotifyArtist>
)

data class SpotifyArtist(val id: String, val name: String)
