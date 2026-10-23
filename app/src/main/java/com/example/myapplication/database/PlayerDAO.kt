package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.myapplication.data.PlayerEntity

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(player: PlayerEntity): Long

    @Query("SELECT * FROM players ORDER BY name")
    fun observeAll(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getById(id: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): PlayerEntity?
}