
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
import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.mobdeve.s18.group9.dinosync.model.StudySession
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.text.SimpleDateFormat

// Sealed class to represent either user or group filtering
sealed class ChartFilter {
    data class ByUser(val userId: String) : ChartFilter()
    data class ByGroup(val groupId: String, val groupMembers: List<GroupMember>) : ChartFilter()
}

// Filtering logic for daily history based on chart filter
fun filterDailyHistoryByFilter(
    filter: ChartFilter,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    groupMembers: List<GroupMember>
): List<DailyStudyHistory> {
    return when (filter) {
        is ChartFilter.ByUser -> {
            dailyStudyHistory.filter { history ->
                history.userId == filter.userId &&
                        studySessions.any { session ->
                            session.userId == filter.userId &&
                                    session.status == "completed" &&
                                    session.sessionDate == history.date
                        }
            }
        }
        is ChartFilter.ByGroup -> {
            val userIdsInGroup = filter.groupMembers
                .filter { it.groupId == filter.groupId }
                .map { it.userId }
                .toSet()

            dailyStudyHistory.filter { history ->
                history.userId in userIdsInGroup &&
                        groupMembers.any { member ->
                            member.userId == history.userId &&
                                    member.groupId == filter.groupId &&
                                    member.startedAt.isNotEmpty() // && member.endedAt.isNotEmpty()
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
    groupMembers: List<GroupMember>,
    dailyStudyHistory: List<DailyStudyHistory>
): Map<Int, Int> {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val targetDateStr = dateFormatter.format(targetDate)

    val hourlyMap = mutableMapOf<Int, Int>()

    when (filter) {
        is ChartFilter.ByUser -> {
            sessions.filter { session ->
                session.userId == filter.userId &&
                        session.status == "completed" &&
                        session.sessionDate == targetDateStr
            }.forEach { session ->
                session.endedAt?.toDate()?.let { endedDate ->
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
                    cal.time = endedDate
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    val totalMinutes = session.hourSet * 60 + session.minuteSet
                    hourlyMap[hour] = (hourlyMap[hour] ?: 0) + totalMinutes
                }
            }
        }

        is ChartFilter.ByGroup -> {
            val groupUserIds = groupMembers
                .filter { it.groupId == filter.groupId }
                .map { it.userId }
                .toSet()

            dailyStudyHistory
                .filter { it.userId in groupUserIds && it.date == targetDateStr }
                .forEach { history ->
                    val hasSessionThatDay = groupMembers.any { member ->
                        member.userId == history.userId &&
                                !member.startedAt.isNullOrBlank() &&
                                runCatching {
                                    val startDate = dateTimeFormatter.parse(member.startedAt)
                                    startDate != null && dateFormatter.format(startDate) == history.date
                                }.getOrDefault(false)
                    }

                    if (hasSessionThatDay) {
                        val defaultHour = 0 // group history is not hourly-specific
                        hourlyMap[defaultHour] = (hourlyMap[defaultHour] ?: 0) +
                                history.totalGroupStudyMinutes.toInt()
                    }
                }

            groupMembers
                .filter { it.userId in groupUserIds && it.startedAt.isNotBlank() && !it.endedAt.isNullOrBlank() }
                .forEach { member ->
                    runCatching {
                        val start = dateTimeFormatter.parse(member.startedAt)
                        val end = member.endedAt?.let { dateTimeFormatter.parse(it) }

                        if (start != null && end != null &&
                            dateFormatter.format(start) == targetDateStr
                        ) {
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
                            cal.time = end
                            val hour = cal.get(Calendar.HOUR_OF_DAY)
                            val durationMinutes = ((end.time - start.time) / 60000).toInt()
                            hourlyMap[hour] = (hourlyMap[hour] ?: 0) + durationMinutes
                        }
                    }
                }
        }
    }

    return hourlyMap.toSortedMap()
}


// Main composable for sessions line chart - takes either userId or groupId
@Composable
fun SessionsLineChart(
    filter: ChartFilter,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    groupMembers: List<GroupMember>
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
            .background(Color(0xFFF3F3F3)),
        contentAlignment = Alignment.Center
    ) {
        when (selectedTab) {
            "Day" -> StudyDayLineChart(dailyStudyHistory, studySessions, filter, groupMembers)
            "Week" -> StudyWeeklyLineChart(dailyStudyHistory, studySessions, filter, groupMembers)
            "Month" -> StudyMonthlyLineChart(dailyStudyHistory, studySessions, filter, groupMembers)
            "Year" -> StudyYearlyLineChart(dailyStudyHistory, studySessions, filter, groupMembers)
        }
    }
}

@Composable
fun StudyDayLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter,
    groupMembers: List<GroupMember>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val hrsOfDay = listOf("12AM", "1AM", "2AM", "3AM", "4AM", "5AM", "6AM", "7AM", "8AM", "9AM", "10AM", "11AM", "12PM",
        "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM", "8PM", "9PM", "10PM", "11PM")
    val bottomAxisValueFormatter = CartesianValueFormatter { context, value, _ ->
        val index = value.toInt()
        val label = hrsOfDay[index]
        label
    }

    LaunchedEffect(filter, dailyStudyHistory, studySessions, groupMembers) {
        val todayDate = Date()
        val chartData = aggregateHourlyChartData(studySessions, todayDate, filter, groupMembers, dailyStudyHistory)
        Log.d("StudyDayLineChart", "Hourly data: $chartData")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormatter.format(todayDate)
        Log.d("StudyDayLineChart", "Today date string: $todayStr")
        val fullSeries = (0..23).map { hour -> chartData[hour]?.toFloat() ?: 0f }

        modelProducer.runTransaction {
            lineSeries { series(fullSeries) }
        }
    }



    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter
            ),
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
    )
}

@Composable
fun StudyWeeklyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter,
    groupMembers: List<GroupMember>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val bottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        val index = value.toInt().coerceIn(0, daysOfWeek.lastIndex)
        daysOfWeek[index]
    }

    val timeZone = TimeZone.getTimeZone("Asia/Manila")
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        this.timeZone = timeZone
    }
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        this.timeZone = timeZone
    }


    LaunchedEffect(dailyStudyHistory, studySessions, filter, groupMembers) {
        // Start and end of this week
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek) // Sunday
        }
        val startOfWeek = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time

        // Prepare a mutable map for Sunday (1) to Saturday (7)
        val weeklyMap = (1..7).associateWith { 0f }.toMutableMap()

        when (filter) {
            is ChartFilter.ByGroup -> {
                val groupUserIds = groupMembers
                    .filter { it.groupId == filter.groupId }
                    .map { it.userId }
                    .toSet()

                dailyStudyHistory
                    .filter { it.userId in groupUserIds }
                    .filter { dateFormatter.parse(it.date) in startOfWeek..endOfWeek }
                    .forEach { history ->
                        val hasSessionThatDay = groupMembers.any { member ->
                            val matches = member.userId == history.userId &&
                                    !member.startedAt.isNullOrBlank() &&
                                    runCatching {
                                        val parsed = dateTimeFormatter.parse(member.startedAt)
                                        parsed != null && dateFormatter.format(parsed) == history.date
                                    }.getOrDefault(false)

                            if (matches) {
                                Log.d("WeeklyChart", "member.startedAt=${member.startedAt}, parsed=${dateFormatter.format(dateTimeFormatter.parse(member.startedAt)!!)}, history.date=${history.date}")
                            }
                            matches
                        }

                        if (hasSessionThatDay) {
                            val dayIndex = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
                                time = dateFormatter.parse(history.date)!!
                            }.get(Calendar.DAY_OF_WEEK)

                            weeklyMap[dayIndex] = (weeklyMap[dayIndex] ?: 0f) + history.totalGroupStudyMinutes.toFloat()
                        }

                    }
            }


            is ChartFilter.ByUser -> {
                dailyStudyHistory
                    .filter { it.userId == filter.userId }
                    .filter { dateFormatter.parse(it.date) in startOfWeek..endOfWeek }
                    .forEach { history ->
                        val dayIndex = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
                            time = dateFormatter.parse(history.date)!!
                        }.get(Calendar.DAY_OF_WEEK)
                        weeklyMap[dayIndex] = (weeklyMap[dayIndex] ?: 0f) +
                                history.totalIndividualMinutes.toFloat()
                    }
            }
        }

        val chartData = weeklyMap.toSortedMap().values.toList()
        Log.d("StudyWeeklyLineChart", "Weekly full data: $chartData")

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = bottomAxisValueFormatter
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun StudyMonthlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter,
    groupMembers: List<GroupMember>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val bottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        months.getOrNull(value.toInt()) ?: ""
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(dailyStudyHistory, studySessions, filter, groupMembers) {
        val monthlyMap = (0..11).associateWith { 0f }.toMutableMap()

        when (filter) {
            is ChartFilter.ByGroup -> {
                val groupUserIds = groupMembers
                    .filter { it.groupId == filter.groupId }
                    .map { it.userId }
                    .toSet()

                dailyStudyHistory
                    .filter { it.userId in groupUserIds }
                    .forEach { history ->
                        val hasSessionThatDay = groupMembers.any { member ->
                            member.userId == history.userId &&
                                    !member.startedAt.isNullOrBlank() &&
                                    dateFormatter.format(dateTimeFormatter.parse(member.startedAt)!!) == history.date
                        }

                        if (hasSessionThatDay) {
                            val monthIndex = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
                                time = dateFormatter.parse(history.date)!!
                            }.get(Calendar.MONTH)

                            monthlyMap[monthIndex] = (monthlyMap[monthIndex] ?: 0f) + history.totalGroupStudyMinutes.toFloat()
                        }
                    }
            }

            is ChartFilter.ByUser -> {
                dailyStudyHistory
                    .filter { it.userId == filter.userId }
                    .forEach { history ->
                        val monthIndex = Calendar.getInstance().apply {
                            time = dateFormatter.parse(history.date)!!
                        }.get(Calendar.MONTH)

                        monthlyMap[monthIndex] = (monthlyMap[monthIndex] ?: 0f) + history.totalIndividualMinutes.toFloat()
                    }
            }
        }

        modelProducer.runTransaction {
            lineSeries { series(monthlyMap.toSortedMap().values.toList()) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = bottomAxisValueFormatter
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun StudyYearlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter,
    groupMembers: List<GroupMember>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
    val currentYear = calendar.get(Calendar.YEAR)

    val years = (currentYear - 4..currentYear).map { it.toString() }
    val bottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        years.getOrNull(value.toInt()) ?: ""
    }

    // formatters (declare once)
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    LaunchedEffect(dailyStudyHistory, studySessions, filter, groupMembers) {
        // initialize year -> minutes map for the last 5 years
        val yearlyMap = ((currentYear - 4)..currentYear).associateWith { 0f }.toMutableMap()

        when (filter) {
            is ChartFilter.ByUser -> {
                dailyStudyHistory
                    .filter { it.userId == filter.userId }
                    .forEach { history ->
                        val parsed = history.parsedDate ?: return@forEach
                        val year = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply { time = parsed }
                            .get(Calendar.YEAR)
                        if (year in (currentYear - 4)..currentYear) {
                            yearlyMap[year] = (yearlyMap[year] ?: 0f) + history.totalIndividualMinutes.toFloat()
                        }
                    }
            }

            is ChartFilter.ByGroup -> {
                // members who ever belonged to this group
                val groupUserIds = groupMembers
                    .filter { it.groupId == filter.groupId }
                    .map { it.userId }
                    .toSet()

                // For each daily history of those users, include it ONLY if there's a groupMember session that day
                dailyStudyHistory
                    .filter { it.userId in groupUserIds }
                    .forEach { history ->
                        // safe check: did the user have a group session (GroupMember) that started on history.date?
                        val hasSessionThatDay = groupMembers.any { member ->
                            member.userId == history.userId &&
                                    !member.startedAt.isNullOrBlank() &&
                                    runCatching {
                                        val startDate = dateTimeFormatter.parse(member.startedAt)
                                        startDate != null && dateFormatter.format(startDate) == history.date
                                    }.getOrDefault(false)
                        }

                        if (hasSessionThatDay) {
                            val parsed = history.parsedDate ?: return@forEach
                            val year = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply { time = parsed }
                                .get(Calendar.YEAR)
                            if (year in (currentYear - 4)..currentYear) {
                                yearlyMap[year] = (yearlyMap[year] ?: 0f) + history.totalGroupStudyMinutes.toFloat()
                            }
                        }
                    }
            }
        }

        // produce chartData in ascending year order (currentYear-4 .. currentYear)
        val chartData = ((currentYear - 4)..currentYear).map { yearlyMap[it] ?: 0f }

        modelProducer.runTransaction {
            lineSeries { series(chartData) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxSize()
        )
    }
}


// Convenience functions for easier usage
@Composable
fun UserSessionsLineChart(
    userId: String,
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    groupMembers: List<GroupMember>
) {
    SessionsLineChart(
        filter = ChartFilter.ByUser(userId),
        dailyStudyHistory = dailyStudyHistory,
        studySessions = studySessions,
        groupMembers = groupMembers
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
        studySessions = studySessions,
        groupMembers = groupMembers
    )
}