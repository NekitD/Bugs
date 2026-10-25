package com.example.myapplication.game

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.myapplication.tabs.SettingsData
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.example.myapplication.R
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign

@Composable
fun GameScreen(
    settings: SettingsData,
    playerName: String?,
    modifier: Modifier = Modifier,
    onGameOver: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val gameState = remember(settings) { GameState(settings) }
    var gameStarted by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf<Int?>(null) }

    val tiltState = rememberTiltState()
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }


    val bug1Bitmap = ImageBitmap.imageResource(R.drawable.bug1)
    val bug2Bitmap = ImageBitmap.imageResource(R.drawable.bug2)
    val bug3Bitmap = ImageBitmap.imageResource(R.drawable.bug3)
    val bug4Bitmap = ImageBitmap.imageResource(R.drawable.bug4)
    val bugBitmaps: Map<Int, ImageBitmap> = remember {
        mapOf(
            R.drawable.bug1 to bug1Bitmap,
            R.drawable.bug2 to bug2Bitmap,
            R.drawable.bug3 to bug3Bitmap,
            R.drawable.bug4 to bug4Bitmap
        )
    }

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableFloatStateOf(settings.roundDuration) }
    var bugsSnapshot by remember { mutableStateOf<List<Bug>>(emptyList()) }
    var tick by remember { mutableIntStateOf(0) }
    var fieldSize by remember { mutableStateOf(Size.Zero) }


    LaunchedEffect(gameStarted) {
        if (!gameStarted) return@LaunchedEffect

        while (fieldSize == Size.Zero) {
            withFrameNanos { }
        }

        gameState.start(fieldSize)

        score = 0
        timeLeft = settings.roundDuration

        var lastFrame = 0L
        var wasBonusActive = false

        while (!gameState.isGameOver) {
            withFrameNanos { now ->
                if (lastFrame == 0L) lastFrame = now
                val deltaSec = ((now - lastFrame) / 1_000_000_000f)
                    .coerceAtMost(0.05f)
                lastFrame = now

                gameState.currentTilt = tiltState.value

                if (gameState.isBonusActive && !wasBonusActive) {
                    // Играем звук
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(context, R.raw.bug_scream)
                    mediaPlayer?.start()
                }
                wasBonusActive = gameState.isBonusActive


                gameState.update(deltaSec, fieldSize)

                score = gameState.score
                timeLeft = gameState.timeLeft
                bugsSnapshot = gameState.bugs.toList()
                tick++
            }
        }

        finalScore = gameState.score
        onGameOver(gameState.score)
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
                    text = "Очки: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Время: ${timeLeft.toInt()} с",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (timeLeft < 10f) Color.Red else Color.Unspecified
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (gameStarted && !gameState.isGameOver) {
                                gameState.onTap(offset)
                                score = gameState.score
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
                    @Suppress("UNUSED_EXPRESSION")
                    tick
                    bugsSnapshot.forEach { bug ->
                        val bmp = bugBitmaps[bug.imageRes] ?: bugBitmaps.values.first()
                        drawBug(bug, bmp)
                    }
                    gameState.bonusPosition?.let { pos ->
                        drawCircle(
                            color = Color.Yellow,
                            radius = 40f,
                            center = Offset(pos.x + 50f, pos.y + 50f)
                        )
                    }
                }
                if (gameState.isBonusActive) {
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
                if (!gameStarted || finalScore != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (finalScore != null) {
                                Text(
                                    text = "Игра окончена!",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ваш счёт: $finalScore",
                                    fontSize = 22.sp
                                )
                                Button(onClick = {
                                    finalScore = null
                                    gameStarted = false
                                }) {
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
                                if (playerName != null) {
                                    // Игрок выбран — можно играть
                                    Text(
                                        text = "Игрок: $playerName",
                                        fontSize = 16.sp,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(onClick = { gameStarted = true }) {
                                        Text("Начать игру")
                                    }
                                } else {
                                    // Игрок не выбран — показываем предупреждение
                                    Text(
                                        text = "Сначала выберите игрока\nво вкладке «Вход»",
                                        fontSize = 16.sp,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { },
                                        enabled = false
                                    ) {
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