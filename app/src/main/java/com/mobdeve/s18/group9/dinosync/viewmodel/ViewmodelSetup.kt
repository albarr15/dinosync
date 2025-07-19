package com.mobdeve.s18.group9.dinosync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobdeve.s18.group9.dinosync.model.*
import com.mobdeve.s18.group9.dinosync.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CompanionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _companions = MutableStateFlow<List<Companion>>(emptyList())
    val companions: StateFlow<List<Companion>> = _companions

    fun loadCompanions(userId: String) {
        viewModelScope.launch {
            _companions.value = repository.getCompanionsByUserId(userId)
        }
    }
}

class CourseViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    fun loadCourses() {
        viewModelScope.launch {
            _courses.value = repository.getAllCourses()
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

    fun updateDailyHistory(userId: String, date: String, moodId: String, hours: Float) {
        viewModelScope.launch {
            val existing = repository.getDailyStudyHistoryByDate(userId, date)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = dateFormat.parse(date)
            val timestamp = parsedDate?.let { Timestamp(it) }

            val updated = existing?.copy(
                hasStudied = true,
                moodEntryId = moodId,
                totalIndividualHours = (existing.totalIndividualHours + hours).toInt()
            )
                ?: DailyStudyHistory(
                    userId = userId,
                    date = timestamp,
                    hasStudied = true,
                    moodEntryId = moodId,
                    totalIndividualHours = hours.toInt()
                )

            repository.setDailyStudyHistory(updated)
        }
    }
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
    fun observeMusicSessions(userId: String): Flow<List<MusicSession>> {
        return repository.listenToMusicSessions(userId)
    }
    fun createMusicSession(session: MusicSession) {
        viewModelScope.launch {
            repository.createMusicSession(session)
        }
    }

}

class MusicSessionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _musicSessions = MutableStateFlow<List<MusicSession>>(emptyList())
    val musicSessions: StateFlow<List<MusicSession>> = _musicSessions

    fun loadMusicSessions(userId: String) {
        viewModelScope.launch {
            _musicSessions.value = repository.getMusicSessionsByUser(userId)
        }
    }

    fun musicSessionsFlow(userId: String): StateFlow<List<MusicSession>> {
        return repository.listenToMusicSessions(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun createMusicSession(session: MusicSession) {
        viewModelScope.launch {
            repository.createMusicSession(session)
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