package com.mobdeve.s18.group9.dinosync.spotify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyAuthorizationActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri: Uri? = intent.data
        if (uri != null) {
            val response = AuthorizationResponse.fromUri(uri)

            when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    val authCode = response.code
                    Log.d("SpotifyAuth", "Authorization code: $authCode")

                    // TODO: Exchange this code for an access token in your backend or token endpoint
                    // and/or pass it back to your main activity
                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e("SpotifyAuth", "Auth error: ${response.error}")
                }

                else -> {
                    Log.w("SpotifyAuth", "Auth cancelled or unknown type: ${response.type}")
                }
            }
        }
        // Optional: close this activity and return to main
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent) // To handle case when activity launched normally
    }
}