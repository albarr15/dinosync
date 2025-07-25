package com.mobdeve.s18.group9.dinosync.viewmodel

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobdeve.s18.group9.dinosync.model.*
import com.mobdeve.s18.group9.dinosync.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import com.mobdeve.s18.group9.dinosync.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.mobdeve.s18.group9.dinosync.getCurDate
import com.mobdeve.s18.group9.dinosync.ui.theme.DirtyGreen
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch

class CompanionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _companions = MutableStateFlow<List<Companion>>(emptyList())
    val companions: StateFlow<List<Companion>> = _companions

    fun loadCompanions(userId: String) {
        viewModelScope.launch {
            val fetchedCompanions = repository.getAllCompanionsByUserId(userId)

            if (fetchedCompanions.isEmpty()) {
                val newCompanion = Companion(
                    userId = userId,
                    dateCreated = Timestamp.now(),
                    current = true
                )
                repository.insertCompanion(newCompanion)

                // Re-fetch companions after adding
                _companions.value = repository.getAllCompanionsByUserId(userId)
            } else {
                _companions.value = fetchedCompanions
            }
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

class CompanionActViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _companions = MutableStateFlow<List<Companion>>(emptyList())
    val companions: StateFlow<List<Companion>> = _companions

    fun loadCompanions(userId: String) {
        viewModelScope.launch {
            _companions.value = repository.getAllCompanionsByUserId(userId)
        }
    }
}

class CourseViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    init {
        loadCourses()
    }

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
        Log.d("GroupActivityGroupActivity", "loadDailyHistory called")
    }

    fun updateDailyHistory(
        userId: String,
        date: String,
        moodId: String,
        additionalMinutes: Float,
        studyMode : String  // "Group" or  "Individual"
    ) {
        Log.d("DailyHistoryVM", "updateDailyHistory called → userId: $userId, date: $date, moodId: $moodId, additionalMinutes: $additionalMinutes, studyMode: $studyMode")

        viewModelScope.launch {
            val existing = repository.getDailyStudyHistoryByDate(userId, date)

            val currentGroupMinutes = existing?.totalGroupStudyMinutes ?: 0f
            val currentIndividualMinutes = existing?.totalIndividualMinutes ?: 0f

            val newGroupMinutes = if (studyMode == "Group") (currentGroupMinutes + additionalMinutes).coerceAtMost(1440f) else currentGroupMinutes
            val newIndividualMinutes = if (studyMode == "Individual") (currentIndividualMinutes + additionalMinutes).coerceAtMost(1440f) else currentIndividualMinutes

            if (existing != null) {
                Log.d("DailyHistoryVM", "Updating existing DailyStudyHistory for date: $date")

                val updatedEntry =  existing.copy(
                    hasStudied = true,
                    moodEntryId = if (moodId.isNotBlank()) moodId else existing.moodEntryId,
                    totalGroupStudyMinutes = newGroupMinutes,
                    totalIndividualMinutes = newIndividualMinutes
                )
                repository.updateDailyStudyHistory(updatedEntry)

            } else {
                Log.d("DailyHistoryVM", "Creating new DailyStudyHistory for date: $date")
                val newEntry = DailyStudyHistory(
                    userId = userId,
                    date = date,
                    hasStudied = true,
                    moodEntryId = moodId,
                    totalGroupStudyMinutes = newGroupMinutes,
                    totalIndividualMinutes = newIndividualMinutes
                )
                repository.insertDailyStudyHistory(newEntry)
            }
        }
    }
}

class GroupMemberViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members: StateFlow<List<GroupMember>> = _members

    fun loadAllMembers() {
        viewModelScope.launch {
            _members.value = repository.getAllGroupMembers()
        }
        Log.d("GroupActivityGroupActivity", "loadAllMembers called")
    }
    fun addGroupMember(newMember: GroupMember) {
        viewModelScope.launch {
            val result = repository.addGroupMember(newMember)
            if (result.isSuccess) {
                _members.value = repository.getAllGroupMembers()
                Log.d("GroupMemberViewModel", "Member added successfully")
            } else {
                Log.e("GroupMemberViewModel", "Failed to add member: ${result.exceptionOrNull()}")
            }
        }
    }
    fun leaveGroup(userId: String, groupId: String, startedAt: String, endedAt: String) {
        Log.d("leaveGroup", "Initiating leave group for userId=$userId, groupId=$groupId")
        Log.d("leaveGroup", "StartedAt=$startedAt, EndedAt=$endedAt")

        viewModelScope.launch {
            try {
                repository.updateGroupMemberEndedAt(userId, groupId, startedAt, endedAt)
                Log.d("leaveGroup", "Successfully updated endedAt in repository.")
            } catch (e: Exception) {
                Log.e("leaveGroup", "Failed to update endedAt", e)
            }
        }
    }

    fun startNewGroupSession(
        dailyStudyHistoryViewModel: DailyStudyHistoryViewModel,
        userId: String,
        moodId: String,
        groupMember: GroupMember,
        additionalMinutes: Float,
        startedAt: String
    ) {
        viewModelScope.launch {
            val date = getCurDate()

            // 1. Update daily history
            dailyStudyHistoryViewModel.updateDailyHistory(
                userId = userId,
                date = date,
                moodId = moodId,
                additionalMinutes = additionalMinutes,
                studyMode = "Group"
            )

            // 2. Reset GroupMember session
            repository.resetGroupMemberSession(
                userId = userId,
                groupId = groupMember.groupId,
                minutes = additionalMinutes,
                startedAt = startedAt
            )
        }
    }

}

// Not implemented yet
class GroupSessionViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _sessions = MutableStateFlow<List<GroupSession>>(emptyList())
    val sessions: StateFlow<List<GroupSession>> = _sessions

    fun loadGroupSessions(groupId: String) {
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

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog

    fun loadStudyGroups() {
        viewModelScope.launch {
            _studyGroups.value = repository.getAllStudyGroups()
        }
        Log.d("GroupActivityGroupActivity", "loadStudyGroups called")
    }

    fun createGroup(hostId: String, name: String, bio: String, university: String, courseId: String) {
        viewModelScope.launch {
            val group = StudyGroup(
                hostId = hostId,
                groupId = "",
                name = name,
                bio = bio,
                image = "groupimage",
                rank = 0L,
                university = university,
                courseId = courseId
            )
            repository.createStudyGroup(group)
            loadStudyGroups()
            _showCreateDialog.value = false
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteStudyGroup(groupId)
            loadStudyGroups()
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
        Log.d("GroupActivityGroupActivity", "loadAllUsers called")
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
            Log.d("Profile", "Mood History: ${_moodHistory.value}")
        }
    }
}

class UniversityViewModel : ViewModel() {
    private val _universityList = mutableStateOf<List<String>>(emptyList())
    val universityList: State<List<String>> = _universityList

    init {
        viewModelScope.launch {
            val universities = RetrofitClient.universityApi
                .getUniversities("Philippines")
                .map { it.name }
            _universityList.value = universities
        }
    }
}

class StatsViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _studySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val studySessions: MutableStateFlow<List<StudySession>> = _studySessions

    private val _dailyStudyHistory = MutableStateFlow<List<DailyStudyHistory>>(emptyList())
    val dailyStudyHistory: MutableStateFlow<List<DailyStudyHistory>> = _dailyStudyHistory

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _pieData = MutableStateFlow<List<Pie>>(emptyList())
    val pieData: MutableStateFlow<List<Pie>> = _pieData

    private val _streakData = MutableStateFlow<Map<String, Float>>(emptyMap())
    val streakData: MutableStateFlow<Map<String, Float>> = _streakData

    fun loadUserStats(userId: String) {
        viewModelScope.launch {
            _user.value = repository.getUserById(userId)
            _studySessions.value = repository.getStudySessionsByUserId(userId)
            _dailyStudyHistory.value = repository.getDailyStudyHistory(userId)
            _courses.value = repository.getAllUserCourses(userId)
            _streakData.value = repository.getTotalStudyMinsByUserId(userId)
        }
    }

    fun computeTotalSecondsPerCourse(sessions: List<StudySession>): Map<String, Double> {
        return sessions
            .filter { it.courseId != null && it.startedAt != null && it.endedAt != null }
            .groupBy { it.courseId?.takeIf { it.isNotBlank() } ?: "Unassigned" }
            .mapValues { (_, courseSessions) ->
                courseSessions.sumOf { session ->
                    val durationSeconds = (session.hourSet * 3660) + (session.minuteSet * 60)
                    durationSeconds.toDouble().coerceAtLeast(0.0) // ensure no negative
                }
            }
    }


    // TODO: LIMIT TO 5 COURSES
    fun loadPieData(userId: String) {
        viewModelScope.launch {
            val courses = repository.getAllUserCourses(userId)
            val sessions = repository.getStudySessionsByUserId(userId)

            val secondsPerCourse = computeTotalSecondsPerCourse(sessions)
            val totalSeconds =
                secondsPerCourse.values.sum().coerceAtLeast(1.0) // avoid divide-by-zero

            val allCourseIds = (courses.map { it.courseId } + "Unassigned").distinct()

            val pieColors = listOf(
                Color(0xFF388E3C), // Green
                Color(0xFFA7C957), // Light green
                Color(0xFFF9C74F), // Yellow
                Color(0xFF577590), // Blue-gray
                Color(0xFFDD6E42)  // Orange
            )

            _pieData.value = allCourseIds.mapIndexed { i, courseId ->
                val courseLabel = if (courseId == "Unassigned") "Unassigned"
                else courses.find { it.courseId == courseId }?.name ?: "Unknown"

                val courseSeconds = secondsPerCourse[courseLabel] ?: 0.0
                // Log.d("Load Pie Data", "$courseSeconds / $totalSeconds")
                val percentage = courseSeconds / totalSeconds
                val roundedPercentage = String.format("%.2f", percentage * 100).toDouble()

                Pie(
                    label = courseLabel,
                    data = roundedPercentage,
                    color = pieColors[i % pieColors.size]
                )
            }
        }
    }
}
