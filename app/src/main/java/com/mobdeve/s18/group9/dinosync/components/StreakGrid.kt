import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StreakGrid(
    modifier: Modifier = Modifier,
    studyAct: Map<String, Float> // String-> date, Float-> totalMins
) {

    // Log.d("StreakGrid", "Found studyAct: $studyAct")
    // val daily_time = listOf(0, 1, 0, 2, 2, 0, 0), latest time at end of list, in hrs
    // val studyAct = generateTimeSampleDataMap()
    val daysToShow = 60
    val columns = 15
    val rows = 4

    // Generate the last 60 consecutive days
    val today = LocalDate.now()
    val last60Days = (0 until daysToShow).map { daysAgo ->
        today.minusDays(daysAgo.toLong()).toString()
    }.reversed() // oldest to newest

    // Map each day to its study time (0 if missing)
    val recentStudyAct = last60Days.map { date ->
        studyAct[date] ?: 0F
    }

    // Fill grid in top-down, right-to-left row-major order
    val studyActGrid = Array(rows) { Array(columns) { 0F } }

    for (i in recentStudyAct.indices) {
        val row = i / columns
        val col = columns - 1 - (i % columns) // reverse column order
        if (row < rows) {
            studyActGrid[row][col] = recentStudyAct[recentStudyAct.size - 1 - i]
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columns) {
                    val x = studyActGrid[row][col]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = getStudyActColor(x),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

fun getStudyActColor(totalStudyMins: Float): Color {
    return when (totalStudyMins) {
        0F -> Color(0x3BB6B6B6)
        in 1F..30F -> Color(0x66388E3C) // 1 to 30 mins
        in 31F..120F -> Color(0xBF388E3C) // 31 mins to 1 hours
        in 121F..180F -> Color(0xFF388E3C) // 1 hrs to 3 hrs
        in 181F..300F -> Color(0xFF2FCC1F) // 3 hrs to 5 hrs
        else -> Color(0xFF0FE016) // 5 hrs to 24 hrs
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateTimeSampleDataMap(): Map<String, Float> {
    val today = LocalDate.now()
    return (0 until 365).associate { daysAgo ->
        val date = today.minusDays(daysAgo.toLong()).toString()
        val randomMinutes = (0..300).random().toFloat() // 0 to 6 hours in minutes
        date to randomMinutes
    }
}