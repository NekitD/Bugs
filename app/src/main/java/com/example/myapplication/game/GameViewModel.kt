package com.example.myapplication.game

import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.myapplication.tabs.SettingsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameState: GameState? = null
    private var currentSettings: SettingsData? = null
    private var currentPlayerName: String? = null

    fun startGame(settings: SettingsData, playerName: String?, fieldSize: Size) {
        currentSettings = settings
        currentPlayerName = playerName
        gameState = GameState(settings).apply {
            start(fieldSize)
        }
        _uiState.value = GameUiState(
            score = 0,
            timeLeft = settings.roundDuration,
            isGameOver = false,
            gameStarted = true
        )
    }

    // Вызывается на каждом кадре
    fun update(deltaSec: Float, fieldSize: Size, tilt: Offset) {
        val gs = gameState ?: return
        if (gs.isGameOver) return

        gs.currentTilt = tilt
        gs.update(deltaSec, fieldSize)

        _uiState.value = _uiState.value.copy(
            score = gs.score,
            timeLeft = gs.timeLeft,
            isGameOver = gs.isGameOver,
            bugs = gs.bugs.toList(),
            bonusPosition = gs.bonusPosition,
            isBonusActive = gs.isBonusActive,
            goldRate = gs.goldRate,
            goldBugPoints = gs.goldBugPoints
        )
    }

    fun onTap(offset: Offset) {
        gameState?.onTap(offset)
    }

    fun setGoldRate(rate: Double?) {
        gameState?.goldRate = rate
        _uiState.value = _uiState.value.copy(goldRate = rate)
    }

    fun resetGame() {
        gameState = null
        _uiState.value = GameUiState()
    }
}

data class GameUiState(
    val score: Int = 0,
    val timeLeft: Float = 0f,
    val isGameOver: Boolean = false,
    val gameStarted: Boolean = false,
    val bugs: List<Bug> = emptyList(),
    val bonusPosition: Offset? = null,
    val isBonusActive: Boolean = false,
    val goldRate: Double? = null,
    val goldBugPoints: Int = 1
)