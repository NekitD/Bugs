package com.example.myapplication.game

import com.example.myapplication.tabs.SettingsData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameState(private val settings: SettingsData){

    val bugs = mutableListOf<Bug>()
    var tilt = Offset.Zero

    fun start(bounds: Size){
        repeat(settings.maxBugs / 2 + 1){
            spawnBug(bounds, false)
        }
    }

    fun onTap(tap: Offset): Boolean{
        if(isGameOver) return false
        val hit = bugs.firstOrNull{it.alive && it.contains(tap)}
        if(hit != null){
            hit.alive = false
            score += 3
            return true
        } else {
            score -= 1
            return false
        }

    }

    fun update(speed: Float, bounds: Size){
        if(isGameOver) return

        timeLeft -= speed
        if(timeLeft <= 0f){
            isGameOver = true
            return
        }

        if(bonusActive){
            bonusTimeLeft -= speed
            if(bonusTimeLeft <= 0f){
                bonusActive = false

            }
        }

        spawnTimer -= speed
        val aliveCommon = bugs.count{it.alive && !it.isGolden}
        if (spawnTimer <= 0f && aliveCommon < settings.maxBugs){
            spawnBug(bounds, false)
            spawnTimer = 1f / settings.gameSpeed
        }

        bonusTimer -= speed
        if(bonusTimer <= 0f){
            bonusActive = true
            bonusTimeLeft = 10f
            bonusTimer = settings.bonusInterval
        }

        goldenTimer -= speed
        val hasGolden = bugs.any{it.alive && it.isGolden}
        if(goldenTimer <= 0f && !hasGolden){
            spawnBug(bounds, true)
            goldenTimer = 20f
        }
        bugs.removeAll { !it.alive }
    }

    var score = 0
    var timeLeft = settings.roundDuration
    var isGameOver = false
    fun getGameOver(): Boolean {return isGameOver}
    var bonusTimer = settings.bonusInterval
    var goldenTimer = 20f
    var bonusActive = false
    var bonusTimeLeft = 0f
    var nextId: Long = 0
    var spawnTimer = 0f

    var commonBugImages = listOf(
        com.example.myapplication.R.drawable.bug1,
        com.example.myapplication.R.drawable.bug2,
        com.example.myapplication.R.drawable.bug3,
        com.example.myapplication.R.drawable.bug4
    )

    private fun spawnBug(bounds: Size, golden: Boolean){
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        val speed = if (golden) 120f else 180f
        val bugSize = if (golden) 100f else 150f
        val x = Random.nextFloat() * (bounds.width - bugSize)
        val y = Random.nextFloat() * (bounds.height - bugSize)

        val image = commonBugImages.random()

        bugs += Bug(nextId++,Offset(x,y),
            Offset(cos(angle) * speed, sin(angle) * speed),
            bugSize, golden, image)
    }

}