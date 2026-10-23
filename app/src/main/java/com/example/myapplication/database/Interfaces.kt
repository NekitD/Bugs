package com.example.myapplication.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface PlayerDao {
    @Insert suspend fun insert(player: PlayerEntity): Long
    @Query("SELECT * FROM players ORDER BY name")
    fun observeAll(): Flow<List<PlayerEntity>>
    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getById(id: Long): PlayerEntity?
}

@Dao
interface GameSessionDao {
    @Insert suspend fun insert(session: GameSessionEntity): Long

    // Для вкладки "Рекорды": имя игрока + очки + сложность + время
    @Query("""
        SELECT p.name AS playerName, s.score, s.difficulty, s.finishedAt
        FROM game_sessions s
        JOIN players p ON p.id = s.playerId
        ORDER BY s.score DESC, s.finishedAt ASC
    """)
    fun observeRecords(): Flow<List<RecordRow>>
}

data class RecordRow(
    val playerName: String,
    val score: Int,
    val difficulty: Int,
    val finishedAtEpochMillis: Long
)