package com.realtopia.game.presentation.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlin.math.*

@Composable
fun IsometricMapView(
    properties: List<Property>,
    onPropertyBuy: (Property) -> Unit,
    onPropertySell: (Property) -> Unit,
    canAffordProperty: (Property) -> Boolean,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var hoveredProperty by remember { mutableStateOf<Property?>(null) }
    
    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFF4682B4)  // Steel blue
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    offset += change.position
                }
            }
    ) {
        // Island background
        IslandBackground(
            zoomLevel = zoomLevel,
            offset = offset,
            floatingOffset = floatingOffset
        )
        
        // Grid system
        GridSystem(
            zoomLevel = zoomLevel,
            offset = offset
        )
        
        // Properties on grid
        PropertiesOnGrid(
            properties = properties,
            zoomLevel = zoomLevel,
            offset = offset,
            floatingOffset = floatingOffset,
            onPropertyClick = { property ->
                selectedProperty = property
            },
            onPropertyHover = { property ->
                hoveredProperty = property
            },
            onPropertyBuy = onPropertyBuy,
            onPropertySell = onPropertySell,
            canAffordProperty = canAffordProperty
        )
        
        // UI Overlay
        MapUIOverlay(
            balance = 150000.0, // This should come from ViewModel
            mission = "₺1,000,000",
            timeLeft = "04:55",
            modifier = Modifier.align(Alignment.TopCenter)
        )
        
        // Zoom controls
        ZoomControls(
            zoomLevel = zoomLevel,
            onZoomIn = { zoomLevel = (zoomLevel * 1.2f).coerceAtMost(2f) },
            onZoomOut = { zoomLevel = (zoomLevel / 1.2f).coerceAtLeast(0.5f) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
        
        // Property details overlay
        selectedProperty?.let { property ->
            PropertyDetailsOverlay(
                property = property,
                onClose = { selectedProperty = null },
                onBuy = { 
                    onPropertyBuy(property)
                    selectedProperty = null
                },
                onSell = { 
                    onPropertySell(property)
                    selectedProperty = null
                },
                canAfford = canAffordProperty(property),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun IslandBackground(
    zoomLevel: Float,
    offset: Offset,
    floatingOffset: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = zoomLevel
                scaleY = zoomLevel
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        val width = size.width
        val height = size.height
        
        // Island shape
        val islandPath = Path().apply {
            moveTo(0f, height * 0.7f)
            quadraticBezierTo(width * 0.2f, height * 0.5f, width * 0.4f, height * 0.6f)
            quadraticBezierTo(width * 0.6f, height * 0.4f, width * 0.8f, height * 0.5f)
            quadraticBezierTo(width, height * 0.6f, width, height * 0.7f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        // Draw island
        drawPath(
            path = islandPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4a7c59), // Forest green
                    Color(0xFF2d5016)  // Dark green
                )
            )
        )
        
        // Draw beach
        val beachPath = Path().apply {
            moveTo(0f, height * 0.7f)
            quadraticBezierTo(width * 0.2f, height * 0.5f, width * 0.4f, height * 0.6f)
            quadraticBezierTo(width * 0.6f, height * 0.4f, width * 0.8f, height * 0.5f)
            quadraticBezierTo(width, height * 0.6f, width, height * 0.7f)
            lineTo(width, height * 0.75f)
            lineTo(0f, height * 0.75f)
            close()
        }
        
        drawPath(
            path = beachPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF4E4BC), // Sand
                    Color(0xFFE6D3A3)  // Darker sand
                )
            )
        )
        
        // Draw decorative elements - simplified
        // Trees and beach elements can be added later
    }
}

@Composable
private fun GridSystem(
    zoomLevel: Float,
    offset: Offset
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = zoomLevel
                scaleY = zoomLevel
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        val width = size.width
        val height = size.height
        val gridSize = 80f
        
        // Draw grid lines
        for (x in 0..(width / gridSize).toInt() + 1) {
            val xPos = x * gridSize
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(xPos, height * 0.4f),
                end = Offset(xPos, height * 0.7f),
                strokeWidth = 2f
            )
        }
        
        for (y in 0..((height * 0.3f) / gridSize).toInt() + 1) {
            val yPos = height * 0.4f + y * gridSize
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(0f, yPos),
                end = Offset(width, yPos),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun PropertiesOnGrid(
    properties: List<Property>,
    zoomLevel: Float,
    offset: Offset,
    floatingOffset: Float,
    onPropertyClick: (Property) -> Unit,
    onPropertyHover: (Property) -> Unit,
    onPropertyBuy: (Property) -> Unit,
    onPropertySell: (Property) -> Unit,
    canAffordProperty: (Property) -> Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = zoomLevel
                scaleY = zoomLevel
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        properties.forEachIndexed { index, property ->
            val gridPosition = calculateGridPosition(index, properties.size)
            val floatingY = sin(floatingOffset * 2 * PI + index).toFloat() * 5f
            
            IsometricPropertyCard(
                property = property,
                position = gridPosition,
                floatingOffset = floatingY,
                onPropertyClick = { onPropertyClick(property) },
                onPropertyHover = { onPropertyHover(property) },
                onPropertyBuy = { onPropertyBuy(property) },
                onPropertySell = { onPropertySell(property) },
                canAfford = canAffordProperty(property),
                modifier = Modifier
                    .offset(
                        (gridPosition.x - 40).dp,
                        (gridPosition.y - 40 + floatingY).dp
                    )
            )
        }
    }
}

@Composable
private fun IsometricPropertyCard(
    property: Property,
    position: Offset,
    floatingOffset: Float,
    onPropertyClick: () -> Unit,
    onPropertyHover: () -> Unit,
    onPropertyBuy: () -> Unit,
    onPropertySell: () -> Unit,
    canAfford: Boolean,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isHovered) 12f else 6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )
    
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isHovered = true; onPropertyHover() },
                    onDragEnd = { isHovered = false },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onPropertyClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = getPropertyColor(property.type)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = getPropertyGradient(property.type)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = getPropertyIcon(property.type),
                        contentDescription = property.type.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = formatMoney(property.price),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        fontSize = 8.sp
                    )
                }
                
                // Hover effect
                if (isHovered) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    radius = 40f
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun MapUIOverlay(
    balance: Double,
    mission: String,
    timeLeft: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Balance",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Bakiye: ${formatMoney(balance)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
            
            // Mission
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Mission",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Görev: $mission",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
            
            // Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Time",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Süre: $timeLeft",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ZoomControls(
    zoomLevel: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(48.dp),
            containerColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom In",
                tint = Color.White
            )
        }
        
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(48.dp),
            containerColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom Out",
                tint = Color.White
            )
        }
        
        Text(
            text = "${(zoomLevel * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun PropertyDetailsOverlay(
    property: Property,
    onClose: () -> Unit,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    canAfford: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = getPropertyColor(property.type)
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
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = property.type.displayName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = formatMoney(property.price),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) GameSuccess else GameError
                        )
                    ) {
                        Text("Satın Al", color = Color.White)
                    }
                    
                    Button(
                        onClick = onSell,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GameWarning
                        )
                    ) {
                        Text("Sat", color = Color.White)
                    }
                    
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text("Kapat", color = Color.White)
                    }
                }
            }
        }
    }
}

// Helper functions
private fun calculateGridPosition(index: Int, totalProperties: Int): Offset {
    val gridSize = 80f
    val cols = 6
    val row = index / cols
    val col = index % cols
    return Offset(
        x = col * gridSize + 100f,
        y = row * gridSize + 200f
    )
}

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

private fun getPropertyGradient(propertyType: Property.PropertyType): List<Color> {
    val baseColor = getPropertyColor(propertyType)
    return listOf(
        baseColor,
        baseColor.copy(alpha = 0.8f)
    )
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
        amount >= 1_000_000 -> "%.1fM₺".format(amount / 1_000_000)
        amount >= 1_000 -> "%.1fK₺".format(amount / 1_000)
        else -> "%.0f₺".format(amount)
    }
}

// Canvas helper functions - simplified for now
