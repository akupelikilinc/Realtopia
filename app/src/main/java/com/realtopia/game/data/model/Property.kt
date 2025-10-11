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
    val priceChangePercentage: Double = 0.0
) : Parcelable {
    
    enum class PropertyType(val displayName: String, val basePrice: Double, val riskLevel: Float) {
        HOUSE("Ev", 100.0, 0.1f),
        SHOP("Dükkan", 150.0, 0.15f),
        APARTMENT("Apartman", 200.0, 0.2f),
        OFFICE("Ofis", 300.0, 0.25f)
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
}
