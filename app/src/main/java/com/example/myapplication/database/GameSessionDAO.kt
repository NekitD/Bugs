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
        INNER JOIN players p ON p.id = s.playerId
        INNER JOIN (
            SELECT playerId, MAX(score) AS maxScore
            FROM game_sessions
            GROUP BY playerId
        ) best ON s.playerId = best.playerId AND s.score = best.maxScore
        ORDER BY s.score DESC, s.finishedAt ASC, s.id DESC
    """)
    fun observeRecords(): Flow<List<RecordRow>>

    @Query("""
        SELECT p.name AS playerName, s.score, s.difficulty, s.finishedAt
        FROM game_sessions s
        INNER JOIN players p ON p.id = s.playerId
        INNER JOIN (
            SELECT playerId, MAX(score) AS maxScore
            FROM game_sessions
            GROUP BY playerId
        ) best ON s.playerId = best.playerId AND s.score = best.maxScore
        ORDER BY s.score DESC, s.finishedAt ASC, s.id DESC
    """)
    suspend fun getRecordsOnce(): List<RecordRow>
}

data class RecordRow(
    val playerName: String,
    val score: Int,
    val difficulty: Int,
    val finishedAt: Long
)