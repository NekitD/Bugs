package com.example.myapplication.database
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
@Database(
    entities = [PlayerEntity::class, GameSessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameSessionDao(): GameSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bugs_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}