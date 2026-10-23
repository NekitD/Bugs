package com.example.myapplication.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.database.RecordRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Records(
    records: List<RecordRow>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Рекордов пока нет.\nСыграйте первую игру!",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Игрок", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Очки", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End)
                Text("Ур.", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }


        items(records) { record ->
            val index = records.indexOf(record) + 1
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "$index",
                        modifier = Modifier.width(32.dp),
                        fontWeight = FontWeight.Bold,
                        color = if (index <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )


                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = record.playerName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = dateFormatter.format(Date(record.finishedAt)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${record.score}",
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${record.difficulty}",
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}