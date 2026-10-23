package com.example.myapplication.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myapplication.data.PlayerEntity

@Entity(
    tableName = "game_sessions",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("playerId"),
        Index("finishedAt")
    ]
)
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerId: Long,
    val score: Int,
    val difficulty: Int,
    val finishedAt: Long
)