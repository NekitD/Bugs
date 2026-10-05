package com.example.myapplication.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class SettingsData(
    val gameSpeed: Float = 1f,
    val maxBugs: Int = 5,
    val bonusInterval: Float = 15f,
    val roundDuration: Float = 60f
)


@Composable
fun Settings(
    modifier: Modifier = Modifier,
    currentSet: SettingsData,
    onSettingsChange: (SettingsData) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Настройки игры",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        SettingSlider(
            title = "Скорость игры",
            value = currentSet.gameSpeed,
            valueRange = 0.5f..3f,
            steps = 4,
            displayValue = "%.1fx".format(currentSet.gameSpeed),
            onValueChange = { onSettingsChange(currentSet.copy(gameSpeed = it)) }
        )

        SettingSlider(
            title = "Максимум жуков на экране",
            value = currentSet.maxBugs.toFloat(),
            valueRange = 1f..20f,
            steps = 18,
            displayValue = "${currentSet.maxBugs} шт.",
            onValueChange = { onSettingsChange(currentSet.copy(maxBugs = it.toInt())) }
        )

        SettingSlider(
            title = "Интервал появления бонусов",
            value = currentSet.bonusInterval,
            valueRange = 5f..60f,
            steps = 10,
            displayValue = "${currentSet.bonusInterval.toInt()} сек",
            onValueChange = { onSettingsChange(currentSet.copy(bonusInterval = it)) }
        )

        SettingSlider(
            title = "Длительность раунда",
            value = currentSet.roundDuration,
            valueRange = 30f..300f,
            steps = 8,
            displayValue = "${currentSet.roundDuration.toInt()} сек",
            onValueChange = { onSettingsChange(currentSet.copy(roundDuration = it)) }
        )
    }
}

@Composable
private fun SettingSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = displayValue,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}