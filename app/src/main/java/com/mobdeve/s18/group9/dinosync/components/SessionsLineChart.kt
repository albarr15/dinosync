
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            // Only keep histories that match userId AND have a matching completed session on the same date
            dailyStudyHistory.filter { history ->
                history.userId == filter.userId &&
                        studySessions.any { session ->
                            session.userId == filter.userId && session.status == "completed"
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
                                    member.startedAt.isNotEmpty() &&
                                    member.endedAt.isNotEmpty()
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
    groupMembers: List<GroupMember>
): Map<Int, Int> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val targetDateStr = formatter.format(targetDate)

    val hourlyMap = mutableMapOf<Int, Int>()

    when (filter) {
        is ChartFilter.ByUser -> {
            val filteredSessions = sessions.filter { session ->
                session.userId == filter.userId &&
                        session.status == "completed" &&
                        session.sessionDate == targetDateStr
            }

            filteredSessions.forEach { session ->
                session.endedAt?.toDate()?.let { endedDate ->
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
                    calendar.time = endedDate
                    val startHour = calendar.get(Calendar.HOUR_OF_DAY)

                    val totalMinutes = session.hourSet * 60 + session.minuteSet
                    hourlyMap[startHour] = (hourlyMap[startHour] ?: 0) + totalMinutes
                }
            }
        }

        is ChartFilter.ByGroup -> {
            val groupUserIds = filter.groupMembers
                .filter { it.groupId == filter.groupId }
                .map { it.userId }
                .toSet()

            val targetDateOnly = formatter.parse(targetDateStr)!!

            groupMembers.filter { member ->
                member.userId in groupUserIds &&
                        member.groupId == filter.groupId &&
                        member.startedAt.isNotEmpty() && member.endedAt.isNotEmpty()
            }.forEach { member ->
                try {
                    val start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(member.startedAt)
                    val end = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(member.endedAt)

                    if (start != null && end != null) {
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
                        cal.time = end
                        val endHour = cal.get(Calendar.HOUR_OF_DAY)

                        val memberDateStr = formatter.format(start)
                        val isSameDate = memberDateStr == targetDateStr

                        if (isSameDate) {
                            val durationInMinutes = ((end.time - start.time) / 60000).toInt()
                            hourlyMap[endHour] = (hourlyMap[endHour] ?: 0) + durationInMinutes
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HourlyChart", "Date parse error for member: ${member.userId}", e)
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

    LaunchedEffect(filter, dailyStudyHistory, studySessions) {
        val todayDate = Date()
        val chartData = aggregateHourlyChartData(studySessions, todayDate, filter, groupMembers)
        val fullSeries = (0..23).map { hour -> chartData[hour] ?: 0 }
        modelProducer.runTransaction {
            lineSeries { series(fullSeries) }
        }

        //Log.d("SessionsColumnChart", "Daily: $fullSeries")

    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
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
    val bottomAxisValueFormatter = CartesianValueFormatter { context, value, _ ->
        val index = value.toInt()
        val label = daysOfWeek[index]
        label
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions,
            groupMembers
        )

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
        val today = calendar.time

        // Set calendar to the start of the week (Sunday)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek) // Usually Sunday

        val startOfWeek = calendar.time

        // End of week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time


        // Filter only this week's entries
        val thisWeekHistory = filteredHistory.filter { daily ->
            val currentDate = dateFormatter.parse(daily.date)

            currentDate.after(startOfWeek) && currentDate.before(endOfWeek) ||
                    currentDate == startOfWeek || currentDate == endOfWeek
        }
        val weeklyData = thisWeekHistory
            .groupBy { daily ->
                calendar.setTime(daily.parsedDate)
                val dow = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Sat
                // Log.d("StudyWeeklyLineChart", "Grouping ${daily.parsedDate} into dayIndex = $dow")
                dow
            }
            .mapValues { entry ->
                val totalMins = entry.value.map { it.totalIndividualMinutes }.sum()
                // Log.d("StudyWeeklyLineChart", "Day ${entry.key} total minutes = $totalMins")
                totalMins
            }
            .toSortedMap()

        val fullWeekMap = (1..7).associateWith { 0f }.toMutableMap()
        weeklyData.forEach { (dayIndex, value) ->
            fullWeekMap[dayIndex] = value.toFloat()
        }

        val chartData = fullWeekMap.toSortedMap().values.toList()
        // Log.d("SessionsLineChart", "Weekly full data: $chartData")

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
    val bottomAxisValueFormatter = CartesianValueFormatter { context, value, _ ->
        val index = value.toInt()
        val label = months[index]
        label
    }

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions,
            groupMembers
        )

        val calendar = Calendar.getInstance()
        val monthlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.parsedDate
                val month = calendar.get(Calendar.MONTH)
                month
            }
            .mapValues { entry ->
                val sum = entry.value.sumOf { it.totalIndividualMinutes.toDouble() }
                //Log.d("StudyMonthlyLineChart", "Key: ${entry.key}, Sum of minutes: $sum")
                sum
            }
            .toSortedMap()

        val fullMonthMap = (0..11).associateWith { 0f }.toMutableMap()
        monthlyData.forEach { (monthIndex, value) ->
            fullMonthMap[monthIndex] = value.toFloat()
        }

        val chartData = fullMonthMap.toSortedMap().values.toList()

        // val chartData = monthlyData.values.map { it.toFloat() }
        //og.d("StudyMonthlyLineChart", "Final chartData: $chartData")

        modelProducer.runTransaction {
            //Log.d("StudyMonthlyLineChart", "Updating chart model with data")
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
fun StudyYearlyLineChart(
    dailyStudyHistory: List<DailyStudyHistory>,
    studySessions: List<StudySession>,
    filter: ChartFilter,
    groupMembers: List<GroupMember>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    val years = (currentYear - 4..currentYear).map { it.toString() }
    val bottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        val index = value.toInt()
        years.getOrNull(index) ?: ""
    }

    LaunchedEffect(dailyStudyHistory, studySessions, filter) {
        val filteredHistory = filterDailyHistoryByFilter(
            filter,
            dailyStudyHistory,
            studySessions,
            groupMembers
        )

        val yearlyData = filteredHistory
            .groupBy { daily ->
                calendar.time = daily.parsedDate
                calendar.get(Calendar.YEAR)
            }
            .mapValues { entry ->
                entry.value.sumOf { it.totalIndividualMinutes.toDouble() / 60}
            }
            .toSortedMap()

        // shows the past five to current years
        val fullYearMap = ((currentYear - 4)..currentYear).associateWith { 0f }.toMutableMap()
        yearlyData.forEach { (monthIndex, value) ->
            fullYearMap[monthIndex] = value.toFloat()
        }

        val chartData = fullYearMap.toSortedMap().values.toList()

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