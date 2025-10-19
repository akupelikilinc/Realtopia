package com.realtopia.game.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.navigation.GameMode
import com.realtopia.game.presentation.ui.components.*
import com.realtopia.game.presentation.ui.theme.*
import com.realtopia.game.presentation.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameMode: GameMode = GameMode.ENDLESS,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val ownedProperties by viewModel.ownedProperties.collectAsState()
    val marketEvents by viewModel.marketEvents.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
        ) {
            // Top Bar
            TopBar(
                balance = gameState.balance,
                portfolioValue = gameState.portfolioValue,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Map View based on game mode
            when (gameMode) {
                GameMode.CAREER, GameMode.TIME_TRIAL -> {
                    IsometricMapView(
                        properties = properties,
                        onPropertyBuy = { property ->
                            viewModel.buyProperty(property)
                        },
                        onPropertySell = { property ->
                            viewModel.sellProperty(property)
                        },
                        canAffordProperty = { property ->
                            gameState.canAffordProperty(property)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                GameMode.ENDLESS -> {
                    ModernMapView(
                        properties = properties,
                        onPropertyBuy = { property ->
                            viewModel.buyProperty(property)
                        },
                        onPropertySell = { property ->
                            viewModel.sellProperty(property)
                        },
                        canAffordProperty = { property ->
                            gameState.canAffordProperty(property)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
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
        
        uiState.showUnlockNotification?.let { propertyType ->
            UnlockNotification(
                propertyType = propertyType,
                onDismiss = { viewModel.hideUnlockNotification() }
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
fun UnlockNotification(
    propertyType: Property.PropertyType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(3000) // Show for 3 seconds
        isVisible = false
        onDismiss()
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = getPropertyColor(propertyType)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getPropertyIcon(propertyType),
                    contentDescription = propertyType.displayName,
                    tint = TextOnDark,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "YENİ MÜLK TİPİ AÇILDI!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextOnDark
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = propertyType.displayName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextOnDark
                )
            }
        }
    }
}

@Composable
private fun getPropertyIcon(propertyType: Property.PropertyType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (propertyType) {
        Property.PropertyType.APARTMENT -> Icons.Default.Apartment
        Property.PropertyType.HOUSE -> Icons.Default.Home
        Property.PropertyType.VILLA -> Icons.Default.Villa
        Property.PropertyType.SHOP -> Icons.Default.Store
        Property.PropertyType.OFFICE -> Icons.Default.Business
        Property.PropertyType.PLAZA -> Icons.Default.BusinessCenter
        Property.PropertyType.SKYSCRAPER -> Icons.Default.LocationCity
    }
}

@Composable
private fun getPropertyColor(propertyType: Property.PropertyType): Color {
    return when (propertyType) {
        Property.PropertyType.APARTMENT -> ApartmentColor
        Property.PropertyType.HOUSE -> HouseColor
        Property.PropertyType.VILLA -> VillaColor
        Property.PropertyType.SHOP -> ShopColor
        Property.PropertyType.OFFICE -> OfficeColor
        Property.PropertyType.PLAZA -> PlazaColor
        Property.PropertyType.SKYSCRAPER -> SkyscraperColor
    }
}
