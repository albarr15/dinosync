package com.mobdeve.s18.group9.dinosync.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.group9.dinosync.model.User
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun register(email: String, password: String, userName: String, userBio: String): Result<String> {
        return try {
            // Create user in Firebase Auth first
            val cred = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = cred.user?.uid ?: return Result.failure(Exception("Missing UID"))

            // Then check username uniqueness
            val usernameExists = db.collection("users")
                .whereEqualTo("userName", userName)
                .get()
                .await()
                .isEmpty.not()

            if (usernameExists) {
                // Rollback: delete auth user to avoid orphan account
                auth.currentUser?.delete()
                return Result.failure(Exception("Username already taken. Choose another."))
            }

            // Write Firestore user document
            val userMap = mapOf(
                "userName" to userName,
                "userBio" to userBio,
                "userProfileImage" to "",
                "userTotalStudyHoursSpent" to 0,
                "userTotalStudyHoursSpentIndividually" to 0,
                "userTotalStudyHoursSpentWithGroup" to 0,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("users").document(uid).set(userMap).await()


            return Result.success(uid)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val cred = auth.signInWithEmailAndPassword(email, password).await()
            val user = cred.user

            if (user != null && user.isEmailVerified) {
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Email not verified. Please verify your email."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun currentUserUid(): String? = auth.currentUser?.uid
}
