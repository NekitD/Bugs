package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.game.GameScreen
import com.example.myapplication.tabs.Authors
import com.example.myapplication.tabs.RegistrationForm
import com.example.myapplication.tabs.Rules
import com.example.myapplication.tabs.Settings
import com.example.myapplication.tabs.SettingsData
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.mutableIntStateOf
import com.example.myapplication.tabs.PlayerSelect
import com.example.myapplication.data.PlayerEntity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val tabs = listOf("Регистрация", "Вход", "Жуки", "Рекорды" , "Правила", "Авторы", "Настройки")

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    var currentPlayerId by remember { mutableStateOf<Long?>(null) }
    var settings by remember { mutableStateOf(SettingsData()) }
    var players by remember { mutableStateOf<List<PlayerEntity>>(emptyList()) }
    var refreshKey by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> RegistrationForm(
                        onRegistered = { id ->
                            currentPlayerId = id
                            refreshKey++
                            scope.launch { pagerState.animateScrollToPage(1) }
                        }
                    )
                    1 -> PlayerSelect(
                        players = players,
                        currentPlayerId = currentPlayerId,
                        onSelect = { id ->
                            currentPlayerId = id
                            scope.launch { pagerState.animateScrollToPage(2) }
                        },
                        onGoToRegistration = {
                            scope.launch { pagerState.animateScrollToPage(0) }
                        }
                    )
                    2 -> GameScreen(settings = settings, onGameOver = {})
                    4 -> Rules()
                    5 -> Authors()
                    6 -> Settings(
                        currentSet = settings,
                        onSettingsChange = { settings = it }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello World! From $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

