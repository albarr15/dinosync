package com.mobdeve.s18.group9.dinosync.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

fun formatFocusTime(percentage: Double, totalTimeSeconds: Int): String {
    val totalSeconds = (percentage / 100) * totalTimeSeconds
    val totalMinutes = (totalSeconds / 60).toInt()

    return if (totalMinutes >= 60) {
        val hours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        if (remainingMinutes > 0) {
            "(${"%.1f".format(hours + remainingMinutes / 60.0)} hrs)"
        } else {
            "($hours hrs)"
        }
    } else {
        "($totalMinutes mins)"
    }
}

@Composable
fun PieStats(data: List<Pie>, totalTime: Int) {
    Row (modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        ){
        PieChart(
            modifier = Modifier.size(150.dp)
                .padding(5.dp)
                .padding(end = 15.dp),
            data = data,
            style = Pie.Style.Fill
        )

        Column{
            for (pie in data) {
                Row(modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(7.dp), onDraw = {
                        drawCircle(color = pie.color)
                    })

                    Text(text = pie.label.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 5.dp) )

                    Text(text = pie.data.toString() + "%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 5.dp) )

                    Text(text = formatFocusTime(pie.data, totalTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 1.dp) )
                }
            }
        }
    }
}