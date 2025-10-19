package com.realtopia.game.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlin.random.Random

@Composable
fun SimpleMonopolyScreen(
    modifier: Modifier = Modifier
) {
    var currentPlayer by remember { mutableStateOf("OYUNCU 1") }
    var playerBalance by remember { mutableStateOf(3000.0) }
    var diceResult by remember { mutableStateOf(0) }
    var canRollDice by remember { mutableStateOf(true) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    
    // Sample properties
    val properties = remember {
        listOf(
            Property(
                id = "1",
                name = "MASLAK",
                type = Property.PropertyType.APARTMENT,
                price = 200.0,
                rent = 20.0
            ),
            Property(
                id = "2", 
                name = "LEVENT",
                type = Property.PropertyType.HOUSE,
                price = 300.0,
                rent = 30.0
            ),
            Property(
                id = "3",
                name = "BEŞİKTAŞ", 
                type = Property.PropertyType.VILLA,
                price = 500.0,
                rent = 50.0
            )
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50), // Green grass
                        Color(0xFF2E7D32)  // Darker green
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player info
            PlayerInfoCard(
                playerName = currentPlayer,
                balance = playerBalance
            )
            
            // Game board
            MonopolyBoardCard(
                properties = properties,
                onPropertyClick = { property ->
                    selectedProperty = property
                }
            )
            
            // Dice and controls
            DiceSection(
                diceResult = diceResult,
                canRollDice = canRollDice,
                onRollDice = {
                    if (canRollDice) {
                        diceResult = Random.nextInt(1, 7)
                        canRollDice = false
                    }
                },
                onEndTurn = {
                    canRollDice = true
                    diceResult = 0
                }
            )
        }
        
        // Property details overlay
        selectedProperty?.let { property ->
            PropertyDetailsCard(
                property = property,
                playerBalance = playerBalance,
                onBuy = {
                    if (playerBalance >= property.price) {
                        playerBalance -= property.price
                        selectedProperty = null
                    }
                },
                onSell = {
                    playerBalance += property.price * 0.8
                    selectedProperty = null
                },
                onClose = {
                    selectedProperty = null
                }
            )
        }
    }
}

@Composable
private fun PlayerInfoCard(
    playerName: String,
    balance: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC) // Beige
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color(0xFFFF9800),
                        RoundedCornerShape(25.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = playerName.first().toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
            
            Column {
                Text(
                    text = playerName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Text(
                    text = "₺${formatMoney(balance)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
private fun MonopolyBoardCard(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC) // Beige
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "MONOPOLY TAHTASI",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Properties grid
            properties.chunked(2).forEach { rowProperties ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowProperties.forEach { property ->
                        PropertyCard(
                            property = property,
                            onClick = { onPropertyClick(property) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PropertyCard(
    property: Property,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isHovered = true },
                    onDragEnd = { isHovered = false },
                    onDrag = { _, _ -> }
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = getPropertyColor(property.type)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = getPropertyIcon(property.type),
                contentDescription = property.type.displayName,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = property.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "₺${formatMoney(property.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun DiceSection(
    diceResult: Int,
    canRollDice: Boolean,
    onRollDice: () -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC) // Beige
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dice
            FloatingActionButton(
                onClick = onRollDice,
                modifier = Modifier.size(60.dp),
                containerColor = if (canRollDice) Color(0xFF4CAF50) else Color.Gray
            ) {
                if (diceResult > 0) {
                    Text(
                        text = diceResult.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Zar At",
                        tint = Color.White
                    )
                }
            }
            
            Column {
                Text(
                    text = if (diceResult > 0) "Zar: $diceResult" else "Zar At",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Text(
                    text = if (canRollDice) "Zar atabilirsiniz" else "Sırayı geçin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onEndTurn,
                enabled = !canRollDice,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Sırayı Geç", color = Color.White)
            }
        }
    }
}

@Composable
private fun PropertyDetailsCard(
    property: Property,
    playerBalance: Double,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getPropertyIcon(property.type),
                    contentDescription = property.type.displayName,
                    tint = getPropertyColor(property.type),
                    modifier = Modifier.size(64.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = property.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Kira ₺${formatMoney(property.rent)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "₺${formatMoney(property.price)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF2E7D32)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onBuy,
                        enabled = playerBalance >= property.price,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (playerBalance >= property.price) Color(0xFFFF9800) else Color.Gray
                        )
                    ) {
                        Text("SATIN AL", color = Color.White)
                    }
                    
                    Button(
                        onClick = onSell,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text("SAT", color = Color.White)
                    }
                    
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E)
                        )
                    ) {
                        Text("KAPAT", color = Color.White)
                    }
                }
            }
        }
    }
}

// Helper functions
private fun getPropertyColor(propertyType: Property.PropertyType): Color {
    return when (propertyType) {
        Property.PropertyType.APARTMENT -> Color(0xFF4CAF50)
        Property.PropertyType.HOUSE -> Color(0xFF2196F3)
        Property.PropertyType.VILLA -> Color(0xFF9C27B0)
        Property.PropertyType.SHOP -> Color(0xFFFF9800)
        Property.PropertyType.OFFICE -> Color(0xFFF44336)
        Property.PropertyType.PLAZA -> Color(0xFFFFD700)
        Property.PropertyType.SKYSCRAPER -> Color(0xFFE91E63)
    }
}

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

private fun formatMoney(amount: Double): String {
    return when {
        amount >= 1000 -> "%.1fK".format(amount / 1000)
        else -> "%.0f".format(amount)
    }
}
