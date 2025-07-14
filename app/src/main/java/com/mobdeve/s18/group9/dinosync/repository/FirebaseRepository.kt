package com.mobdeve.s18.group9.dinosync.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.group9.dinosync.model.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // ACHIEVEMENTS ✔️
    suspend fun getAchievementsByUserId(userId: String): List<Achievement> {
        val snapshot = db.collection("achievement")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(Achievement::class.java)
    }

    // COURSES ✔️
    suspend fun getAllCourses(): List<Course> {
        val snapshot = db.collection("course").get().await()
        return snapshot.toObjects(Course::class.java)
    }

    // DAILY STUDY HISTORY ✔️
    suspend fun getDailyStudyHistory(userId: String): List<DailyStudyHistory> {
        val snapshot = db.collection("dailystudyhistory")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(DailyStudyHistory::class.java)
    }

    // DINO CATALOG✔️
    suspend fun getAllDino(): List<DinoCatalog> {
        val snapshot = db.collection("dino_catalog").get().await()
        return snapshot.toObjects(DinoCatalog::class.java)
    }

    suspend fun getDinoById(dinoId: String): DinoCatalog? {
        val snapshot = db.collection("dino_catalog").document(dinoId).get().await()
        return snapshot.toObject(DinoCatalog::class.java)
    }
    fun listenToDinoCatalog(): Flow<List<DinoCatalog>> = callbackFlow {
        val listener = db.collection("dino_catalog")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dinos = snapshot?.toObjects(DinoCatalog::class.java) ?: emptyList()
                trySend(dinos)
            }

        awaitClose { listener.remove() }
    }
    // GROUP MEMBERS ✔️
    suspend fun getGroupMembers(groupId: String): List<GroupMember> {
        val snapshot = db.collection("groupmember")
            .whereEqualTo("groupId", groupId)
            .get().await()
        return snapshot.toObjects(GroupMember::class.java)
    }

    // GROUP SESSIONS  ✔️
    suspend fun getGroupSessions(groupId: String): List<GroupSession> {
        val snapshot = db.collection("groupsession")
            .whereEqualTo("groupId", groupId)
            .get().await()
        return snapshot.toObjects(GroupSession::class.java)
    }

    // MOODS ✔️
    suspend fun getAllMoods(): List<Mood> {
        val snapshot = db.collection("mood").get().await()
        return snapshot.toObjects(Mood::class.java)
    }

    // MUSIC ✔️
    suspend fun getAllMusic(): List<Music> {
        val snapshot = db.collection("music").get().await()
        return snapshot.toObjects(Music::class.java)
    }

    // MUSIC SESSION ✔️
    suspend fun getMusicSessionsByUser(userId: String): List<MusicSession> {
        val snapshot = db.collection("musicsession")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(MusicSession::class.java)
    }

    // Real-time listener for MusicSession
    fun listenToMusicSessions(userId: String): Flow<List<MusicSession>> = callbackFlow {
        val listener = db.collection("musicsession")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.toObjects(MusicSession::class.java) ?: emptyList()
                trySend(sessions)
            }

        awaitClose { listener.remove() }
    }

    // USERS ✔️
    suspend fun getUserById(userId: String): User? {
        val snapshot = db.collection("users").document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    // ✔️
    suspend fun getAllUsers(): List<User> {
        val snapshot = db.collection("users").get().await()
        return snapshot.toObjects(User::class.java)
    }

    // STUDY SESSIONS ✔️
    suspend fun getStudySessionsByUserId(userId: String): List<StudySession> {
        val snapshot = db.collection("studysession")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(StudySession::class.java)
    }

    // Real-time listener for individual StudySession
    fun listenToStudySessions(userId: String): Flow<List<StudySession>> = callbackFlow {
        val listener = db.collection("studysession")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.toObjects(StudySession::class.java) ?: emptyList()
                trySend(sessions)
            }

        awaitClose { listener.remove() }
    }

    // STUDY GROUPS ✔️
    suspend fun getAllStudyGroups(): List<StudyGroup> {
        val snapshot = db.collection("studygroup").get().await()
        return snapshot.toObjects(StudyGroup::class.java)
    }

    // TODO_ITEMS ️ ✔️
    suspend fun getTodosByUserId(userId: String): List<TodoItem> {
        val snapshot = db.collection("todoitem")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(TodoItem::class.java)
    }

    // Real-time listener for group sessions
    fun listenToGroupSessions(groupId: String): Flow<List<GroupSession>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = db.collection("groupsession")
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val sessions = snapshot?.toObjects(GroupSession::class.java) ?: emptyList()
                trySend(sessions)
            }

        awaitClose { listenerRegistration.remove() }
    }
}

