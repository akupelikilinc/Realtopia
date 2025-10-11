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
    
    private var gameTimerJob: Job? = null
    private var marketUpdateJob: Job? = null
    
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val showPropertyDetails: Property? = null,
        val showAchievementNotification: Achievement? = null,
        val showMarketEventNotification: MarketEvent? = null,
        val showLevelUpNotification: Int? = null
    )
    
    init {
        initializeGame()
        startGameTimer()
        startMarketUpdates()
        observeData()
    }
    
    private fun initializeGame() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Initialize achievements if empty
                val existingAchievements = gameRepository.getAllAchievements().first()
                if (existingAchievements.isEmpty()) {
                    // This would be handled in the repository initialization
                }
                
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
    
    private fun startGameTimer() {
        gameTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Update every second
                
                val currentState = _gameState.value
                if (!currentState.isPaused && !currentState.isGameOver && currentState.timeLeft > 0) {
                    val newTimeLeft = currentState.timeLeft - 1000
                    val newGameState = currentState.copy(timeLeft = newTimeLeft)
                    
                    if (newTimeLeft <= 0) {
                        // Game over
                        val gameOverState = newGameState.copy(
                            timeLeft = 0,
                            isGameOver = true
                        )
                        _gameState.value = gameOverState
                        gameRepository.updateGameState(gameOverState)
                    } else {
                        _gameState.value = newGameState
                        gameRepository.updateGameState(newGameState)
                    }
                }
            }
        }
    }
    
    private fun startMarketUpdates() {
        marketUpdateJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Update every 5 seconds
                
                if (!_gameState.value.isPaused && !_gameState.value.isGameOver) {
                    gameRepository.updateMarketPrices()
                    gameRepository.triggerMarketEvent()
                }
            }
        }
    }
    
    fun buyProperty(property: Property) {
        viewModelScope.launch {
            try {
                val result = gameRepository.buyProperty(property.id)
                result.onSuccess { updatedProperty ->
                    // Check for level up
                    if (gameRepository.checkLevelUp()) {
                        val newLevel = gameRepository.getCurrentLevel()
                        _uiState.value = _uiState.value.copy(showLevelUpNotification = newLevel)
                    }
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun sellProperty(property: Property) {
        viewModelScope.launch {
            try {
                val result = gameRepository.sellProperty(property.id)
                result.onSuccess { updatedProperty ->
                    // Check for level up
                    if (gameRepository.checkLevelUp()) {
                        val newLevel = gameRepository.getCurrentLevel()
                        _uiState.value = _uiState.value.copy(showLevelUpNotification = newLevel)
                    }
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
            val newState = currentState.copy(isPaused = !currentState.isPaused)
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
    
    fun hideLevelUpNotification() {
        _uiState.value = _uiState.value.copy(showLevelUpNotification = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        gameTimerJob?.cancel()
        marketUpdateJob?.cancel()
    }
}
