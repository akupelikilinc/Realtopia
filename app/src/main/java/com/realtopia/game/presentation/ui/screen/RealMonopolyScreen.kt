package com.realtopia.game.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

@Composable
fun RealMonopolyScreen(
    modifier: Modifier = Modifier
) {
    var currentPlayer by remember { mutableStateOf(0) }
    var players by remember { mutableStateOf(createPlayers()) }
    var diceResult by remember { mutableStateOf(0) }
    var canRollDice by remember { mutableStateOf(true) }
    var gamePhase by remember { mutableStateOf(GamePhase.ROLL_DICE) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var showDiceResult by remember { mutableStateOf(false) }
    
    val boardProperties = remember { createBoardProperties() }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50)) // Green grass
        ) {
            // Players info
            PlayersInfo(
                players = players,
                currentPlayer = currentPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            // Game board
            MonopolyBoard(
                properties = boardProperties,
                players = players,
                currentPlayerPosition = players[currentPlayer].position,
                onPropertyClick = { property ->
                    if (gamePhase == GamePhase.PROPERTY_ACTION) {
                        selectedProperty = property
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            // Game controls
            GameControls(
                diceResult = diceResult,
                canRollDice = canRollDice,
                gamePhase = gamePhase,
                onRollDice = {
                    if (canRollDice) {
                        diceResult = Random.nextInt(1, 7)
                        showDiceResult = true
                        canRollDice = false
                        gamePhase = GamePhase.MOVE_PLAYER
                    }
                },
                onEndTurn = {
                    // Move to next player
                    currentPlayer = (currentPlayer + 1) % players.size
                    canRollDice = true
                    diceResult = 0
                    gamePhase = GamePhase.ROLL_DICE
                    showDiceResult = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        
        // Dice result overlay
        if (showDiceResult) {
            DiceResultOverlay(
                diceResult = diceResult,
                onDismiss = {
                    showDiceResult = false
                    // Move player
                    val newPosition = (players[currentPlayer].position + diceResult) % 40
                    players = players.mapIndexed { index, player ->
                        if (index == currentPlayer) {
                            player.copy(position = newPosition)
                        } else player
                    }
                    gamePhase = GamePhase.PROPERTY_ACTION
                }
            )
        }
        
        // Property details overlay
        selectedProperty?.let { property ->
            PropertyDetailsOverlay(
                property = property,
                player = players[currentPlayer],
                onBuy = {
                    if (players[currentPlayer].balance >= property.price.toInt()) {
                        players = players.mapIndexed { index, player ->
                            if (index == currentPlayer) {
                                player.copy(
                                    balance = player.balance - property.price.toInt(),
                                    properties = player.properties + property.id
                                )
                            } else player
                        }
                    }
                    selectedProperty = null
                },
                onSell = {
                    if (property.ownerId == players[currentPlayer].id) {
                        players = players.mapIndexed { index, player ->
                            if (index == currentPlayer) {
                                player.copy(
                                    balance = player.balance + (property.price * 0.8).toInt(),
                                    properties = player.properties - property.id
                                )
                            } else player
                        }
                    }
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
private fun PlayersInfo(
    players: List<Player>,
    currentPlayer: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        players.forEachIndexed { index, player ->
            PlayerCard(
                player = player,
                isCurrentPlayer = index == currentPlayer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PlayerCard(
    player: Player,
    isCurrentPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlayer) Color(0xFFFFD700) else Color(0xFFF5F5DC)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPlayer) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(player.color, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = player.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "₺${formatMoney(player.balance.toDouble())}",
                fontSize = 9.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
private fun MonopolyBoard(
    properties: List<Property>,
    players: List<Player>,
    currentPlayerPosition: Int,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC) // Beige
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            drawMonopolyBoard(
                size = size,
                properties = properties,
                players = players,
                onPropertyClick = onPropertyClick
            )
        }
    }
}

@Composable
private fun GameControls(
    diceResult: Int,
    canRollDice: Boolean,
    gamePhase: GamePhase,
    onRollDice: () -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC)
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
                    text = when (gamePhase) {
                        GamePhase.ROLL_DICE -> "Zar At"
                        GamePhase.MOVE_PLAYER -> "Hareket Et"
                        GamePhase.PROPERTY_ACTION -> "Mülk İşlemi"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Text(
                    text = if (diceResult > 0) "Zar: $diceResult" else "Sıra sizde",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onEndTurn,
                enabled = gamePhase == GamePhase.PROPERTY_ACTION,
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
private fun DiceResultOverlay(
    diceResult: Int,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ZAR SONUCU",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Animated dice
                AnimatedDice(value = diceResult)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Hareket: $diceResult kare",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Tamam", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AnimatedDice(value: Int) {
    var isAnimating by remember { mutableStateOf(true) }
    
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) 360f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "rotation"
    )
    
    LaunchedEffect(value) {
        isAnimating = true
        kotlinx.coroutines.delay(1000)
        isAnimating = false
    }
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer {
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun PropertyDetailsOverlay(
    property: Property,
    player: Player,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
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
                        enabled = player.balance >= property.price.toInt() && property.ownerId == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (player.balance >= property.price.toInt() && property.ownerId == null) Color(0xFFFF9800) else Color.Gray
                        )
                    ) {
                        Text("SATIN AL", color = Color.White)
                    }
                    
                    Button(
                        onClick = onSell,
                        enabled = property.ownerId == player.id,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (property.ownerId == player.id) Color(0xFFD32F2F) else Color.Gray
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

// Data classes
data class Player(
    val id: String,
    val name: String,
    val balance: Int,
    val color: Color,
    val position: Int = 0,
    val properties: Set<String> = emptySet()
)

enum class GamePhase {
    ROLL_DICE,
    MOVE_PLAYER,
    PROPERTY_ACTION
}

// Helper functions
private fun createPlayers(): List<Player> {
    return listOf(
        Player(
            id = "player1",
            name = "OYUNCU 1",
            balance = 3000,
            color = Color(0xFFFF9800) // Orange
        ),
        Player(
            id = "player2",
            name = "OYUNCU 2",
            balance = 3000,
            color = Color(0xFF2196F3) // Blue
        ),
        Player(
            id = "player3",
            name = "OYUNCU 3",
            balance = 3000,
            color = Color(0xFF4CAF50) // Green
        ),
        Player(
            id = "player4",
            name = "OYUNCU 4",
            balance = 3000,
            color = Color(0xFFF44336) // Red
        )
    )
}

private fun createBoardProperties(): List<Property> {
    val propertyNames = listOf(
        "BAŞLANGIÇ", "MASLAK", "LEVENT", "BEŞİKTAŞ", "ŞANS",
        "KADIKÖY", "ÜSKÜDAR", "ŞANS", "BOSTANCI", "KARTAL",
        "HAPİSHANE", "MALTEPE", "PENDİK", "ŞANS", "TÜZLA",
        "ÜCRETSİZ PARK", "ÇEKMEKÖY", "ŞANS", "SARIYER", "BEYKOZ",
        "HAPİSHANEYE GİT", "ŞİŞLİ", "MECİDİYEKÖY", "ŞANS", "GAYRETTEPE",
        "ŞANS", "BAKIRKÖY", "ZEYTİNBURNU", "ŞANS", "YEŞİLKÖY",
        "ŞANS", "BAHÇELİEVLER", "ŞANS", "KÜÇÜKÇEKMECE", "BÜYÜKÇEKMECE",
        "ŞANS", "AVCILAR", "ŞANS", "SİLİVRİ", "ÇATALCA"
    )
    
    return propertyNames.mapIndexed { index, name ->
        when (name) {
            "BAŞLANGIÇ", "HAPİSHANE", "ÜCRETSİZ PARK", "HAPİSHANEYE GİT" -> {
                Property(
                    id = "special_$index",
                    name = name,
                    type = Property.PropertyType.APARTMENT,
                    price = 0.0,
                    rent = 0.0
                )
            }
            "ŞANS" -> {
                Property(
                    id = "chance_$index",
                    name = name,
                    type = Property.PropertyType.APARTMENT,
                    price = 0.0,
                    rent = 0.0
                )
            }
            else -> {
                val basePrice = 100.0 + (index * 50.0)
                Property(
                    id = "property_$index",
                    name = name,
                    type = Property.PropertyType.APARTMENT,
                    price = basePrice,
                    rent = basePrice * 0.1
                )
            }
        }
    }
}

private fun DrawScope.drawMonopolyBoard(
    size: androidx.compose.ui.geometry.Size,
    properties: List<Property>,
    players: List<Player>,
    onPropertyClick: (Property) -> Unit
) {
    val boardSize = minOf(size.width, size.height)
    val squareSize = boardSize / 11f
    val startX = (size.width - boardSize) / 2
    val startY = (size.height - boardSize) / 2
    
    // Draw board background
    drawRect(
        color = Color(0xFFF5F5DC),
        topLeft = Offset(startX, startY),
        size = androidx.compose.ui.geometry.Size(boardSize, boardSize)
    )
    
    // Draw property squares
    drawPropertySquares(
        startX = startX,
        startY = startY,
        squareSize = squareSize,
        properties = properties
    )
    
    // Draw players
    drawPlayers(
        startX = startX,
        startY = startY,
        squareSize = squareSize,
        players = players
    )
}

private fun DrawScope.drawPropertySquares(
    startX: Float,
    startY: Float,
    squareSize: Float,
    properties: List<Property>
) {
    // Top row (left to right)
    for (i in 0..9) {
        val x = startX + i * squareSize
        val y = startY
        val property = properties.getOrNull(i)
        
        drawRect(
            color = getPropertyGroupColor(i),
            topLeft = Offset(x, y),
            size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
        )
        
        // Draw property name - simplified for now
        // Text drawing will be handled by Compose Text components
    }
    
    // Right column (top to bottom)
    for (i in 0..9) {
        val x = startX + 9 * squareSize
        val y = startY + (i + 1) * squareSize
        val property = properties.getOrNull(9 + i)
        
        drawRect(
            color = getPropertyGroupColor(9 + i),
            topLeft = Offset(x, y),
            size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
        )
        
        // Draw property name - simplified for now
        // Text drawing will be handled by Compose Text components
    }
    
    // Bottom row (right to left)
    for (i in 0..9) {
        val x = startX + (8 - i) * squareSize
        val y = startY + 10 * squareSize
        val property = properties.getOrNull(19 + i)
        
        drawRect(
            color = getPropertyGroupColor(19 + i),
            topLeft = Offset(x, y),
            size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
        )
        
        // Draw property name - simplified for now
        // Text drawing will be handled by Compose Text components
    }
    
    // Left column (bottom to top)
    for (i in 0..9) {
        val x = startX
        val y = startY + (9 - i) * squareSize
        val property = properties.getOrNull(29 + i)
        
        drawRect(
            color = getPropertyGroupColor(29 + i),
            topLeft = Offset(x, y),
            size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
        )
        
        // Draw property name - simplified for now
        // Text drawing will be handled by Compose Text components
    }
}

private fun DrawScope.drawPlayers(
    startX: Float,
    startY: Float,
    squareSize: Float,
    players: List<Player>
) {
    players.forEach { player ->
        val position = calculatePlayerPosition(
            player.position,
            startX,
            startY,
            squareSize
        )
        
        drawCircle(
            color = player.color,
            radius = 8f,
            center = position
        )
    }
}

private fun calculatePlayerPosition(
    position: Int,
    startX: Float,
    startY: Float,
    squareSize: Float
): Offset {
    val totalSquares = 40
    val normalizedPosition = position % totalSquares
    
    return when {
        normalizedPosition < 10 -> {
            // Top row
            Offset(
                startX + normalizedPosition * squareSize + squareSize / 2,
                startY + squareSize / 2
            )
        }
        normalizedPosition < 20 -> {
            // Right column
            val row = normalizedPosition - 10
            Offset(
                startX + 10 * squareSize - squareSize / 2,
                startY + row * squareSize + squareSize / 2
            )
        }
        normalizedPosition < 30 -> {
            // Bottom row
            val col = 30 - normalizedPosition
            Offset(
                startX + col * squareSize + squareSize / 2,
                startY + 10 * squareSize - squareSize / 2
            )
        }
        else -> {
            // Left column
            val row = 40 - normalizedPosition
            Offset(
                startX + squareSize / 2,
                startY + row * squareSize + squareSize / 2
            )
        }
    }
}

private fun getPropertyGroupColor(index: Int): Color {
    return when (index % 8) {
        0 -> Color(0xFFE57373) // Light red
        1 -> Color(0xFF64B5F6) // Light blue
        2 -> Color(0xFFFFF176) // Light yellow
        3 -> Color(0xFF81C784) // Light green
        4 -> Color(0xFFFFB74D) // Light orange
        5 -> Color(0xFFBA68C8) // Light purple
        6 -> Color(0xFF4DB6AC) // Light teal
        else -> Color(0xFF90A4AE) // Light blue grey
    }
}

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
