package com.realtopia.game.di

import android.content.Context
import androidx.room.Room
import com.realtopia.game.data.database.RealtopiaDatabase
import com.realtopia.game.data.database.PropertyDao
import com.realtopia.game.data.database.AchievementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideRealtopiaDatabase(
        @ApplicationContext context: Context
    ): RealtopiaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RealtopiaDatabase::class.java,
            "realtopia_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun providePropertyDao(database: RealtopiaDatabase): PropertyDao {
        return database.propertyDao()
    }
    
    @Provides
    fun provideAchievementDao(database: RealtopiaDatabase): AchievementDao {
        return database.achievementDao()
    }
}
