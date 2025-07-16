package com.mobdeve.s18.group9.dinosync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.model.Music
import com.mobdeve.s18.group9.dinosync.ui.theme.GreenGray
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun AudioPlayerCardSpotify(
    title: String,                       // Song title from Spotify
    artist: String,                      // Artist name from Spotify
    albumArtUri: String,                 // Album art URI from Spotify
    isPlaying: Boolean,                  // True if song is playing
    progress: Float,                     // Playback progress (0.0 to 1.0)
    onShuffle: () -> Unit = {},          // Triggered when shuffle icon is clicked
    onPrevious: () -> Unit = {},         // Triggered when previous icon is clicked
    onPlayPause: () -> Unit = {},        // Triggered when play/pause icon is clicked
    onNext: () -> Unit = {},             // Triggered when next icon is clicked
    onRepeat: () -> Unit = {}            // Triggered when repeat icon is clicked
) {
    // Main container box for styling and layout
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GreenGray) // Your custom color
    ) {
        // Horizontal row layout: Album Art | Track Info + Controls
        Row(modifier = Modifier.fillMaxSize()) {

            // Load and display album art using Coil's AsyncImage
            AsyncImage(
                model = albumArtUri,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
            )

            // Vertical layout for Title, Artist, Progress, and Controls
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 15.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Track title and artist text
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )

                    Text(
                        text = artist,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Progress bar showing how far the song has played
                LinearProgressIndicator(
                    progress = { progress }, // Value should be 0.0 to 1.0
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color.Black,
                    trackColor = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom row for music control buttons
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
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(24.dp)
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
