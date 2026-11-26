package com.example.myapplication.game

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.gold.GoldRepository
import com.example.myapplication.tabs.SettingsData
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    settings: SettingsData,
    playerName: String?,
    modifier: Modifier = Modifier,
    onGameOver: (Int) -> Unit = {},
    viewModel: GameViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tiltState = rememberTiltState()

    var fieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        viewModel.setGoldRate(GoldRepository.fetchGoldRate(context))
    }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    LaunchedEffect(uiState.isBonusActive) {
        if (uiState.isBonusActive) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.bug_scream)
            mediaPlayer?.start()
        }
    }

    LaunchedEffect(uiState.gameStarted, fieldSize) {
        if (!uiState.gameStarted || fieldSize == Size.Zero) return@LaunchedEffect

        var lastFrame = 0L
        while (!viewModel.uiState.value.isGameOver) {
            withFrameNanos { now ->
                if (lastFrame == 0L) lastFrame = now
                val deltaSec = ((now - lastFrame) / 1_000_000_000f)
                    .coerceAtMost(0.05f)
                lastFrame = now
                viewModel.update(deltaSec, fieldSize, tiltState.value)
            }
        }
    }

    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) {
            onGameOver(uiState.score)
        }
    }

    val bug1 = ImageBitmap.imageResource(R.drawable.bug1)
    val bug2 = ImageBitmap.imageResource(R.drawable.bug2)
    val bug3 = ImageBitmap.imageResource(R.drawable.bug3)
    val bug4 = ImageBitmap.imageResource(R.drawable.bug4)
    val golden = ImageBitmap.imageResource(R.drawable.golden)
    val bugBitmaps: Map<Int, ImageBitmap> = remember {
        mapOf(
            R.drawable.bug1 to bug1,
            R.drawable.bug2 to bug2,
            R.drawable.bug3 to bug3,
            R.drawable.bug4 to bug4,
            R.drawable.golden to golden
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Очки: ${uiState.score}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Время: ${uiState.timeLeft.toInt()} с",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.timeLeft < 10f) Color.Red else Color.Unspecified
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState.gameStarted, uiState.isGameOver) {
                        detectTapGestures { offset ->
                            if (uiState.gameStarted && !uiState.isGameOver) {
                                viewModel.onTap(offset)
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { fieldSize = it.toSize() }
                ) {
                    uiState.bugs.forEach { bug ->
                        val bmp = bugBitmaps[bug.imageRes] ?: bugBitmaps.values.first()
                        drawBug(bug, bmp)
                    }
                    uiState.bonusPosition?.let { pos ->
                        drawCircle(
                            color = Color.Yellow,
                            radius = 40f,
                            center = Offset(pos.x + 50f, pos.y + 50f)
                        )
                    }
                }
                if (uiState.isBonusActive) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Бонус наклона активен!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                if (!uiState.gameStarted || uiState.isGameOver) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (uiState.isGameOver) {
                                Text(
                                    text = "Игра окончена!",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ваш счёт: ${uiState.score}",
                                    fontSize = 22.sp
                                )
                                Button(onClick = { viewModel.resetGame() }) {
                                    Text("Сыграть ещё раз")
                                }
                            } else {
                                Text(
                                    text = "Жуки",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Нажимайте на жуков: +3 очка\nПромах: −1 очко",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Золотой жук (каждые 20 с): +${uiState.goldBugPoints} очков" +
                                            (uiState.goldRate?.let { " · золото ЦБ: %.2f ₽/г".format(it) }
                                                ?: " · курс недоступен"),
                                    fontSize = 14.sp,
                                    color = Color.Yellow
                                )
                                if (playerName != null) {
                                    Text(
                                        text = "Игрок: $playerName",
                                        fontSize = 16.sp,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = {
                                            viewModel.startGame(settings, fieldSize)
                                        },
                                        enabled = fieldSize != Size.Zero
                                    ) {
                                        Text("Начать игру")
                                    }
                                } else {
                                    Text(
                                        text = "Сначала выберите игрока\nво вкладке «Вход»",
                                        fontSize = 16.sp,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(onClick = {}, enabled = false) {
                                        Text("Начать игру")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBug(bug: Bug, bitmap: ImageBitmap) {
    drawImage(
        image = bitmap,
        dstOffset = IntOffset(
            bug.position.x.roundToInt(),
            bug.position.y.roundToInt()
        ),
        dstSize = IntSize(
            bug.size.roundToInt(),
            bug.size.roundToInt()
        )
    )
}