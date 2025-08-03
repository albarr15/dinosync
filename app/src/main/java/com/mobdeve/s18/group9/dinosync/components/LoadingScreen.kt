package com.mobdeve.s18.group9.dinosync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.YellowGreen

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.7f))
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = DarkGreen
        )
    }
}