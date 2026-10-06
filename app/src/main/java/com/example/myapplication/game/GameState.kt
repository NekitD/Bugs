package com.example.myapplication.game

import com.example.myapplication.tabs.SettingsData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameState(private val settings: SettingsData){

    val bugs = mutableListOf<Bug>()

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

        spawnTimer -= speed
        val aliveCommon = bugs.count{it.alive && !it.isGolden}
        if (spawnTimer <= 0f && aliveCommon < settings.maxBugs){
            spawnBug(bounds, false)
            spawnTimer = 1f / settings.gameSpeed
        }
        bugs.forEach { bug ->
            bug.update(speed, bounds, settings.gameSpeed)
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