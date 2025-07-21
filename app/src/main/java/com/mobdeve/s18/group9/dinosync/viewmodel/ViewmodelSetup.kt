package com.mobdeve.s18.group9.dinosync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobdeve.s18.group9.dinosync.model.*
import com.mobdeve.s18.group9.dinosync.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class CompanionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _companions = MutableStateFlow<List<Companion>>(emptyList())
    val companions: StateFlow<List<Companion>> = _companions

    fun loadCompanions(userId: String) {
        viewModelScope.launch {
            _companions.value = repository.getAllCompanionsByUserId(userId)
        }
    }

    fun updateCurrentCompanion(
        userId: String,
        current: Boolean,
        dateAwarded: Timestamp?,
        remainingHatchTime: Int
    ) {
        Log.d(
            "CompanionVM",
            "updateCurrentCompanion called → userId: $userId, current: $current, " +
                    "dateAwarded: $dateAwarded, remainingHatchTime: $remainingHatchTime"
        )

        viewModelScope.launch {
            val currentCompanion = repository.getCurrentCompanionByUserId(userId)

            val cappedHatchTime = remainingHatchTime.coerceAtLeast(0)

            if (cappedHatchTime > 0) {
                Log.d("CompanionVM", "Updating existing Companion")
                val updatedEntry = currentCompanion.copy(
                    current = true,
                    dateAwarded = null,
                    remainingHatchTime = cappedHatchTime
                )
                repository.updateCompanion(userId, updatedEntry)
                loadCompanions(userId)
            } else {
                Log.d("CompanionVM", "Hatching companion and creating new egg")
                val now = Timestamp.now()
                // 1. Set previous companion as not current and set dateAwarded
                val hatchedCompanion = currentCompanion.copy(
                    current = false,
                    dateAwarded = now,
                    remainingHatchTime = 0
                )
                repository.updateCompanion(userId, hatchedCompanion)
                loadCompanions(userId)
                // 2. Create new egg
                val newEgg = Companion(
                    userId = userId,
                    requiredHatchTime = 5, // TODO: adjust as needed
                    remainingHatchTime = 5,
                    current = true,
                    dateCreated = now
                )
                repository.insertCompanion(newEgg)
                loadCompanions(userId)
            }
        }
    }
}

class CourseViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    fun loadCourses() {
        viewModelScope.launch {
            try {
                val courseList = repository.getAllCourses()
                _courses.value = courseList
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error loading courses: ${e.message}")
            }
        }
    }
    fun addCourseIfNew(courseName: String) {
        viewModelScope.launch {
            if (courseName.isNotBlank() && !_courses.value.any { it.name == courseName }) {
                val newCourse = Course(name = courseName)
                repository.addCourse(newCourse)
                loadCourses()
            }
        }
    }
}


class DailyStudyHistoryViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _dailyHistory = MutableStateFlow<List<DailyStudyHistory>>(emptyList())
    val dailyHistory: StateFlow<List<DailyStudyHistory>> = _dailyHistory

    fun loadDailyHistory(userId: String) {
        viewModelScope.launch {
            _dailyHistory.value = repository.getDailyStudyHistory(userId)
        }
    }

    fun createDailyHistory(
        userId: String,
        date: String,
        moodId: String,
    ) {
        viewModelScope.launch {
            val newEntry = DailyStudyHistory(
                userId = userId,
                hasStudied = true,
                date = date,
                moodEntryId = moodId,
                totalIndividualMinutes = 0f,
                totalGroupStudyMinutes = 0f

            )
            repository.insertDailyStudyHistory(newEntry)
        }
        Log.d("DailyHistoryVM", "createDailyHistory, ${userId},  ${date}, ${moodId}")

    }

    fun updateDailyHistory(
        userId: String,
        date: String,
        moodId: String,
        additionalMinutes: Float
    ) {
        Log.d("DailyHistoryVM", "updateDailyHistory called → userId: $userId, date: $date, moodId: $moodId, additionalMinutes: $additionalMinutes")

        viewModelScope.launch {
            val existing = repository.getDailyStudyHistoryByDate(userId, date)

            val currentMinutes = existing?.totalIndividualMinutes ?: 0f
            val cappedMinutes = (currentMinutes + additionalMinutes).coerceAtMost(1440f)

            if (existing != null) {
                Log.d("DailyHistoryVM", "Updating existing DailyStudyHistory for date: $date")
                val updatedEntry =  existing.copy(
                    hasStudied = true,
                    moodEntryId = moodId,
                    totalIndividualMinutes = cappedMinutes
                )
                repository.updateDailyStudyHistory(updatedEntry)

            } else {
                Log.d("DailyHistoryVM", "Creating new DailyStudyHistory for date: $date")
                val create = DailyStudyHistory(
                    userId = userId,
                    date = date,
                    hasStudied = true,
                    moodEntryId = moodId,
                    totalIndividualMinutes = 0f,
                    totalGroupStudyMinutes = 0f
                )
                repository.insertDailyStudyHistory(create)
            }
        }
    }


    /*
    fun recordHistory(userId: String, date: String, moodId: String, minutes: Float) {
        viewModelScope.launch {
            updateDailyHistory(userId, date, moodId, minutes)
        }
    }
    */

}

class GroupMemberViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members: StateFlow<List<GroupMember>> = _members

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            _members.value = repository.getGroupMembers(groupId)
        }
    }
}

class GroupSessionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _sessions = MutableStateFlow<List<GroupSession>>(emptyList())
    val sessions: StateFlow<List<GroupSession>> = _sessions

    fun loadGroupSessions(groupId: String) {
        viewModelScope.launch {
            _sessions.value = repository.getGroupSessions(groupId)
        }
    }

    fun groupSessionsFlow(groupId: String): StateFlow<List<GroupSession>> {
        return repository.listenToGroupSessions(groupId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }
}

class MoodViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _moods = MutableStateFlow<List<Mood>>(emptyList())
    val moods: StateFlow<List<Mood>> = _moods

    fun loadMoods() {
        viewModelScope.launch {
            _moods.value = repository.getAllMoods()
        }
    }

}

class MusicViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _musicList = MutableStateFlow<List<Music>>(emptyList())
    val musicList: StateFlow<List<Music>> = _musicList

    fun loadMusic() {
        viewModelScope.launch {
            _musicList.value = repository.getAllMusic()
        }
    }


}

class StudyGroupViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _studyGroups = MutableStateFlow<List<StudyGroup>>(emptyList())
    val studyGroups: StateFlow<List<StudyGroup>> = _studyGroups

    fun loadStudyGroups() {
        viewModelScope.launch {
            _studyGroups.value = repository.getAllStudyGroups()
        }
    }
}

class StudySessionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _studySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val studySessions: StateFlow<List<StudySession>> = _studySessions

    fun loadStudySessions(userId: String) {
        viewModelScope.launch {
            _studySessions.value = repository.getStudySessionsByUserId(userId)
        }
    }

    fun studySessionsFlow(userId: String): StateFlow<List<StudySession>> {
        return repository.listenToStudySessions(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun createStudySession(session: StudySession) {
        viewModelScope.launch {
            repository.addStudySession(session)
        }
    }

    fun createStudySessionAndGetId(session: StudySession, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val id = repository.createStudySessionAndReturnId(session)
            onComplete(id)
        }
    }

    fun updateStudySession(sessionId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updateStudySession(sessionId, updates)
        }
    }
}


class TodoViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _todos = MutableStateFlow<List<TodoDocument>>(emptyList())
    val todos: StateFlow<List<TodoDocument>> = _todos

    fun loadTodos(userId: String) {
        viewModelScope.launch {
            _todos.value = repository.getTodosByUserId(userId)
        }
    }

    fun getTodoItems(): List<TodoItem> = _todos.value.map { it.item }
    fun addTodo(item: TodoItem) {
        viewModelScope.launch {
            repository.addTodoItem(item)
            _todos.value = repository.getTodosByUserId(item.userId) // Refresh list
        }
    }

    fun updateTodo(id: String, updatedItem: TodoItem) {
        viewModelScope.launch {
            repository.updateTodoItem(id, updatedItem)
            //_todos.value = repository.getTodosByUserId(updatedItem.userId)
            _todos.update { list ->
                list.map { if (it.id == id) TodoDocument(id, updatedItem) else it }
            }
        }
    }

    fun deleteTodo(id: String, userId: String) {
        viewModelScope.launch {
            repository.deleteTodoItem(id)
            _todos.value = repository.getTodosByUserId(userId)

        }
    }

}


class UserViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _user.value = repository.getUserById(userId)
        }
    }

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    fun loadAllUsers() {
        viewModelScope.launch {
            _allUsers.value = repository.getAllUsers()
        }
    }
}

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _companions = MutableStateFlow<List<Companion>>(emptyList())
    val companions: StateFlow<List<Companion>> = _companions

    private val _groups = MutableStateFlow<List<StudyGroup>>(emptyList())
    val groups: StateFlow<List<StudyGroup>> = _groups

    private val _moodHistory = MutableStateFlow<List<Mood>>(emptyList())
    val moodHistory: StateFlow<List<Mood>> = _moodHistory

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _user.value = repository.getUserById(userId)
            _companions.value = repository.getCompanionsByUserId(userId)
            _groups.value = repository.getUserGroups(userId)
            _moodHistory.value = repository.getUserMoodHistory(userId)
        }
    }
}