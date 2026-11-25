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

    var goldRate: Double? = null

    val goldBugPoints: Int
        get() = ((goldRate ?: 0.0) / 100.0).toInt().coerceAtLeast(1)

    private val goldInterval = 20f
    private var goldTimer: Float = goldInterval

    var currentTilt: Offset = Offset.Zero

    fun start(bounds: Size) {
        repeat(settings.maxBugs / 2 + 1) {
            spawnBug(bounds, Random.nextInt(4))
        }
        bonusTimer = 15f
        goldTimer = goldInterval
    }

    fun onTap(tap: Offset): Boolean {
        if (isGameOver) return false

        bonusPosition?.let { pos ->
            val bonusSize = 100f
            if (tap.x in pos.x..(pos.x + bonusSize) && tap.y in pos.y..(pos.y + bonusSize)) {
                isBonusActive = true
                bonusActiveTimer = 5f
                bonusPosition = null
                return true
            }
        }

        val hit = bugs.firstOrNull { it.alive && it.isGold && it.contains(tap) }
            ?: bugs.firstOrNull { it.alive && it.contains(tap) }
        if (hit != null) {
            hit.alive = false
            score += if (hit.isGold) goldBugPoints else 3
            return true
        } else {
            if (score > 0) score -= 1
            return false
        }
    }
    private var wasBonusActive = false

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

        if (wasBonusActive && !isBonusActive) {
            bugs.forEach { bug ->
                bug.velocity = bug.baseVelocity
            }
        }
        wasBonusActive = isBonusActive

        goldTimer -= dsec
        if (goldTimer <= 0f) {
            spawnGoldBug(bounds)
            goldTimer = goldInterval
        }

        spawnTimer -= dsec
        val alive = bugs.count { it.alive && !it.isGold }
        if (spawnTimer <= 0f && alive < settings.maxBugs) {
            spawnBug(bounds, Random.nextInt(4))
            spawnTimer = 1f / settings.gameSpeed
        }

        bugs.forEach { bug ->
            if (isBonusActive) {
                val tiltAccel = currentTilt * 3000f * dsec
                bug.velocity = Offset(
                    bug.velocity.x + tiltAccel.x,
                    bug.velocity.y + tiltAccel.y
                )
                val friction = 0.98f
                bug.velocity = Offset(
                    bug.velocity.x * friction,
                    bug.velocity.y * friction
                )
                val maxSpeed = 1500f
                val speed = kotlin.math.sqrt(
                    bug.velocity.x * bug.velocity.x + bug.velocity.y * bug.velocity.y
                )
                if (speed > maxSpeed) {
                    val k = maxSpeed / speed
                    bug.velocity = Offset(bug.velocity.x * k, bug.velocity.y * k)
                }
            }
            bug.update(dsec, bounds, if (isBonusActive) 1f else settings.gameSpeed)
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
        val initialVelocity = Offset(cos(angle) * speed, sin(angle) * speed)

        bugs += Bug(
            nextId++,
            Offset(x, y),
            initialVelocity,
            initialVelocity,
            bugSize,
            image
        )
    }

    private fun spawnGoldBug(bounds: Size) {
        // Старый золотой (если его не поймали) заменяется новым — они не копятся
        bugs.removeAll { it.isGold }

        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        val speed = 300f
        val bugSize = 120f
        val x = Random.nextFloat() * (bounds.width - bugSize)
        val y = Random.nextFloat() * (bounds.height - bugSize)
        val v = Offset(cos(angle) * speed, sin(angle) * speed)

        bugs += Bug(
            nextId++,
            Offset(x, y),
            v,
            v,
            bugSize,
            com.example.myapplication.R.drawable.golden,
            isGold = true
        )
    }
}