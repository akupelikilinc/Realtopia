package com.realtopia.game.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.Property
import com.realtopia.game.presentation.ui.theme.*

@Composable
fun PropertyCard(
    property: Property,
    onPropertyClick: () -> Unit,
    onPropertyLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    val borderColor = if (property.isOwned) {
        GameSuccess
    } else {
        when (property.priceChange) {
            in 0.0..Double.MAX_VALUE -> GameSuccess
            in Double.MIN_VALUE..0.0 -> GameError
            else -> MaterialTheme.colorScheme.outline
        }
    }
    
    Card(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onPropertyClick()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (property.isOwned) {
                GameSuccess.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Property Icon
            Icon(
                imageVector = getPropertyIcon(property.type),
                contentDescription = property.type.displayName,
                tint = getPropertyColor(property.type),
                modifier = Modifier.size(24.dp)
            )
            
            // Price
            Text(
                text = "$${property.currentPrice.toInt()}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            // Price Change Indicator
            if (property.priceChange != 0.0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = property.getPriceChangeIcon(),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                        color = Color(android.graphics.Color.parseColor(property.getPriceChangeColor()))
                    )
                    Text(
                        text = "${property.priceChangePercentage.toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(android.graphics.Color.parseColor(property.getPriceChangeColor()))
                    )
                }
            }
            
            // Ownership Indicator
            if (property.isOwned) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sahip",
                    tint = GameSuccess,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
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
