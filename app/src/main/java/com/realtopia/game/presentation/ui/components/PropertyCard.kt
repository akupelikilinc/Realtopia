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
import androidx.compose.ui.graphics.Brush
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
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    canAfford: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    val propertyGradient = getPropertyGradient(property.type)
    val propertyColor = getPropertyColor(property.type)
    
    Card(
        modifier = modifier
            .width(160.dp)
            .height(200.dp)
            .scale(scale)
            .border(
                width = if (property.isOwned) 3.dp else 1.dp,
                color = if (property.isOwned) GameSuccess else CardBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(propertyGradient),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Property Icon
                    Icon(
                        imageVector = getPropertyIcon(property.type),
                        contentDescription = property.type.displayName,
                        tint = TextOnDark,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Property Name
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextOnDark,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    
                    // Location
                    Text(
                        text = property.location.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextOnDark.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Price Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Current Price
                    Text(
                        text = property.getFormattedPrice(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextOnDark,
                        textAlign = TextAlign.Center
                    )
                    
                    // Price Change
                    if (property.priceChange != 0.0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = property.getPriceChangeIcon(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (property.priceChange > 0) PriceUpColor else PriceDownColor
                            )
                            Text(
                                text = property.getFormattedPriceChangePercentage(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (property.priceChange > 0) PriceUpColor else PriceDownColor
                            )
                        }
                    }
                }
                
                // Action Button
                if (property.isOwned) {
                    Button(
                        onClick = onSellClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SellButtonColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "SAT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextOnDark
                        )
                    }
                } else {
                    Button(
                        onClick = onBuyClick,
                        enabled = canAfford,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) BuyButtonColor else DisabledButtonColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (canAfford) "AL" else "YETERSİZ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextOnDark
                        )
                    }
                }
            }
            
            // Ownership Badge
            if (property.isOwned) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sahip",
                    tint = GameSuccess,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
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

@Composable
private fun getPropertyGradient(propertyType: Property.PropertyType): List<Color> {
    return when (propertyType) {
        Property.PropertyType.APARTMENT -> ApartmentGradient
        Property.PropertyType.HOUSE -> HouseGradient
        Property.PropertyType.VILLA -> VillaGradient
        Property.PropertyType.SHOP -> ShopGradient
        Property.PropertyType.OFFICE -> OfficeGradient
        Property.PropertyType.PLAZA -> PlazaGradient
        Property.PropertyType.SKYSCRAPER -> SkyscraperGradient
    }
}
