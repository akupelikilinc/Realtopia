package com.realtopia.game.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "properties")
@Parcelize
data class Property(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: PropertyType,
    val price: Double,
    val currentPrice: Double,
    val sellPrice: Double,
    val isOwned: Boolean = false,
    val purchaseDate: Long? = null,
    val gridX: Int,
    val gridY: Int,
    val priceChange: Double = 0.0,
    val priceChangePercentage: Double = 0.0,
    val location: Location = Location.CENTER
) : Parcelable {
    
    enum class PropertyType(
        val displayName: String, 
        val basePrice: Double, 
        val maxPrice: Double,
        val riskLevel: Float,
        val colorHex: String
    ) {
        APARTMENT("Daire", 5000.0, 20000.0, 0.1f, "#4CAF50"),
        HOUSE("Ev", 20000.0, 50000.0, 0.15f, "#2196F3"),
        VILLA("Villa", 50000.0, 150000.0, 0.2f, "#9C27B0"),
        SHOP("Dükkan", 100000.0, 300000.0, 0.25f, "#FF9800"),
        OFFICE("Ofis", 300000.0, 800000.0, 0.3f, "#F44336"),
        PLAZA("Plaza", 800000.0, 2000000.0, 0.35f, "#FFD700"),
        SKYSCRAPER("Gökdelen", 2000000.0, 10000000.0, 0.4f, "#E91E63")
    }
    
    enum class Location(val displayName: String, val priceMultiplier: Double) {
        CENTER("Merkez", 1.0),
        COAST("Sahil", 1.3),
        SUBURB("Şehir Dışı", 0.7)
    }
    
    fun getPriceChangeColor(): String {
        return when {
            priceChange > 0 -> "#4CAF50" // Green
            priceChange < 0 -> "#F44336" // Red
            else -> "#9E9E9E" // Gray
        }
    }
    
    fun getPriceChangeIcon(): String {
        return when {
            priceChange > 0 -> "↗"
            priceChange < 0 -> "↘"
            else -> "→"
        }
    }
    
    fun calculateProfit(): Double {
        return if (isOwned && purchaseDate != null) {
            currentPrice - price
        } else 0.0
    }
    
    fun calculateProfitPercentage(): Double {
        return if (isOwned && purchaseDate != null && price > 0) {
            ((currentPrice - price) / price) * 100
        } else 0.0
    }
    
    fun getFormattedPrice(): String {
        return when {
            currentPrice >= 1000000 -> "₺${(currentPrice / 1000000).toInt()}M"
            currentPrice >= 1000 -> "₺${(currentPrice / 1000).toInt()}K"
            else -> "₺${currentPrice.toInt()}"
        }
    }
    
    fun getFormattedPriceChange(): String {
        val sign = if (priceChange > 0) "+" else ""
        return "$sign₺${priceChange.toInt()}"
    }
    
    fun getFormattedPriceChangePercentage(): String {
        val sign = if (priceChangePercentage > 0) "+" else ""
        return "$sign${priceChangePercentage.toInt()}%"
    }
}
