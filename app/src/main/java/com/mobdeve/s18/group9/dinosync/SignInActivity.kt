package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.components.BottomNavigationBar
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration

/** TODO: Saving account details, password & email verification **/


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("userId", -1)

        setContent {
            DinoSyncTheme {
                SignInActivityScreen()
            }
        }
    }

    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy()")
    }
}

@Composable
fun SignInActivityScreen() {
    val context = LocalContext.current
    var isPWVisible by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        // Do nothing, just to disable back btn
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.dinosync_logo_white),
                contentDescription = "DinoSync Logo (White)",
                modifier = Modifier.size(100.dp)
            )
            Text(
                "DinoSync",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Welcome back!",
                color = Color.White,
                fontSize = 10.sp
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )
            // card for user input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 3))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // adds spacing between fields
            ) {
                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Username or Email") },
                    modifier = Modifier.fillMaxWidth(),
                    lineLimits = TextFieldLineLimits.SingleLine
                )
                OutlinedSecureTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Password") },
                    trailingIcon = @androidx.compose.runtime.Composable {
                        IconButton(onClick = {
                            isPWVisible = !isPWVisible
                        }) {
                            if (isPWVisible) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "Show Password",
                                    tint = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = "Hide Password",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    textObfuscationMode = if (isPWVisible) {TextObfuscationMode.Visible} else {TextObfuscationMode.RevealLastTyped},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier.height(23.dp)
                )
                Row (modifier = Modifier.align(Alignment.End)
                    .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Don't have an account? ", fontSize = 14.sp)
                    Text(
                        text = "Register",
                        modifier = Modifier.clickable {
                            val intent = Intent(context, RegisterActivity::class.java)
                            context.startActivity(intent) },
                        color = Color(0xFF1E88E5),
                        fontSize = 14.sp,
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                Button(onClick = { val intent = Intent(context, MainActivity::class.java)
                    // disable going back to sign in page upon successful sign in
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent) }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = Color.White
                    )) {
                    Text("Sign In")
                }
            }
        }
    }
}
