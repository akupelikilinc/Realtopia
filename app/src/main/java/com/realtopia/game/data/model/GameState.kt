package com.realtopia.game.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val balance: Double = 10000.0, // 10K TL başlangıç
    val portfolioValue: Double = 0.0,
    val totalPropertiesOwned: Int = 0,
    val totalPropertiesSold: Int = 0,
    val totalProfit: Double = 0.0,
    val unlockedPropertyTypes: Set<Property.PropertyType> = setOf(Property.PropertyType.APARTMENT),
    val isGamePaused: Boolean = false
) : Parcelable {
    
    enum class LevelTheme {
        URBAN,
        SUBURBAN,
        COASTAL,
        MOUNTAIN,
        DESERT
    }
    
    enum class GameMode {
        CAREER,
        SANDBOX,
        CHALLENGE
    }
    
    fun getBalanceFormatted(): String {
        return when {
            balance >= 1000000 -> "₺${(balance / 1000000).toInt()}M"
            balance >= 1000 -> "₺${(balance / 1000).toInt()}K"
            else -> "₺${balance.toInt()}"
        }
    }
    
    fun getPortfolioValueFormatted(): String {
        return when {
            portfolioValue >= 1000000 -> "₺${(portfolioValue / 1000000).toInt()}M"
            portfolioValue >= 1000 -> "₺${(portfolioValue / 1000).toInt()}K"
            else -> "₺${portfolioValue.toInt()}"
        }
    }
    
    fun getTotalProfitFormatted(): String {
        val sign = if (totalProfit >= 0) "+" else ""
        return when {
            kotlin.math.abs(totalProfit) >= 1000000 -> "$sign₺${(kotlin.math.abs(totalProfit) / 1000000).toInt()}M"
            kotlin.math.abs(totalProfit) >= 1000 -> "$sign₺${(kotlin.math.abs(totalProfit) / 1000).toInt()}K"
            else -> "$sign₺${kotlin.math.abs(totalProfit).toInt()}"
        }
    }
    
    fun canAffordProperty(property: Property): Boolean {
        return balance >= property.currentPrice
    }
    
    fun getNextUnlockablePropertyType(): Property.PropertyType? {
        val allTypes = Property.PropertyType.values()
        val currentIndex = allTypes.indexOfFirst { it in unlockedPropertyTypes }
        return if (currentIndex >= 0 && currentIndex < allTypes.size - 1) {
            allTypes[currentIndex + 1]
        } else null
    }
}
