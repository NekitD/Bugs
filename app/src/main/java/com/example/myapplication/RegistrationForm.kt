package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
}

@Preview(showBackground = true)
@Composable
fun RegistrationFormPreview() {
    MyApplicationTheme {
        RegistrationForm()
    }
}