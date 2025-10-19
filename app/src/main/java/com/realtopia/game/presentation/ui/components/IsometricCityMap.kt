package com.realtopia.game.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*
import kotlin.math.*

@Composable
fun IsometricCityMap(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    onPropertyLongClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Sky blue background
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawIsometricCity(properties, onPropertyClick)
        }
    }
}

private fun DrawScope.drawIsometricCity(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit
) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    
    // Draw sea/water
    drawSea(canvasWidth, canvasHeight)
    
    // Draw beach
    drawBeach(canvasWidth, canvasHeight)
    
    // Draw grass areas (building plots)
    drawGrassAreas(canvasWidth, canvasHeight)
    
    // Draw roads
    drawIsometricRoads(canvasWidth, canvasHeight)
    
    // Draw buildings
    drawBuildings(properties, canvasWidth, canvasHeight)
    
    // Draw parked vehicles
    drawParkedVehicles(canvasWidth, canvasHeight)
    
    // Draw beach decorations (palm trees, umbrellas, etc.)
    drawBeachDecorations(canvasWidth, canvasHeight)
}

private fun DrawScope.drawSea(canvasWidth: Float, canvasHeight: Float) {
    // Sea areas in corners
    val seaColor = Color(0xFF0066CC)
    
    // Bottom left sea
    drawPath(
        path = Path().apply {
            moveTo(0f, canvasHeight * 0.8f)
            lineTo(canvasWidth * 0.3f, canvasHeight * 0.9f)
            lineTo(0f, canvasHeight)
            close()
        },
        color = seaColor
    )
    
    // Bottom right sea
    drawPath(
        path = Path().apply {
            moveTo(canvasWidth * 0.7f, canvasHeight * 0.9f)
            lineTo(canvasWidth, canvasHeight * 0.8f)
            lineTo(canvasWidth, canvasHeight)
            close()
        },
        color = seaColor
    )
}

private fun DrawScope.drawBeach(canvasWidth: Float, canvasHeight: Float) {
    val beachColor = Color(0xFFFFE4B5)
    
    // Beach areas
    drawPath(
        path = Path().apply {
            moveTo(0f, canvasHeight * 0.7f)
            lineTo(canvasWidth * 0.3f, canvasHeight * 0.8f)
            lineTo(canvasWidth * 0.3f, canvasHeight * 0.9f)
            lineTo(0f, canvasHeight * 0.8f)
            close()
        },
        color = beachColor
    )
    
    drawPath(
        path = Path().apply {
            moveTo(canvasWidth * 0.7f, canvasHeight * 0.8f)
            lineTo(canvasWidth * 0.7f, canvasHeight * 0.9f)
            lineTo(canvasWidth, canvasHeight * 0.8f)
            lineTo(canvasWidth, canvasHeight * 0.7f)
            close()
        },
        color = beachColor
    )
}

private fun DrawScope.drawGrassAreas(canvasWidth: Float, canvasHeight: Float) {
    val grassColor = Color(0xFF4CAF50)
    
    // Four main grass areas for buildings
    val grassAreas = listOf(
        // Top left
        listOf(
            Offset(canvasWidth * 0.1f, canvasHeight * 0.1f),
            Offset(canvasWidth * 0.4f, canvasHeight * 0.1f),
            Offset(canvasWidth * 0.45f, canvasHeight * 0.15f),
            Offset(canvasWidth * 0.15f, canvasHeight * 0.15f)
        ),
        // Top right
        listOf(
            Offset(canvasWidth * 0.55f, canvasHeight * 0.15f),
            Offset(canvasWidth * 0.85f, canvasHeight * 0.15f),
            Offset(canvasWidth * 0.9f, canvasHeight * 0.1f),
            Offset(canvasWidth * 0.6f, canvasHeight * 0.1f)
        ),
        // Bottom left
        listOf(
            Offset(canvasWidth * 0.1f, canvasHeight * 0.2f),
            Offset(canvasWidth * 0.4f, canvasHeight * 0.2f),
            Offset(canvasWidth * 0.45f, canvasHeight * 0.6f),
            Offset(canvasWidth * 0.15f, canvasHeight * 0.6f)
        ),
        // Bottom right
        listOf(
            Offset(canvasWidth * 0.55f, canvasHeight * 0.6f),
            Offset(canvasWidth * 0.85f, canvasHeight * 0.6f),
            Offset(canvasWidth * 0.9f, canvasHeight * 0.2f),
            Offset(canvasWidth * 0.6f, canvasHeight * 0.2f)
        )
    )
    
    grassAreas.forEach { area ->
        drawPath(
            path = Path().apply {
                moveTo(area[0].x, area[0].y)
                area.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
                close()
            },
            color = grassColor
        )
    }
}

private fun DrawScope.drawIsometricRoads(canvasWidth: Float, canvasHeight: Float) {
    val roadColor = Color(0xFF2C2C2C)
    val roadWidth = 6.dp.toPx()
    
    // Horizontal roads
    drawLine(
        color = roadColor,
        start = Offset(0f, canvasHeight * 0.5f),
        end = Offset(canvasWidth, canvasHeight * 0.5f),
        strokeWidth = roadWidth
    )
    
    // Vertical roads
    drawLine(
        color = roadColor,
        start = Offset(canvasWidth * 0.5f, 0f),
        end = Offset(canvasWidth * 0.5f, canvasHeight),
        strokeWidth = roadWidth
    )
    
    // Road markings
    val dashLength = 15.dp.toPx()
    val gapLength = 15.dp.toPx()
    
    // Horizontal road markings
    var currentX = 0f
    while (currentX < canvasWidth) {
        drawLine(
            color = Color.White,
            start = Offset(currentX, canvasHeight * 0.5f),
            end = Offset(currentX + dashLength, canvasHeight * 0.5f),
            strokeWidth = 2.dp.toPx()
        )
        currentX += dashLength + gapLength
    }
    
    // Vertical road markings
    var currentY = 0f
    while (currentY < canvasHeight) {
        drawLine(
            color = Color.White,
            start = Offset(canvasWidth * 0.5f, currentY),
            end = Offset(canvasWidth * 0.5f, currentY + dashLength),
            strokeWidth = 2.dp.toPx()
        )
        currentY += dashLength + gapLength
    }
}

private fun DrawScope.drawBuildings(
    properties: List<Property>,
    canvasWidth: Float,
    canvasHeight: Float
) {
    // Building positions
    val buildingPositions = listOf(
        // Top left area
        Offset(canvasWidth * 0.2f, canvasHeight * 0.3f),
        Offset(canvasWidth * 0.35f, canvasHeight * 0.25f),
        // Top right area
        Offset(canvasWidth * 0.65f, canvasHeight * 0.25f),
        Offset(canvasWidth * 0.8f, canvasHeight * 0.3f),
        // Bottom left area
        Offset(canvasWidth * 0.2f, canvasHeight * 0.45f),
        Offset(canvasWidth * 0.35f, canvasHeight * 0.4f),
        // Bottom right area
        Offset(canvasWidth * 0.65f, canvasHeight * 0.4f),
        Offset(canvasWidth * 0.8f, canvasHeight * 0.45f)
    )
    
    properties.take(8).forEachIndexed { index, property ->
        if (index < buildingPositions.size) {
            drawBuilding(
                property = property,
                position = buildingPositions[index],
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight
            )
        }
    }
}

private fun DrawScope.drawBuilding(
    property: Property,
    position: Offset,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val buildingWidth = 40.dp.toPx()
    val buildingHeight = 30.dp.toPx()
    val buildingDepth = 20.dp.toPx()
    
    // Building colors based on type
    val buildingColor = when (property.type) {
        Property.PropertyType.HOUSE -> Color(0xFF8D6E63) // Brown
        Property.PropertyType.SHOP -> Color(0xFFE57373) // Light red
        Property.PropertyType.APARTMENT -> Color(0xFF90A4AE) // Gray
        Property.PropertyType.OFFICE -> Color(0xFF64B5F6) // Blue
        Property.PropertyType.VILLA -> Color(0xFFBA68C8) // Purple
        Property.PropertyType.PLAZA -> Color(0xFFFFD54F) // Yellow
        Property.PropertyType.SKYSCRAPER -> Color(0xFF4FC3F7) // Light blue
    }
    
    val roofColor = when (property.type) {
        Property.PropertyType.HOUSE -> Color(0xFF5D4037) // Dark brown
        Property.PropertyType.SHOP -> Color(0xFFD32F2F) // Red
        Property.PropertyType.APARTMENT -> Color(0xFF455A64) // Dark gray
        Property.PropertyType.OFFICE -> Color(0xFF1976D2) // Dark blue
        Property.PropertyType.VILLA -> Color(0xFF8E24AA) // Dark purple
        Property.PropertyType.PLAZA -> Color(0xFFF57F17) // Dark yellow
        Property.PropertyType.SKYSCRAPER -> Color(0xFF0277BD) // Dark blue
    }
    
    // Draw building base (isometric)
    val basePoints = listOf(
        Offset(position.x - buildingWidth/2, position.y),
        Offset(position.x, position.y - buildingHeight/2),
        Offset(position.x + buildingWidth/2, position.y),
        Offset(position.x, position.y + buildingHeight/2)
    )
    
    drawPath(
        path = Path().apply {
            moveTo(basePoints[0].x, basePoints[0].y)
            basePoints.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
            close()
        },
        color = buildingColor
    )
    
    // Draw building roof (isometric)
    val roofPoints = listOf(
        Offset(position.x - buildingWidth/2, position.y - buildingDepth),
        Offset(position.x, position.y - buildingHeight/2 - buildingDepth),
        Offset(position.x + buildingWidth/2, position.y - buildingDepth),
        Offset(position.x, position.y + buildingHeight/2 - buildingDepth)
    )
    
    drawPath(
        path = Path().apply {
            moveTo(roofPoints[0].x, roofPoints[0].y)
            roofPoints.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
            close()
        },
        color = roofColor
    )
    
    // Draw building sides for 3D effect
    drawLine(
        color = buildingColor.copy(alpha = 0.7f),
        start = basePoints[0],
        end = roofPoints[0],
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = buildingColor.copy(alpha = 0.7f),
        start = basePoints[1],
        end = roofPoints[1],
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = buildingColor.copy(alpha = 0.7f),
        start = basePoints[2],
        end = roofPoints[2],
        strokeWidth = 2.dp.toPx()
    )
    
    // Draw price label above building
    val priceText = "$${property.currentPrice.toInt()}"
    val priceLabelY = position.y - buildingDepth - 20.dp.toPx()
    
    // Price label background
    drawRoundRect(
        color = Color.Blue.copy(alpha = 0.8f),
        topLeft = Offset(position.x - 30.dp.toPx(), priceLabelY - 10.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(60.dp.toPx(), 20.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
    )
    
    // Price text (simplified - in real implementation you'd use drawText)
    drawCircle(
        color = Color.White,
        radius = 3.dp.toPx(),
        center = Offset(position.x, priceLabelY)
    )
}

private fun DrawScope.drawParkedVehicles(canvasWidth: Float, canvasHeight: Float) {
    val vehiclePositions = listOf(
        Offset(canvasWidth * 0.3f, canvasHeight * 0.5f),
        Offset(canvasWidth * 0.7f, canvasHeight * 0.5f),
        Offset(canvasWidth * 0.5f, canvasHeight * 0.3f),
        Offset(canvasWidth * 0.5f, canvasHeight * 0.7f)
    )
    
    vehiclePositions.forEach { position ->
        drawVehicle(position)
    }
}

private fun DrawScope.drawVehicle(position: Offset) {
    val vehicleWidth = 20.dp.toPx()
    val vehicleHeight = 12.dp.toPx()
    val vehicleColor = Color(0xFFFF9800) // Orange
    
    // Vehicle body (isometric)
    val vehiclePoints = listOf(
        Offset(position.x - vehicleWidth/2, position.y),
        Offset(position.x, position.y - vehicleHeight/2),
        Offset(position.x + vehicleWidth/2, position.y),
        Offset(position.x, position.y + vehicleHeight/2)
    )
    
    drawPath(
        path = Path().apply {
            moveTo(vehiclePoints[0].x, vehiclePoints[0].y)
            vehiclePoints.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
            close()
        },
        color = vehicleColor
    )
    
    // Vehicle windows
    drawPath(
        path = Path().apply {
            moveTo(vehiclePoints[0].x + 2.dp.toPx(), vehiclePoints[0].y - 2.dp.toPx())
            lineTo(vehiclePoints[1].x - 2.dp.toPx(), vehiclePoints[1].y + 2.dp.toPx())
            lineTo(vehiclePoints[2].x - 2.dp.toPx(), vehiclePoints[2].y + 2.dp.toPx())
            lineTo(vehiclePoints[3].x + 2.dp.toPx(), vehiclePoints[3].y - 2.dp.toPx())
            close()
        },
        color = Color.White.copy(alpha = 0.8f)
    )
}

private fun DrawScope.drawBeachDecorations(canvasWidth: Float, canvasHeight: Float) {
    // Palm trees
    drawPalmTree(Offset(canvasWidth * 0.15f, canvasHeight * 0.75f))
    drawPalmTree(Offset(canvasWidth * 0.85f, canvasHeight * 0.75f))
    
    // Beach umbrellas
    drawBeachUmbrella(Offset(canvasWidth * 0.25f, canvasHeight * 0.85f))
    drawBeachUmbrella(Offset(canvasWidth * 0.75f, canvasHeight * 0.85f))
}

private fun DrawScope.drawPalmTree(position: Offset) {
    // Palm tree trunk
    drawLine(
        color = Color(0xFF8D6E63),
        start = Offset(position.x, position.y),
        end = Offset(position.x, position.y - 30.dp.toPx()),
        strokeWidth = 4.dp.toPx()
    )
    
    // Palm leaves
    val leafColor = Color(0xFF4CAF50)
    repeat(6) { i ->
        val angle = (i * 60f) * (PI / 180f)
        val leafEndX = position.x + cos(angle).toFloat() * 20.dp.toPx()
        val leafEndY = position.y - 30.dp.toPx() + sin(angle).toFloat() * 20.dp.toPx()
        
        drawLine(
            color = leafColor,
            start = Offset(position.x, position.y - 30.dp.toPx()),
            end = Offset(leafEndX, leafEndY),
            strokeWidth = 3.dp.toPx()
        )
    }
}

private fun DrawScope.drawBeachUmbrella(position: Offset) {
    // Umbrella pole
    drawLine(
        color = Color(0xFF8D6E63),
        start = Offset(position.x, position.y),
        end = Offset(position.x, position.y - 25.dp.toPx()),
        strokeWidth = 3.dp.toPx()
    )
    
    // Umbrella top (simplified circle)
    drawCircle(
        color = Color(0xFFF44336),
        radius = 15.dp.toPx(),
        center = Offset(position.x, position.y - 25.dp.toPx())
    )
}
