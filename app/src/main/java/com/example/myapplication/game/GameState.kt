package com.example.myapplication.game

import com.example.myapplication.tabs.SettingsData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameState(private val settings: SettingsData){

    val bugs = mutableListOf<Bug>()
    private var score = 0
    private var timeLeft = settings.roundDuration
    private var isGameOver = false
    private var bonusTimer = settings.bonusInterval
    private var goldenTimer = 20f
    private var bonusActive = false
    private var bonusTimeLeft = 0f
    var tilt = Offset.Zero
    private var nextId: Long = 0
    private var spawnTimer = 0f

    private var commonBugImages = listOf(
        com.example.myapplication.R.drawable.bug1,
        com.example.myapplication.R.drawable.bug2,
        com.example.myapplication.R.drawable.bug3,
        com.example.myapplication.R.drawable.bug4
    )

    private fun SpawnBug(bounds: Size, golden: Boolean){
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        val speed = if (golden) 120f else 180f
        val bugSize = if (golden) 90f else 100f
        val x = Random.nextFloat() * (bounds.width - bugSize)
        val y = Random.nextFloat() * (bounds.height - bugSize)

        val image = commonBugImages.random()

        bugs += Bug(nextId++,Offset(x,y),
            Offset(cos(angle) * speed, sin(angle) * speed),
            bugSize, golden, image)
    }

}