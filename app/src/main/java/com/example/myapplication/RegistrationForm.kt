package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationForm(modifier: Modifier = Modifier) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Мужской") }
    var course by remember { mutableStateOf("1 курс") }
    var difficulty by remember { mutableFloatStateOf(1f) }
    var courseExpanded by remember { mutableStateOf(false) }

    var selectedDay by remember { mutableIntStateOf(1) }
    var selectedMonth by remember { mutableIntStateOf(0) }
    var selectedYear by remember { mutableIntStateOf(2000) }

    var playerData by remember { mutableStateOf<PlayerData?>(null) }

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
            onValueChange = { fullName = it },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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
        DataPicker(day = selectedDay, month = selectedMonth, year = selectedYear,
            onDateSelected = { d, m, y ->
                selectedDay = d
                selectedMonth = m
                selectedYear = y
            }
        )

        Button(
            onClick = {
                val (zodiacName, zodiacRes) = getZodiacSign(selectedDay, selectedMonth + 1)
                playerData = PlayerData(
                    fullName = fullName,
                    gender = gender,
                    course = course,
                    difficulty = difficulty.toInt(),
                    birthDate = "%02d.%02d.%04d".format(selectedDay, selectedMonth + 1, selectedYear),
                    zodiacSign = zodiacName,
                    zodiacSym = zodiacRes
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ){
            Text("Зарегистрировать")
        }

        playerData?.let{
            data -> Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ){
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ){
                    Text("Данные игрока:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "ФИО: ${data.fullName}")
                    Text(text = "Пол: ${data.gender}")
                    Text(text = "Курс: ${data.course}")
                    Text(text = "Уровень сложности: ${data.difficulty}")
                    Text(text = "Дата рождения: ${data.birthDate}")
                    Text(text = "Знак зодиака: ${data.zodiacSign}", fontWeight = FontWeight.SemiBold)
                    if(data.zodiacSym.isNotEmpty()){
                        Text(text = data.zodiacSym, fontSize = 72.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationFormPreview() {
    MyApplicationTheme {
        RegistrationForm()
    }
}
