package com.example.myapplication.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.PlayerEntity


@Composable
fun PlayerSelect(
    players: List<PlayerEntity>,
    currentPlayerId: Long?,
    onSelect: (Long) -> Unit,
    onGoToRegistration: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (players.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Нет зарегистрированных игроков",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Сначала создайте игрока во вкладке «Регистрация».",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onGoToRegistration) {
                    Text("Перейти к регистрации")
                }
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        currentPlayerId?.let { id ->
            val current = players.firstOrNull { it.id == id }
            current?.let {
                Text(
                    text = "Текущий игрок: ${it.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(players, key = { it.id }) { player ->
                val isSelected = player.id == currentPlayerId

                Card(
                    onClick = { onSelect(player.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (isSelected)
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    else CardDefaults.cardColors()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${player.course} • ${player.gender} • сложность ${player.difficulty}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (isSelected) {
                            Text(
                                text = "Выбран",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}