package com.realtopia.game.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtopia.game.presentation.ui.components.*
import com.realtopia.game.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()
    val marketEvents by viewModel.marketEvents.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Apply theme background
    LaunchedEffect(gameState.currentTheme) {
        // This would update the system UI colors based on theme
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getThemeBackgroundColor(gameState.currentTheme))
        ) {
            // Top Bar
            TopBar(
                balance = gameState.balance,
                level = gameState.currentLevel,
                timeLeft = gameState.timeLeft,
                isPaused = gameState.isPaused,
                onPauseClick = { viewModel.pauseGame() }
            )
            
            // Isometric City Map
            IsometricCityMap(
                properties = properties,
                onPropertyClick = { property ->
                    if (property.isOwned) {
                        viewModel.sellProperty(property)
                    } else {
                        viewModel.buyProperty(property)
                    }
                },
                onPropertyLongClick = { property ->
                    viewModel.showPropertyDetails(property)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            
            // Bottom Bar
            BottomBar(
                gameMode = gameState.gameMode,
                ownedCount = ownedProperties.size,
                portfolioValue = gameState.portfolioValue,
                onRestartClick = { viewModel.restartGame() }
            )
        }
        
        // Overlays
        uiState.showPropertyDetails?.let { property ->
            PropertyDetailsDialog(
                property = property,
                onDismiss = { viewModel.hidePropertyDetails() }
            )
        }
        
        uiState.showAchievementNotification?.let { achievement ->
            AchievementNotification(
                achievement = achievement,
                onDismiss = { viewModel.hideAchievementNotification() }
            )
        }
        
        uiState.showMarketEventNotification?.let { event ->
            MarketEventNotification(
                event = event,
                onDismiss = { viewModel.hideMarketEventNotification() }
            )
        }
        
        uiState.showLevelUpNotification?.let { level ->
            LevelUpNotification(
                level = level,
                onDismiss = { viewModel.hideLevelUpNotification() }
            )
        }
        
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }
    }
}

@Composable
private fun getThemeBackgroundColor(theme: com.realtopia.game.data.model.GameState.LevelTheme): Color {
    return when (theme) {
        com.realtopia.game.data.model.GameState.LevelTheme.URBAN -> com.realtopia.game.presentation.ui.theme.UrbanBackground
        com.realtopia.game.data.model.GameState.LevelTheme.BEACH -> com.realtopia.game.presentation.ui.theme.BeachBackground
        com.realtopia.game.data.model.GameState.LevelTheme.MOUNTAIN -> com.realtopia.game.presentation.ui.theme.MountainBackground
        com.realtopia.game.data.model.GameState.LevelTheme.FOREST -> com.realtopia.game.presentation.ui.theme.ForestBackground
        com.realtopia.game.data.model.GameState.LevelTheme.DESERT -> com.realtopia.game.presentation.ui.theme.DesertBackground
    }
}
