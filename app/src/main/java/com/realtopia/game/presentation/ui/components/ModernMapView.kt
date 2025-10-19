package com.realtopia.game.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun ModernMapView(
    properties: List<Property>,
    onPropertyBuy: (Property) -> Unit,
    onPropertySell: (Property) -> Unit,
    canAffordProperty: (Property) -> Boolean,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var isAnimating by remember { mutableStateOf(false) }
    
    // Auto-rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Floating animation for properties
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
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    ),
                    radius = 1000f
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    offset += change.position
                }
            }
    ) {
        // Animated background elements
        AnimatedBackground(rotation = rotation)
        
        // Properties with 3D positioning
        PropertiesMap(
            properties = properties,
            zoomLevel = zoomLevel,
            offset = offset,
            floatingOffset = floatingOffset,
            onPropertyClick = { property ->
                selectedProperty = property
                isAnimating = !isAnimating
            },
            onPropertyBuy = onPropertyBuy,
            onPropertySell = onPropertySell,
            canAffordProperty = canAffordProperty
        )
        
        // City skyline
        CitySkyline(
            rotation = rotation,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
        
        // Market Status Indicator
        MarketStatusIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
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
private fun AnimatedBackground(rotation: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotation * 0.1f
            }
    ) {
        // Floating particles
        repeat(20) { index ->
            val offsetX = (index * 50f) % 1000f
            val offsetY = (index * 30f) % 800f
            val size = (index % 3 + 1) * 4f
            
            Box(
                modifier = Modifier
                    .offset(offsetX.dp, offsetY.dp)
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.1f)
                    )
            )
        }
        
        // Grid lines
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 1f
            val gridSize = 50f
            
            for (x in 0..size.width.toInt() step gridSize.toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = strokeWidth
                )
            }
            
            for (y in 0..size.height.toInt() step gridSize.toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
private fun CitySkyline(
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer {
                rotationZ = rotation * 0.05f
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val buildingHeights = listOf(80f, 60f, 100f, 40f, 90f, 70f, 50f, 85f, 65f, 75f)
            val buildingWidth = size.width / buildingHeights.size
            
            buildingHeights.forEachIndexed { index, height ->
                val x = index * buildingWidth
                val buildingColor = when (index % 4) {
                    0 -> Color(0xFF2c3e50)
                    1 -> Color(0xFF34495e)
                    2 -> Color(0xFF2c3e50)
                    else -> Color(0xFF34495e)
                }
                
                drawRect(
                    color = buildingColor,
                    topLeft = Offset(x, size.height - height),
                    size = androidx.compose.ui.geometry.Size(buildingWidth, height)
                )
                
                // Windows
                for (windowY in 0..height.toInt() step 20) {
                    for (windowX in 0..buildingWidth.toInt() step 15) {
                        if (windowY > 10 && windowY < height - 10 && windowX > 5 && windowX < buildingWidth - 5) {
                            drawRect(
                                color = Color.Yellow.copy(alpha = 0.3f),
                                topLeft = Offset(x + windowX, size.height - height + windowY),
                                size = androidx.compose.ui.geometry.Size(8f, 8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertiesMap(
    properties: List<Property>,
    zoomLevel: Float,
    offset: Offset,
    floatingOffset: Float,
    onPropertyClick: (Property) -> Unit,
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
            val position = calculatePropertyPosition(index, properties.size)
            val elevation = (index % 3 + 1) * 8f
            val floatingY = sin(floatingOffset * 2 * PI + index).toFloat() * 10f
            
            AnimatedPropertyCard(
                property = property,
                position = position,
                elevation = elevation,
                floatingOffset = floatingY,
                onPropertyClick = { onPropertyClick(property) },
                onPropertyBuy = { onPropertyBuy(property) },
                onPropertySell = { onPropertySell(property) },
                canAfford = canAffordProperty(property),
                modifier = Modifier
                    .offset(
                        (position.x - 50).dp,
                        (position.y - 50 + floatingY).dp
                    )
            )
        }
    }
}

@Composable
private fun AnimatedPropertyCard(
    property: Property,
    position: Offset,
    elevation: Float,
    floatingOffset: Float,
    onPropertyClick: () -> Unit,
    onPropertyBuy: () -> Unit,
    onPropertySell: () -> Unit,
    canAfford: Boolean,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.1f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isHovered) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .size(100.dp)
            .scale(scale)
            .rotate(rotation)
            .graphicsLayer {
                shadowElevation = elevation
                transformOrigin = TransformOrigin.Center
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isHovered = true },
                        onDragEnd = { isHovered = false },
                        onDrag = { _, _ -> }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = getPropertyGradient(property.type).first()
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation.dp
            )
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
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = property.type.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp
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
                
                // Glow effect
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
                                    radius = 50f
                                )
                            )
                    )
                }
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
            containerColor = BackgroundSecondary.copy(alpha = 0.9f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom In",
                tint = TextPrimary
            )
        }
        
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(48.dp),
            containerColor = BackgroundSecondary.copy(alpha = 0.9f)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom Out",
                tint = TextPrimary
            )
        }
        
        Text(
            text = "${(zoomLevel * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
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
                containerColor = getPropertyGradient(property.type).first()
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
                            containerColor = BackgroundSecondary
                        )
                    ) {
                        Text("Kapat", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketStatusIndicator(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundSecondary.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Market Status Icon
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GameSuccess)
            )
            
            Text(
                text = "Piyasa Aktif",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )
        }
    }
}

// Helper functions
private fun calculatePropertyPosition(index: Int, totalProperties: Int): Offset {
    val angle = (index * 2 * PI / totalProperties)
    val radius = 200f + (index % 3) * 50f
    return Offset(
        x = 400f + cos(angle).toFloat() * radius,
        y = 400f + sin(angle).toFloat() * radius
    )
}

private fun getPropertyGradient(propertyType: Property.PropertyType): List<Color> {
    return when (propertyType) {
        Property.PropertyType.APARTMENT -> listOf(ApartmentColor, ApartmentColor.copy(alpha = 0.8f))
        Property.PropertyType.HOUSE -> listOf(HouseColor, HouseColor.copy(alpha = 0.8f))
        Property.PropertyType.VILLA -> listOf(VillaColor, VillaColor.copy(alpha = 0.8f))
        Property.PropertyType.SHOP -> listOf(ShopColor, ShopColor.copy(alpha = 0.8f))
        Property.PropertyType.OFFICE -> listOf(OfficeColor, OfficeColor.copy(alpha = 0.8f))
        Property.PropertyType.PLAZA -> listOf(PlazaColor, PlazaColor.copy(alpha = 0.8f))
        Property.PropertyType.SKYSCRAPER -> listOf(SkyscraperColor, SkyscraperColor.copy(alpha = 0.8f))
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
        amount >= 1_000_000 -> "%.1fM₺".format(amount / 1_000_000)
        amount >= 1_000 -> "%.1fK₺".format(amount / 1_000)
        else -> "%.0f₺".format(amount)
    }
}
