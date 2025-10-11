package com.realtopia.game.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.realtopia.game.data.model.Achievement
import com.realtopia.game.data.model.Property

@Database(
    entities = [Property::class, Achievement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealtopiaDatabase : RoomDatabase() {
    
    abstract fun propertyDao(): PropertyDao
    abstract fun achievementDao(): AchievementDao
    
    companion object {
        @Volatile
        private var INSTANCE: RealtopiaDatabase? = null
        
        fun getDatabase(context: Context): RealtopiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealtopiaDatabase::class.java,
                    "realtopia_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
