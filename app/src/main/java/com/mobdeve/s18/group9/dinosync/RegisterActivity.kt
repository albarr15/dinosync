package com.mobdeve.s18.group9.dinosync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.ui.theme.DarkGreen
import com.mobdeve.s18.group9.dinosync.ui.theme.DinoSyncTheme
import com.mobdeve.s18.group9.dinosync.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            setContent {
                DinoSyncTheme {
                    MainScreen(currentUser.uid)
                }
            }
        } else {
            setContent {
                DinoSyncTheme {
                    RegisterActivityScreen()
                }
            }
        }
    }

    /******** ACTIVITY LIFE CYCLE ******** */
    override fun onStart() {
        super.onStart()
        println("RegisterActivity onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("RegisterActivity onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("RegisterActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("RegisterActivity onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        println("RegisterActivity onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("RegisterActivity onDestroy()")
    }
}

@Composable
fun RegisterActivityScreen(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }


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
                "Create a new account to get started.",
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
                // Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide" else "Show"
                            )
                        }
                    }
                )
                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (isConfirmPasswordVisible) "Hide" else "Show"
                            )
                        }
                    }
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                // Sign In Link
                Row (modifier = Modifier.align(Alignment.End)
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Already have an account? ", fontSize = 14.sp,)
                    Text(
                        text = "Sign In",
                        modifier = Modifier.clickable {
                            val intent = Intent(context, SignInActivity::class.java)
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
                /*
                *   🔁 – button clicked
                    🔐 – Firebase request started
                    ✅ – success toast
                    ❌ – error from Firebase
                * */
                Button(
                    onClick = {
                        println("🔁 Register button clicked")

                        if (username.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            println("⚠️ Fields are empty")
                            return@Button
                        }

                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            println("⚠️ Passwords do not match")
                            return@Button
                        }

                        println("🔐 Attempting to register user...")
                        authViewModel.register(
                            userName = username,
                            email = email,
                            password = password,
                            userBio = "",
                            onSuccess = {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Account created! Please check your email for verification.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            println("✅ Verification email sent to ${currentUser.email}")
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Account created, but failed to send verification email.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            println("❌ Failed to send verification email: ${task.exception?.message}")
                                        }
                                    }

                                val intent = Intent(context, SignInActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, "Registration failed: $errorMsg", Toast.LENGTH_SHORT).show()
                                println("❌ Registration failed: $errorMsg")
                                Log.e("REGISTER_ERROR", "Registration failed: $errorMsg")
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Register")
                }

            }
        }
    }
}
