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
    private var goldRate: Double? = null

    fun startGame(settings: SettingsData, playerName: String?, fieldSize: Size) {
        gameState = GameState(settings).apply {
            this.goldRate = this@GameViewModel.goldRate
            start(fieldSize)
        }
        _uiState.value = GameUiState(
            score = 0,
            timeLeft = settings.roundDuration,
            isGameOver = false,
            gameStarted = true,
            goldRate = goldRate,
            goldBugPoints = gameState!!.goldBugPoints
        )
    }

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
            goldBugPoints = gs.goldBugPoints
        )
    }

    fun setGoldRate(rate: Double?) {
        goldRate = rate
        gameState?.goldRate = rate   // если игра уже идёт — обновим и там
        _uiState.value = _uiState.value.copy(
            goldRate = rate,
            goldBugPoints = gameState?.goldBugPoints ?: _uiState.value.goldBugPoints
        )
    }

    fun onTap(offset: Offset) {
        gameState?.onTap(offset)
    }

    fun resetGame() {
        gameState = null
        _uiState.value = GameUiState(goldRate = goldRate)
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