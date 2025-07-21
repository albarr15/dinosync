package com.mobdeve.s18.group9.dinosync.repository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.group9.dinosync.model.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.Calendar

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // COMPANION ✔️
    suspend fun getCompanionsByUserId(userId: String): List<Companion> {
        val snapshot = db.collection("companion")
            .whereEqualTo("userId", userId)
            .whereNotEqualTo("dateAwarded", null)
            .get().await()
        return snapshot.toObjects(Companion::class.java)
    }


    // COURSES ✔️
    suspend fun getAllCourses(): List<Course> {
        val snapshot = db.collection("course").get().await()
        return snapshot.toObjects(Course::class.java)
    }

    suspend fun addCourse(course: Course) {
        db.collection("course").add(course).await()
    }


    // DAILY STUDY HISTORY ✔️
    suspend fun getDailyStudyHistory(userId: String): List<DailyStudyHistory> {
        val snapshot = db.collection("dailystudyhistory")
            .whereEqualTo("userId", userId)
            .get().await()
        return snapshot.toObjects(DailyStudyHistory::class.java)
    }

    suspend fun getDailyStudyHistoryByDate(userId: String, date: String): DailyStudyHistory? {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("dailystudyhistory")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(DailyStudyHistory::class.java)
    }


    suspend fun updateDailyStudyHistory(history: DailyStudyHistory) = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val querySnapshot = db.collection("dailystudyhistory")
            .whereEqualTo("userId", history.userId)
            .whereEqualTo("date", history.date)
            .get()
            .await()

        val doc = querySnapshot.documents.firstOrNull()
        if (doc != null) {
            db.collection("dailystudyhistory")
                .document(doc.id)
                .set(history)
                .await()
        }
    }

    suspend fun insertDailyStudyHistory(history: DailyStudyHistory) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("dailystudyhistory")
            .add(history)
            .await()
    }



    // GROUP MEMBERS ✔️
    suspend fun getAllGroupMembers(): List<GroupMember> {
        return try {
            val snapshot = db.collection("groupmember").get().await()
            snapshot.documents.mapNotNull { it.toObject(GroupMember::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
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

    suspend fun getUserGroups(userId: String): List<StudyGroup> {
        val memberSnapshot = db.collection("groupmember")
            .whereEqualTo("userId", userId)
            .get().await()

        // extract all group ids
        val groupIds = memberSnapshot.documents.mapNotNull {
            it.getString("groupId")
        }

        if (groupIds.isEmpty()) return emptyList()

        // Batch read all groups, firebase currently limited to 10 'in' queries
        val groups = mutableListOf<StudyGroup>()

        groupIds.chunked(10).forEach { chunk ->
            val groupSnapshot = db.collection("studygroup")
                .whereIn(FieldPath.documentId(), chunk)
                .get().await()

            groups.addAll(groupSnapshot.toObjects<StudyGroup>())
        }

        return groups
    }

    suspend fun getUserMoodHistory(userId: String) : List<Mood> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val dailyStudyHistorySnapshot = db.collection("dailystudyhistory")
            .whereEqualTo("userId", userId)
            .orderBy("date")
            .get().await()

        val moodEntryIds = dailyStudyHistorySnapshot.documents.mapNotNull {
            it.getString("moodEntryId")
        }

        if (moodEntryIds.isEmpty()) return emptyList()

        // Batch read all mood entries

        val moods = mutableListOf<Mood>()
        moodEntryIds.chunked(10).forEach { chunk ->
            val moodSnapshot = db.collection("mood")
                .whereIn(FieldPath.documentId(), chunk)
                .get().await()

            moods.addAll(moodSnapshot.toObjects<Mood>())
        }

        return moods
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

    suspend fun addStudySession(session: StudySession) {
        val db = FirebaseFirestore.getInstance()
        val studySessionsRef = db.collection("studysession")
        studySessionsRef.add(session)
    }

    suspend fun createStudySessionAndReturnId(session: StudySession): String {
        val ref = db.collection("studysession").add(session).await()
        return ref.id
    }

    suspend fun updateStudySession(sessionId: String, updates: Map<String, Any>) {
        db.collection("studysession").document(sessionId).update(updates).await()
    }

    // STUDY GROUPS ✔️
    suspend fun getAllStudyGroups(): List<StudyGroup> {
        val snapshot = db.collection("studygroup").get().await()
        return snapshot.toObjects(StudyGroup::class.java)
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
    suspend fun createStudyGroup(group: StudyGroup) {
        val ref = db.collection("studygroup").document()
        val newGroup = group.copy(groupId = ref.id)
        ref.set(newGroup).await()
    }

    suspend fun deleteStudyGroup(groupId: String) {
        db.collection("studygroup").document(groupId).delete().await()
    }


    // TODO_ITEMS ️ ✔️
    suspend fun getTodosByUserId(userId: String): List<TodoDocument> {
        val snapshot = db.collection("todoitem")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val item = doc.toObject(TodoItem::class.java)
            item?.let {
                TodoDocument(id = doc.id, item = it)
            }
        }
    }

    // CREATE a new Todo
    suspend fun addTodoItem(item: TodoItem): String {
        val docRef = db.collection("todoitem").add(item).await()
        return docRef.id
    }


    // UPDATE a Todo (by document ID)
    suspend fun updateTodoItem(id: String, updatedItem: TodoItem) {
        db.collection("todoitem").document(id).set(updatedItem).await()
    }


    // DELETE a Todo (by document ID)
    suspend fun deleteTodoItem(id: String) {
        db.collection("todoitem").document(id).delete().await()
    }


}

