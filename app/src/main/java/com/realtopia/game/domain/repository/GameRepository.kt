package com.realtopia.game.domain.repository

import com.realtopia.game.data.model.*
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    
    // Game State
    fun getGameState(): Flow<GameState>
    suspend fun updateGameState(gameState: GameState)
    suspend fun resetGame()
    
    // Properties
    fun getAllProperties(): Flow<List<Property>>
    fun getOwnedProperties(): Flow<List<Property>>
    fun getAvailableProperties(): Flow<List<Property>>
    suspend fun getPropertyById(id: String): Property?
    suspend fun buyProperty(propertyId: String): Result<Property>
    suspend fun sellProperty(propertyId: String): Result<Property>
    suspend fun updatePropertyPrice(propertyId: String, newPrice: Double)
    suspend fun generateNewProperties()
    
    // Market System
    fun getActiveMarketEvents(): Flow<List<MarketEvent>>
    suspend fun triggerMarketEvent(): MarketEvent?
    suspend fun updateMarketPrices()
    suspend fun getMarketTrend(): Double
    
    // Achievements
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    suspend fun updateAchievementProgress(type: Achievement.AchievementType, value: Double)
    suspend fun unlockAchievement(achievementId: String)
    suspend fun initializeAchievements()
    
    // Level System
    suspend fun getCurrentLevel(): Int
    suspend fun getLevelProgress(): Float
    suspend fun getRequiredBalanceForNextLevel(): Double
    suspend fun checkLevelUp(): Boolean
    suspend fun getCurrentTheme(): GameState.LevelTheme
    
    // Statistics
    suspend fun getTotalBalance(): Double
    suspend fun getTotalProfit(): Double
    suspend fun getPortfolioValue(): Double
    suspend fun getOwnedPropertyCount(): Int
    suspend fun getTotalPropertiesSold(): Int
}
