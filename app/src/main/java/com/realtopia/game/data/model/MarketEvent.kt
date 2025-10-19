package com.realtopia.game.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketEvent(
    val id: String,
    val name: String,
    val description: String,
    val duration: Long = 20000, // 20 saniye
    val priceMultiplier: Double,
    val affectedPropertyTypes: List<Property.PropertyType>,
    val color: String,
    val icon: String,
    val startTime: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) : Parcelable {
    
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - startTime > duration
    }
    
    fun getTimeRemaining(): Long {
        val elapsed = System.currentTimeMillis() - startTime
        return (duration - elapsed).coerceAtLeast(0)
    }
    
    fun getTimeRemainingFormatted(): String {
        val remaining = getTimeRemaining()
        val minutes = (remaining / 60000).toInt()
        val seconds = ((remaining % 60000) / 1000).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    companion object {
        fun createRandomEvent(): MarketEvent {
            val events = listOf(
                MarketEvent(
                    id = "metro_line",
                    name = "Yeni Metro Hattı!",
                    description = "Merkez bölgesi mülkleri %30 artıyor!",
                    priceMultiplier = 1.3,
                    affectedPropertyTypes = listOf(Property.PropertyType.APARTMENT, Property.PropertyType.HOUSE),
                    color = "#4CAF50",
                    icon = "🚇"
                ),
                MarketEvent(
                    id = "economic_crisis",
                    name = "Ekonomik Kriz",
                    description = "Tüm mülk fiyatları %20 düşüyor!",
                    priceMultiplier = 0.8,
                    affectedPropertyTypes = Property.PropertyType.values().toList(),
                    color = "#F44336",
                    icon = "📉"
                ),
                MarketEvent(
                    id = "coastal_development",
                    name = "Sahil Gelişimi",
                    description = "Sahil bölgesi mülkleri %25 artıyor!",
                    priceMultiplier = 1.25,
                    affectedPropertyTypes = listOf(Property.PropertyType.VILLA, Property.PropertyType.HOUSE),
                    color = "#2196F3",
                    icon = "🏖️"
                ),
                MarketEvent(
                    id = "business_district",
                    name = "İş Merkezi Açılışı",
                    description = "Ofis ve plaza fiyatları %35 artıyor!",
                    priceMultiplier = 1.35,
                    affectedPropertyTypes = listOf(Property.PropertyType.OFFICE, Property.PropertyType.PLAZA),
                    color = "#FF9800",
                    icon = "🏢"
                ),
                MarketEvent(
                    id = "shopping_mall",
                    name = "AVM Açılışı",
                    description = "Dükkan fiyatları %40 artıyor!",
                    priceMultiplier = 1.4,
                    affectedPropertyTypes = listOf(Property.PropertyType.SHOP),
                    color = "#9C27B0",
                    icon = "🛍️"
                ),
                MarketEvent(
                    id = "tech_boom",
                    name = "Teknoloji Patlaması",
                    description = "Gökdelen fiyatları %50 artıyor!",
                    priceMultiplier = 1.5,
                    affectedPropertyTypes = listOf(Property.PropertyType.SKYSCRAPER),
                    color = "#E91E63",
                    icon = "🏗️"
                )
            )
            return events.random()
        }
    }
}
