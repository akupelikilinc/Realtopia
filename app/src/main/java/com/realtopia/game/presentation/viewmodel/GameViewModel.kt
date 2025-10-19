package com.realtopia.game.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtopia.game.data.model.*
import com.realtopia.game.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()
    
    private val _ownedProperties = MutableStateFlow<List<Property>>(emptyList())
    val ownedProperties: StateFlow<List<Property>> = _ownedProperties.asStateFlow()
    
    private val _marketEvents = MutableStateFlow<List<MarketEvent>>(emptyList())
    val marketEvents: StateFlow<List<MarketEvent>> = _marketEvents.asStateFlow()
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private var marketUpdateJob: Job? = null
    
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val showPropertyDetails: Property? = null,
        val showAchievementNotification: Achievement? = null,
        val showMarketEventNotification: MarketEvent? = null,
        val showUnlockNotification: Property.PropertyType? = null
    )
    
    init {
        initializeGame()
        startMarketUpdates()
        observeData()
    }
    
    private fun initializeGame() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Initialize achievements if empty
                gameRepository.initializeAchievements()
                
                // Generate properties if empty
                gameRepository.generateNewProperties()
                
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun observeData() {
        viewModelScope.launch {
            // Observe game state
            gameRepository.getGameState().collect { state ->
                _gameState.value = state
            }
        }
        
        viewModelScope.launch {
            // Observe properties
            gameRepository.getAllProperties().collect { properties ->
                _properties.value = properties
            }
        }
        
        viewModelScope.launch {
            // Observe owned properties
            gameRepository.getOwnedProperties().collect { properties ->
                _ownedProperties.value = properties
            }
        }
        
        viewModelScope.launch {
            // Observe market events
            gameRepository.getActiveMarketEvents().collect { events ->
                _marketEvents.value = events
                
                // Show notification for new events
                events.forEach { event ->
                    if (!_uiState.value.showMarketEventNotification?.id.equals(event.id)) {
                        _uiState.value = _uiState.value.copy(showMarketEventNotification = event)
                    }
                }
            }
        }
        
        viewModelScope.launch {
            // Observe achievements
            gameRepository.getAllAchievements().collect { achievements ->
                _achievements.value = achievements
            }
        }
    }
    
    
    private fun startMarketUpdates() {
        marketUpdateJob = viewModelScope.launch {
            while (true) {
                delay(4000) // Update every 4 seconds
                
                if (!_gameState.value.isGamePaused) {
                    gameRepository.updateMarketPrices()
                    gameRepository.triggerMarketEvent()
                    updatePortfolioValue()
                }
            }
        }
    }
    
    private fun updatePortfolioValue() {
        val ownedProperties = _ownedProperties.value
        val totalValue = ownedProperties.sumOf { it.currentPrice }
        val currentState = _gameState.value
        _gameState.value = currentState.copy(portfolioValue = totalValue)
    }
    
    fun buyProperty(property: Property) {
        viewModelScope.launch {
            try {
                val currentState = _gameState.value
                if (currentState.canAffordProperty(property)) {
                    val result = gameRepository.buyProperty(property.id)
                    result.onSuccess { updatedProperty ->
                        val newBalance = currentState.balance - property.currentPrice
                        val newOwnedCount = currentState.totalPropertiesOwned + 1
                        val newState = currentState.copy(
                            balance = newBalance,
                            totalPropertiesOwned = newOwnedCount
                        )
                        _gameState.value = newState
                        gameRepository.updateGameState(newState)
                        
                        // Check for property type unlock
                        checkForPropertyUnlock(property.type)
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(error = error.message)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(error = "Yetersiz bakiye!")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    private fun checkForPropertyUnlock(propertyType: Property.PropertyType) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val nextUnlockable = currentState.getNextUnlockablePropertyType()
            if (nextUnlockable != null && currentState.balance >= nextUnlockable.basePrice) {
                val newUnlockedTypes = currentState.unlockedPropertyTypes + nextUnlockable
                val newState = currentState.copy(unlockedPropertyTypes = newUnlockedTypes)
                _gameState.value = newState
                gameRepository.updateGameState(newState)
                _uiState.value = _uiState.value.copy(showUnlockNotification = nextUnlockable)
            }
        }
    }
    
    fun sellProperty(property: Property) {
        viewModelScope.launch {
            try {
                val result = gameRepository.sellProperty(property.id)
                result.onSuccess { updatedProperty ->
                    val currentState = _gameState.value
                    val profit = property.currentPrice - property.price
                    val newBalance = currentState.balance + property.currentPrice
                    val newSoldCount = currentState.totalPropertiesSold + 1
                    val newTotalProfit = currentState.totalProfit + profit
                    val newState = currentState.copy(
                        balance = newBalance,
                        totalPropertiesSold = newSoldCount,
                        totalProfit = newTotalProfit
                    )
                    _gameState.value = newState
                    gameRepository.updateGameState(newState)
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun pauseGame() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val newState = currentState.copy(isGamePaused = !currentState.isGamePaused)
            _gameState.value = newState
            gameRepository.updateGameState(newState)
        }
    }
    
    fun restartGame() {
        viewModelScope.launch {
            gameRepository.resetGame()
            _uiState.value = UiState()
        }
    }
    
    fun showPropertyDetails(property: Property) {
        _uiState.value = _uiState.value.copy(showPropertyDetails = property)
    }
    
    fun hidePropertyDetails() {
        _uiState.value = _uiState.value.copy(showPropertyDetails = null)
    }
    
    fun hideAchievementNotification() {
        _uiState.value = _uiState.value.copy(showAchievementNotification = null)
    }
    
    fun hideMarketEventNotification() {
        _uiState.value = _uiState.value.copy(showMarketEventNotification = null)
    }
    
    fun hideUnlockNotification() {
        _uiState.value = _uiState.value.copy(showUnlockNotification = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        marketUpdateJob?.cancel()
    }
}
