package com.realtopia.game.data.repository

import com.realtopia.game.data.database.PropertyDao
import com.realtopia.game.data.database.AchievementDao
import com.realtopia.game.data.model.*
import com.realtopia.game.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao,
    private val achievementDao: AchievementDao
) : GameRepository {
    
    private val _gameState = MutableStateFlow(GameState())
    private val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _activeMarketEvents = MutableStateFlow<List<MarketEvent>>(emptyList())
    private val activeMarketEvents: StateFlow<List<MarketEvent>> = _activeMarketEvents.asStateFlow()
    
    private val _marketTrend = MutableStateFlow(0.02) // 2% base trend
    private val marketTrend: StateFlow<Double> = _marketTrend.asStateFlow()
    
    override fun getGameState(): Flow<GameState> = gameState
    
    override suspend fun updateGameState(gameState: GameState) {
        _gameState.value = gameState
    }
    
    override suspend fun resetGame() {
        _gameState.value = GameState()
        _activeMarketEvents.value = emptyList()
        _marketTrend.value = 0.02
        propertyDao.deleteAllProperties()
        generateNewProperties()
    }
    
    override fun getAllProperties(): Flow<List<Property>> = propertyDao.getAllProperties()
    
    override fun getOwnedProperties(): Flow<List<Property>> = propertyDao.getOwnedProperties()
    
    override fun getAvailableProperties(): Flow<List<Property>> = propertyDao.getAvailableProperties()
    
    override suspend fun getPropertyById(id: String): Property? = propertyDao.getPropertyById(id)
    
    override suspend fun buyProperty(propertyId: String): Result<Property> {
        return try {
            val property = propertyDao.getPropertyById(propertyId)
            if (property == null) {
                Result.failure(Exception("Property not found"))
            } else if (property.isOwned) {
                Result.failure(Exception("Property already owned"))
            } else if (gameState.value.balance < property.currentPrice) {
                Result.failure(Exception("Insufficient balance"))
            } else {
                val newBalance = gameState.value.balance - property.currentPrice
                val newGameState = gameState.value.copy(
                    balance = newBalance,
                    totalPropertiesOwned = gameState.value.totalPropertiesOwned + 1
                )
                _gameState.value = newGameState
                
                propertyDao.buyProperty(propertyId, System.currentTimeMillis())
                
                // Update achievements
                updateAchievementProgress(Achievement.AchievementType.PROPERTIES_OWNED, 1.0)
                updateAchievementProgress(Achievement.AchievementType.TOTAL_BALANCE, newBalance)
                
                val updatedProperty = propertyDao.getPropertyById(propertyId)!!
                Result.success(updatedProperty)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sellProperty(propertyId: String): Result<Property> {
        return try {
            val property = propertyDao.getPropertyById(propertyId)
            if (property == null) {
                Result.failure(Exception("Property not found"))
            } else if (!property.isOwned) {
                Result.failure(Exception("Property not owned"))
            } else {
                val profit = property.currentPrice - property.price
                val newBalance = gameState.value.balance + property.currentPrice
                val newGameState = gameState.value.copy(
                    balance = newBalance,
                    totalPropertiesSold = gameState.value.totalPropertiesSold + 1,
                    totalProfit = gameState.value.totalProfit + profit
                )
                _gameState.value = newGameState
                
                propertyDao.sellProperty(propertyId)
                
                // Update achievements
                updateAchievementProgress(Achievement.AchievementType.PROPERTIES_SOLD, 1.0)
                updateAchievementProgress(Achievement.AchievementType.TOTAL_PROFIT, profit)
                updateAchievementProgress(Achievement.AchievementType.TOTAL_BALANCE, newBalance)
                
                val updatedProperty = propertyDao.getPropertyById(propertyId)!!
                Result.success(updatedProperty)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePropertyPrice(propertyId: String, newPrice: Double) {
        val property = propertyDao.getPropertyById(propertyId) ?: return
        val priceChange = newPrice - property.currentPrice
        val priceChangePercentage = if (property.currentPrice > 0) {
            (priceChange / property.currentPrice) * 100
        } else 0.0
        
        propertyDao.updatePropertyPrice(propertyId, newPrice, priceChange, priceChangePercentage)
    }
    
    override suspend fun generateNewProperties() {
        val properties = mutableListOf<Property>()
        val gridSize = 8
        
        for (x in 0 until gridSize) {
            for (y in 0 until gridSize) {
                if (Random.nextFloat() < 0.7f) { // 70% chance to place property
                    val propertyType = Property.PropertyType.values().random()
                    val basePrice = propertyType.basePrice
                    val priceVariation = Random.nextDouble(0.5, 1.5)
                    val price = basePrice * priceVariation
                    
                    val property = Property(
                        name = "${propertyType.displayName} (${x},${y})",
                        type = propertyType,
                        price = price,
                        currentPrice = price,
                        sellPrice = price * 0.8,
                        gridX = x,
                        gridY = y
                    )
                    properties.add(property)
                }
            }
        }
        
        propertyDao.insertProperties(properties)
    }
    
    override fun getActiveMarketEvents(): Flow<List<MarketEvent>> = activeMarketEvents
    
    override suspend fun triggerMarketEvent(): MarketEvent? {
        return if (Random.nextFloat() < 0.2f && activeMarketEvents.value.isEmpty()) { // 20% chance
            val event = MarketEvent.createRandomEvent()
            _activeMarketEvents.value = listOf(event)
            event
        } else null
    }
    
    override suspend fun updateMarketPrices() {
        val properties = propertyDao.getAllProperties().first()
        val currentTrend = marketTrend.value
        
        properties.forEach { property ->
            if (!property.isOwned) {
                val volatility = property.type.riskLevel
                val randomChange = Random.nextDouble(-volatility.toDouble(), volatility.toDouble())
                val trendEffect = currentTrend * getTrendMultiplier(property.type)
                
                val newPrice = property.currentPrice * (1 + randomChange + trendEffect)
                val minPrice = property.price * 0.1 // Minimum 10% of original price
                val finalPrice = maxOf(newPrice, minPrice)
                
                updatePropertyPrice(property.id, finalPrice)
            }
        }
        
        // Check for expired market events
        val expiredEvents = activeMarketEvents.value.filter { it.isExpired() }
        if (expiredEvents.isNotEmpty()) {
            _activeMarketEvents.value = activeMarketEvents.value - expiredEvents.toSet()
        }
    }
    
    override suspend fun getMarketTrend(): Double = marketTrend.value
    
    private fun getTrendMultiplier(propertyType: Property.PropertyType): Double {
        return when (propertyType) {
            Property.PropertyType.HOUSE -> 1.0
            Property.PropertyType.SHOP -> 1.2
            Property.PropertyType.APARTMENT -> 1.1
            Property.PropertyType.OFFICE -> 1.3
        }
    }
    
    override fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    
    override fun getUnlockedAchievements(): Flow<List<Achievement>> = achievementDao.getUnlockedAchievements()
    
    override suspend fun updateAchievementProgress(type: Achievement.AchievementType, value: Double) {
        val achievements = achievementDao.getAllAchievements().first()
        achievements.filter { it.type == type && !it.isUnlocked }.forEach { achievement ->
            val newProgress = achievement.currentProgress + value
            achievementDao.updateAchievementProgress(achievement.id, newProgress)
            
            if (newProgress >= achievement.targetValue) {
                unlockAchievement(achievement.id)
            }
        }
    }
    
    override suspend fun unlockAchievement(achievementId: String) {
        achievementDao.unlockAchievement(achievementId, System.currentTimeMillis())
        
        // Give reward
        val achievement = achievementDao.getAchievementById(achievementId)
        if (achievement != null && achievement.rewardAmount > 0) {
            val newBalance = gameState.value.balance + achievement.rewardAmount
            val newGameState = gameState.value.copy(balance = newBalance)
            _gameState.value = newGameState
        }
    }
    
    override suspend fun getCurrentLevel(): Int = gameState.value.currentLevel
    
    override suspend fun getLevelProgress(): Float = gameState.value.getLevelProgressPercentage()
    
    override suspend fun getRequiredBalanceForNextLevel(): Double {
        val currentLevel = gameState.value.currentLevel
        return 1000.0 * Math.pow(1.5, currentLevel.toDouble())
    }
    
    override suspend fun checkLevelUp(): Boolean {
        val currentState = gameState.value
        val requiredBalance = getRequiredBalanceForNextLevel()
        
        if (currentState.balance >= requiredBalance) {
            val newLevel = currentState.currentLevel + 1
            val newTheme = getThemeForLevel(newLevel)
            val newGameState = currentState.copy(
                currentLevel = newLevel,
                currentTheme = newTheme,
                levelProgress = 0.0f
            )
            _gameState.value = newGameState
            
            // Update achievement
            updateAchievementProgress(Achievement.AchievementType.LEVEL_REACHED, newLevel.toDouble())
            
            return true
        }
        return false
    }
    
    override suspend fun getCurrentTheme(): GameState.LevelTheme = gameState.value.currentTheme
    
    private fun getThemeForLevel(level: Int): GameState.LevelTheme {
        return when (level % 5) {
            1 -> GameState.LevelTheme.URBAN
            2 -> GameState.LevelTheme.BEACH
            3 -> GameState.LevelTheme.MOUNTAIN
            4 -> GameState.LevelTheme.FOREST
            0 -> GameState.LevelTheme.DESERT
            else -> GameState.LevelTheme.URBAN
        }
    }
    
    override suspend fun getTotalBalance(): Double = gameState.value.balance
    
    override suspend fun getTotalProfit(): Double = gameState.value.totalProfit
    
    override suspend fun getPortfolioValue(): Double = propertyDao.getTotalPortfolioValue() ?: 0.0
    
    override suspend fun getOwnedPropertyCount(): Int = propertyDao.getOwnedPropertyCount()
    
    override suspend fun getTotalPropertiesSold(): Int = gameState.value.totalPropertiesSold
}
