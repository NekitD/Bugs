package com.example.myapplication.game

import com.example.myapplication.tabs.SettingsData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameState(private val settings: SettingsData) {

    val bugs = mutableListOf<Bug>()

    var bonusPosition: Offset? = null
    var isBonusActive: Boolean = false
    private var bonusTimer: Float = 15f
    private var bonusActiveTimer: Float = 0f

    var currentTilt: Offset = Offset.Zero

    fun start(bounds: Size) {
        repeat(settings.maxBugs / 2 + 1) {
            spawnBug(bounds, Random.nextInt(4))
        }
        bonusTimer = 15f
    }

    fun onTap(tap: Offset): Boolean {
        if (isGameOver) return false

        bonusPosition?.let { pos ->
            val bonusSize = 100f
            if (tap.x in pos.x..(pos.x + bonusSize) && tap.y in pos.y..(pos.y + bonusSize)) {
                // Активируем бонус
                isBonusActive = true
                bonusActiveTimer = 5f
                bonusPosition = null
                return true
            }
        }

        val hit = bugs.firstOrNull { it.alive && it.contains(tap) }
        if (hit != null) {
            hit.alive = false
            score += 3
            return true
        } else {
            if (score > 0) score -= 1
            return false
        }
    }

    fun update(dsec: Float, bounds: Size) {
        if (isGameOver) return

        timeLeft -= dsec
        if (timeLeft <= 0f) {
            isGameOver = true
            return
        }

        if (isBonusActive) {
            bonusActiveTimer -= dsec
            if (bonusActiveTimer <= 0f) {
                isBonusActive = false
            }
        } else {

            if (bonusPosition == null) {
                bonusTimer -= dsec
                if (bonusTimer <= 0f) {
                    bonusPosition = Offset(
                        Random.nextFloat() * (bounds.width - 100f),
                        Random.nextFloat() * (bounds.height - 100f)
                    )
                    bonusTimer = 15f
                }
            }
        }

        spawnTimer -= dsec
        val alive = bugs.count { it.alive }
        if (spawnTimer <= 0f && alive < settings.maxBugs) {
            spawnBug(bounds, Random.nextInt(4))
            spawnTimer = 1f / settings.gameSpeed
        }

        bugs.forEach { bug ->
            if (isBonusActive) {
                // Режим наклона: жуки скатываются в сторону наклона
                val tiltForce = currentTilt * 400f * dsec // сила наклона
                bug.velocity = Offset(
                    (bug.velocity.x + tiltForce.x).coerceIn(-500f, 500f),
                    (bug.velocity.y + tiltForce.y).coerceIn(-500f, 500f)
                )
            }
            bug.update(dsec, bounds, settings.gameSpeed)
        }
        bugs.removeAll { !it.alive }
    }

    var score = 0
    var timeLeft = settings.roundDuration
    var isGameOver = false
    var nextId: Long = 0
    var spawnTimer = 0f

    var commonBugImages = listOf(
        com.example.myapplication.R.drawable.bug1,
        com.example.myapplication.R.drawable.bug2,
        com.example.myapplication.R.drawable.bug3,
        com.example.myapplication.R.drawable.bug4
    )

    private fun spawnBug(bounds: Size, type: Int) {
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        var speed: Float = 180f
        var bugSize = 150f
        var image = commonBugImages[0]
        when (type) {
            1 -> { speed = 200f; bugSize = 130f; image = commonBugImages[1] }
            2 -> { speed = 220f; bugSize = 110f; image = commonBugImages[2] }
            3 -> { speed = 240f; bugSize = 100f; image = commonBugImages[3] }
        }

        val x = Random.nextFloat() * (bounds.width - bugSize)
        val y = Random.nextFloat() * (bounds.height - bugSize)

        bugs += Bug(nextId++, Offset(x, y),
            Offset(cos(angle) * speed, sin(angle) * speed),
            bugSize, image)
    }
}