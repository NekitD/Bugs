package com.example.myapplication.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.data.PlayerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.database.sqlite.SQLiteConstraintException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationForm(
    modifier: Modifier = Modifier,
    onRegistered: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Получаем экземпляр базы данных
    val db = remember { AppDatabase.getInstance(context) }

    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Мужской") }
    var course by remember { mutableStateOf("1 курс") }
    var difficulty by remember { mutableFloatStateOf(1f) }
    var courseExpanded by remember { mutableStateOf(false) }

    var selectedDay by remember { mutableIntStateOf(1) }
    var selectedMonth by remember { mutableIntStateOf(0) }
    var selectedYear by remember { mutableIntStateOf(2000) }

    var validationError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "Магистратура", "Аспирантура")
    val genders = listOf("Мужской", "Женский")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Регистрационная форма игрока",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                validationError = null
            },
            label = { Text("ФИО") },
            isError = validationError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (validationError != null) {
            Text(
                text = validationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(text = "Пол:", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            genders.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = (gender == option),
                        onClick = { gender = option }
                    )
                ) {
                    RadioButton(
                        selected = (gender == option),
                        onClick = { gender = option }
                    )
                    Text(text = option)
                }
            }
        }

        Text(text = "Курс:", fontWeight = FontWeight.SemiBold)
        ExposedDropdownMenuBox(
            expanded = courseExpanded,
            onExpandedChange = { courseExpanded = !courseExpanded }
        ) {
            OutlinedTextField(
                value = course,
                onValueChange = {},
                readOnly = true,
                label = { Text("Выберите курс") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
            )
            ExposedDropdownMenu(
                expanded = courseExpanded,
                onDismissRequest = { courseExpanded = false }
            ) {
                courses.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            course = item
                            courseExpanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Уровень сложности: ${difficulty.toInt()}",
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = difficulty,
            onValueChange = { difficulty = it },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Дата рождения:", fontWeight = FontWeight.SemiBold)
        DataPicker(
            day = selectedDay, month = selectedMonth, year = selectedYear,
            onDateSelected = { d, m, y ->
                selectedDay = d
                selectedMonth = m
                selectedYear = y
            }
        )

        Button(
            onClick = {
                val trimmedName = fullName.trim()
                if (trimmedName.isEmpty()) {
                    validationError = "Введите ФИО"
                    return@Button
                }

                val (zodiacName, _) = getZodiacSign(selectedDay, selectedMonth + 1)
                val newPlayer = PlayerEntity(
                    name = trimmedName,
                    gender = gender,
                    course = course,
                    difficulty = difficulty.toInt(),
                    birthDate = "%02d.%02d.%04d".format(
                        selectedDay, selectedMonth + 1, selectedYear
                    ),
                    zodiacSign = zodiacName
                )

                isSaving = true
                scope.launch {
                    try {
                        val id = withContext(Dispatchers.IO) {
                            db.playerDao().insert(newPlayer)
                        }
                        fullName = ""
                        isSaving = false
                        onRegistered(id)
                    } catch (e: SQLiteConstraintException) {
                        isSaving = false
                        validationError = "Игрок с таким именем уже существует"
                    } catch (e: Exception) {
                        isSaving = false
                        validationError = "Ошибка: ${e.message}"
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Зарегистрировать")
            }
        }
    }
}