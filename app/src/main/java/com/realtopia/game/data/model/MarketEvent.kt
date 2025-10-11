package com.realtopia.game.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketEvent(
    val id: String,
    val name: String,
    val description: String,
    val duration: Long, // in milliseconds
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
                    id = "economic_boom",
                    name = "Ekonomik Patlama",
                    description = "Tüm mülk fiyatları %20 artıyor!",
                    duration = 30000, // 30 seconds
                    priceMultiplier = 1.2,
                    affectedPropertyTypes = Property.PropertyType.values().toList(),
                    color = "#4CAF50",
                    icon = "📈"
                ),
                MarketEvent(
                    id = "market_crash",
                    name = "Piyasa Çöküşü",
                    description = "Tüm mülk fiyatları %15 düşüyor!",
                    duration = 30000,
                    priceMultiplier = 0.85,
                    affectedPropertyTypes = Property.PropertyType.values().toList(),
                    color = "#F44336",
                    icon = "📉"
                ),
                MarketEvent(
                    id = "real_estate_boom",
                    name = "Emlak Patlaması",
                    description = "Ev ve apartman fiyatları %25 artıyor!",
                    duration = 45000,
                    priceMultiplier = 1.25,
                    affectedPropertyTypes = listOf(Property.PropertyType.HOUSE, Property.PropertyType.APARTMENT),
                    color = "#2196F3",
                    icon = "🏠"
                ),
                MarketEvent(
                    id = "business_growth",
                    name = "İş Dünyası Büyümesi",
                    description = "Ofis ve dükkan fiyatları %30 artıyor!",
                    duration = 45000,
                    priceMultiplier = 1.3,
                    affectedPropertyTypes = listOf(Property.PropertyType.OFFICE, Property.PropertyType.SHOP),
                    color = "#FF9800",
                    icon = "🏢"
                )
            )
            return events.random()
        }
    }
}
