package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Insert
    suspend fun insert(session: GameSessionEntity): Long

    @Query("""
        SELECT p.name AS playerName, s.score, s.difficulty, s.finishedAt
        FROM game_sessions s
        JOIN players p ON p.id = s.playerId
        ORDER BY s.score DESC, s.finishedAt ASC, s.id DESC
    """)
    fun observeRecords(): Flow<List<RecordRow>>
}

data class RecordRow(
    val playerName: String,
    val score: Int,
    val difficulty: Int,
    val finishedAt: Long
)