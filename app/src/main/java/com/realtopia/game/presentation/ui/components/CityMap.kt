package com.realtopia.game.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CityMap(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    onPropertyLongClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var vehicles by remember { mutableStateOf(generateVehicles()) }
    
    // Animate vehicles
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            vehicles = vehicles.map { vehicle ->
                vehicle.copy(
                    x = (vehicle.x + vehicle.speed).let { x ->
                        if (x > 1.2f) -0.2f else x
                    }
                )
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GrassGreen)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCityMap(vehicles)
        }
        
        // Property areas
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Area 1: Başlangıç alanı (düşük fiyatlar)
            PropertyArea(
                title = "Başlangıç Bölgesi",
                properties = properties.filter { it.currentPrice < 50000 },
                priceRange = "₺10K - ₺50K",
                areaColor = LightGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { property ->
                onPropertyClick(property)
            }
            
            // Area 2: Orta fiyatlar
            PropertyArea(
                title = "Orta Sınıf Bölgesi",
                properties = properties.filter { it.currentPrice in 50000.0..150000.0 },
                priceRange = "₺50K - ₺150K",
                areaColor = LightBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { property ->
                onPropertyClick(property)
            }
            
            // Area 3: Yüksek fiyatlar
            PropertyArea(
                title = "Lüks Bölgesi",
                properties = properties.filter { it.currentPrice in 150000.0..500000.0 },
                priceRange = "₺150K - ₺500K",
                areaColor = LightPurple,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { property ->
                onPropertyClick(property)
            }
            
            // Area 4: Premium fiyatlar
            PropertyArea(
                title = "Premium Bölgesi",
                properties = properties.filter { it.currentPrice > 500000 },
                priceRange = "₺500K+",
                areaColor = LightGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { property ->
                onPropertyClick(property)
            }
        }
    }
}

@Composable
private fun PropertyArea(
    title: String,
    properties: List<Property>,
    priceRange: String,
    areaColor: Color,
    modifier: Modifier = Modifier,
    onPropertyClick: (Property) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = areaColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Area title and price range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = priceRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Properties grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(properties.take(12)) { property ->
                    PropertyIcon(
                        property = property,
                        onClick = { onPropertyClick(property) },
                        onLongClick = { /* Handle long click */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyIcon(
    property: Property,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(40.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (property.isOwned) {
                    GameSuccess.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getPropertyIcon(property.type),
                contentDescription = property.type.displayName,
                tint = getPropertyColor(property.type),
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "₺${(property.currentPrice / 1000).toInt()}K",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            if (property.isOwned) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sahip",
                    tint = GameSuccess,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun getPropertyIcon(propertyType: Property.PropertyType): ImageVector {
    return when (propertyType) {
        Property.PropertyType.HOUSE -> Icons.Default.Home
        Property.PropertyType.SHOP -> Icons.Default.Store
        Property.PropertyType.APARTMENT -> Icons.Default.Apartment
        Property.PropertyType.OFFICE -> Icons.Default.Business
    }
}

@Composable
private fun getPropertyColor(propertyType: Property.PropertyType): Color {
    return when (propertyType) {
        Property.PropertyType.HOUSE -> HouseColor
        Property.PropertyType.SHOP -> ShopColor
        Property.PropertyType.APARTMENT -> ApartmentColor
        Property.PropertyType.OFFICE -> OfficeColor
    }
}

private fun DrawScope.drawCityMap(vehicles: List<Vehicle>) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    
    // Draw roads
    drawRoads(canvasWidth, canvasHeight)
    
    // Draw vehicles
    vehicles.forEach { vehicle ->
        drawVehicle(vehicle, canvasWidth, canvasHeight)
    }
}

private fun DrawScope.drawRoads(canvasWidth: Float, canvasHeight: Float) {
    val roadColor = Color(0xFF2C2C2C)
    val roadWidth = 8.dp.toPx()
    
    // Horizontal roads between areas
    val roadYPositions = listOf(
        canvasHeight * 0.25f,
        canvasHeight * 0.5f,
        canvasHeight * 0.75f
    )
    
    roadYPositions.forEach { y ->
        drawLine(
            color = roadColor,
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = roadWidth
        )
        
        // Road markings
        val dashLength = 20.dp.toPx()
        val gapLength = 20.dp.toPx()
        var currentX = 0f
        
        while (currentX < canvasWidth) {
            drawLine(
                color = Color.White,
                start = Offset(currentX, y),
                end = Offset(currentX + dashLength, y),
                strokeWidth = 2.dp.toPx()
            )
            currentX += dashLength + gapLength
        }
    }
    
    // Vertical roads
    val verticalRoadX = canvasWidth * 0.5f
    drawLine(
        color = roadColor,
        start = Offset(verticalRoadX, 0f),
        end = Offset(verticalRoadX, canvasHeight),
        strokeWidth = roadWidth
    )
}

private fun DrawScope.drawVehicle(vehicle: Vehicle, canvasWidth: Float, canvasHeight: Float) {
    val vehicleSize = 12.dp.toPx()
    val x = vehicle.x * canvasWidth
    val y = vehicle.y * canvasHeight
    
    // Vehicle body
    drawRoundRect(
        color = vehicle.color,
        topLeft = Offset(x - vehicleSize/2, y - vehicleSize/2),
        size = androidx.compose.ui.geometry.Size(vehicleSize, vehicleSize * 0.6f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
    )
    
    // Vehicle windows
    drawRoundRect(
        color = Color.White.copy(alpha = 0.8f),
        topLeft = Offset(x - vehicleSize/2 + 1.dp.toPx(), y - vehicleSize/2 + 1.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(vehicleSize - 2.dp.toPx(), vehicleSize * 0.3f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
    )
}

private fun generateVehicles(): List<Vehicle> {
    return (1..8).map {
        Vehicle(
            x = Random.nextFloat() * 1.2f - 0.2f,
            y = listOf(0.25f, 0.5f, 0.75f).random(),
            speed = Random.nextFloat() * 0.01f + 0.005f,
            color = listOf(
                Color(0xFF2196F3),
                Color(0xFF4CAF50),
                Color(0xFFFF9800),
                Color(0xFF9C27B0),
                Color(0xFFF44336)
            ).random()
        )
    }
}

private data class Vehicle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val color: Color
)