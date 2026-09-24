package com.example.myapplication

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPicker(day: Int, month: Int, year: Int, onDateSelected: (day: Int, month: Int, year: Int) -> Unit,
               modifier: Modifier = Modifier)
{
    var showDialog by remember {mutableStateOf(false)}
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(day, month, year){
            Calendar.getInstance().apply() {set(year, month, day)}.timeInMillis})

    OutlinedButton(onClick = {showDialog = true}, modifier = modifier.fillMaxWidth()){
        Text("Выбрать дату: %02d.%02d.%04d".format(day, month + 1, year))
    }

    if(showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            onDateSelected(
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.YEAR)
                            )
                        }
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}