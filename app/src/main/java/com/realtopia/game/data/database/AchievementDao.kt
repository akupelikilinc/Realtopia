package com.realtopia.game.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.realtopia.game.data.model.Achievement

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements ORDER BY type, targetValue")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 0")
    fun getLockedAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE type = :type")
    fun getAchievementsByType(type: Achievement.AchievementType): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: String): Achievement?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Query("UPDATE achievements SET currentProgress = :progress WHERE id = :id")
    suspend fun updateAchievementProgress(id: String, progress: Double)
    
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedDate = :unlockedDate WHERE id = :id")
    suspend fun unlockAchievement(id: String, unlockedDate: Long)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedAchievementCount(): Int
    
    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getTotalAchievementCount(): Int
    
    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
    
    @Delete
    suspend fun deleteAchievement(achievement: Achievement)
}
