package com.example.myapplication.game

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class Bug(
    val id: Long,
    var position: Offset,
    var velocity: Offset,
    val size: Float = 100f,
    @DrawableRes val imageRes: Int,
    var alive: Boolean = true
) {
    fun update(dsec: Float, bounds: Size, speed: Float) {
        if (!alive) return
        val vx = velocity.x * speed
        val vy = velocity.y * speed
        var newX = position.x + vx * dsec
        var newY = position.y + vy * dsec

        if (newX < 0f) { newX = 0f; velocity = velocity.copy(x = -velocity.x) }
        if (newY < 0f) { newY = 0f; velocity = velocity.copy(y = -velocity.y) }
        if (newX + size > bounds.width) {
            newX = bounds.width - size
            velocity = velocity.copy(x = -velocity.x)
        }
        if (newY + size > bounds.height) {
            newY = bounds.height - size
            velocity = velocity.copy(y = -velocity.y)
        }

        position = Offset(newX, newY)
    }

    fun contains(tap: Offset): Boolean {
        if (!alive) return false
        return tap.x in position.x..(position.x + size) &&
                tap.y in position.y..(position.y + size)
    }
}