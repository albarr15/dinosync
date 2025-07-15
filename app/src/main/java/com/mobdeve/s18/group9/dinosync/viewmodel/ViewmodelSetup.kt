package com.mobdeve.s18.group9.dinosync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobdeve.s18.group9.dinosync.model.*
import com.mobdeve.s18.group9.dinosync.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AchievementViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    fun loadAchievements(userId: String) {
        viewModelScope.launch {
            _achievements.value = repository.getAchievementsByUserId(userId)
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
}

class DinoCatalogViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _dinos = MutableStateFlow<List<DinoCatalog>>(emptyList())
    val dinos: StateFlow<List<DinoCatalog>> = _dinos

    fun loadDinos() {
        viewModelScope.launch {
            _dinos.value = repository.getAllDino()
        }
    }
    val dinoCatalogFlow: StateFlow<List<DinoCatalog>> = repository.listenToDinoCatalog()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
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
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }
}

class TodoViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos

    fun loadTodos(userId: String) {
        viewModelScope.launch {
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
