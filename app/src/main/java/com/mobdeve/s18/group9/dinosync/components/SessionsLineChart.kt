
package com.mobdeve.s18.group9.dinosync.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobdeve.s18.group9.dinosync.model.DailyStudyHistory
import com.mobdeve.s18.group9.dinosync.model.GroupMember
import com.mobdeve.s18.group9.dinosync.model.StudyGroup
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Sealed class to represent either user or group filtering
sealed class ChartFilter {
    data class ByUser(val userId: String) : ChartFilter()
    data class ByGroup(val groupId: String, val groupMembers: List<GroupMember>) : ChartFilter()
}

// Filtering logic for daily history based on chart filter
fun filterDailyHistoryByFilter(
    filter: ChartFilter,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
): List<DailyStudyHistory> {
    return when (filter) {
        is ChartFilter.ByUser -> {
            dailyStudyHistory.filter { history ->
                history.userId == filter.userId &&
                        studySessions.any { session ->
                            session.userId == filter.userId && session.status == "done"
                        }
            }
        }
        is ChartFilter.ByGroup -> {
            val groupUserIds = filter.groupMembers
                .filter { it.groupId == filter.groupId } // TODO
                .map { it.userId }

            dailyStudyHistory.filter { history ->
                history.userId in groupUserIds &&
                studySessions.any { session ->
                    session.userId == history.userId &&
                            session.status == "done"
                }
            }
        }
    }
}

// Aggregation logic for hourly chart data
fun aggregateHourlyChartData(
    sessions: List<StudySession>,
    targetDate: Date,
    filter: ChartFilter,
): Map<Int, Int> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val targetDateStr = formatter.format(targetDate)

    val hourlyMap = mutableMapOf<Int, Int>()

    val filteredSessions = when (filter) {
        is ChartFilter.ByUser -> {
            sessions.filter { session ->
                session.userId == filter.userId &&
                        session.status == "done" &&
                        session.sessionDate == targetDateStr
            }
        }
        is ChartFilter.ByGroup -> {
            val groupUserIds = filter.groupMembers
                .filter { it.groupId == filter.groupId }
                .map { it.userId }

            sessions.filter { session ->
                session.userId in groupUserIds &&
                        session.status == "done" && session.sessionDate == targetDateStr
            }
        }
    }

    Log.d("SessionsLineChart", "Found filtered sessions: $filteredSessions")
    filteredSessions.forEach { session ->
        session.endedAt?.toDate()?.let { endedDate ->
            val calendar = Calendar.getInstance()
            calendar.time = endedDate
            val startHour = calendar.get(Calendar.HOUR_OF_DAY)

            val totalMinutes = session.hourSet * 60 + session.minuteSet
            hourlyMap[startHour] = (hourlyMap[startHour] ?: 0) + totalMinutes
        }
    }
    Log.d("SessionsLineChart", "aggregateHourlyMap: $hourlyMap")
    return hourlyMap.toSortedMap()
}

// Main composable for sessions line chart - takes either userId or groupId
@Composable
fun SessionsLineChart(
    filter: ChartFilter,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
) {
    var selectedTab by remember { mutableStateOf("Day") }

    // Tab Row (Day, Week, Month, Year)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Day", "Week", "Month", "Year").forEach { label ->
            Text(
                text = label,
                fontSize = 14.sp,
                color = if (selectedTab == label) Color.Black else Color.Gray,
                modifier = Modifier
                    .clickable { selectedTab = label }
                    .padding(8.dp)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF)),
        contentAlignment = Alignment.Center
    ) {
        when (selectedTab) {
            "Day" -> StudyDayLineChart(dailyStudyHistory, studySessions, filter)
            "Week" -> StudyWeeklyLineChart(dailyStudyHistory, studySessions, filter)
            "Month" -> StudyMonthlyLineChart(dailyStudyHistory, studySessions, filter)
            "Year" -> StudyYearlyLineChart(dailyStudyHistory, studySessions, filter)
        }
    }
}

@Composable
fun StudyDayLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(filter, dailyStudyHistory, studySessions) {
        val todayDate = Date()
        val chartData = aggregateHourlyChartData(studySessions, todayDate, filter)
        val fullSeries = (1..24).map { hour -> chartData[hour] ?: 0 }
        modelProducer.runTransaction {
            lineSeries { series(fullSeries) }
        }

        Log.d("SessionsLineChart", "Daily: $fullSeries")

    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    )
}

@Composable
fun StudyWeeklyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val weeklyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.parsedDate
                val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)
                year * 100 + weekOfYear
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalIndividualMinutes / 60 .toDouble() }
                Log.d("SessionsLineChart", "Found total mins: ${entry.value.sumOf { it.totalIndividualMinutes.toDouble() }}")

            }
            .toSortedMap()

        val chartData = weeklyData.values.map { it.toFloat() }
        Log.d("SessionsLineChart", "Weekly: $chartData")

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StudyMonthlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val monthlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.parsedDate
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                year * 100 + month
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalIndividualMinutes / 60 .toDouble() }
            }
            .toSortedMap()

        val chartData = monthlyData.values.map { it.toFloat() }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StudyYearlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions
        )

        val calendar = Calendar.getInstance()
        val yearlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.parsedDate
                calendar.get(Calendar.YEAR)
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalIndividualMinutes / 60 .toDouble() }
            }
            .toSortedMap()

        val chartData = yearlyData.values.map { it.toFloat() }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Convenience functions for easier usage
@Composable
fun UserSessionsLineChart(
    userId: String,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>
) {
    SessionsLineChart(
        filter = ChartFilter.ByUser(userId),
        dailyStudyHistory = dailyStudyHistory,
        studySessions = studySessions
    )
}

@Composable
fun GroupSessionsLineChart(
    groupId: String,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    groupMembers: List<GroupMember>
) {
    SessionsLineChart(
        filter = ChartFilter.ByGroup(groupId, groupMembers),
        dailyStudyHistory = dailyStudyHistory,
        studySessions = studySessions
    )
}