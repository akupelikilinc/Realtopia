package com.realtopia.game.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val currentLevel: Int = 1,
    val balance: Double = 35.0,
    val missionTarget: Double = 1000.0,
    val timeLeft: Long = 600000, // 10 minutes in milliseconds
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val gameMode: GameMode = GameMode.CAREER,
    val levelProgress: Float = 0.0f,
    val totalPropertiesOwned: Int = 0,
    val totalPropertiesSold: Int = 0,
    val totalProfit: Double = 0.0,
    val portfolioValue: Double = 0.0,
    val currentTheme: LevelTheme = LevelTheme.URBAN
) : Parcelable {
    
    enum class GameMode {
        CAREER, SANDBOX, CHALLENGE
    }
    
    enum class LevelTheme(
        val displayName: String,
        val backgroundColor: String,
        val groundColor: String
    ) {
        URBAN("Şehir", "#87CEEB", "#4CAF50"),
        BEACH("Sahil", "#87CEEB", "#FFC107"),
        MOUNTAIN("Dağ", "#E0E0E0", "#8D6E63"),
        FOREST("Orman", "#2E7D32", "#4CAF50"),
        DESERT("Çöl", "#FF9800", "#FFC107")
    }
    
    fun getLevelProgressPercentage(): Float {
        return (balance / missionTarget).toFloat().coerceAtMost(1.0f)
    }
    
    fun isMissionCompleted(): Boolean {
        return balance >= missionTarget
    }
    
    fun getTimeLeftFormatted(): String {
        val minutes = (timeLeft / 60000).toInt()
        val seconds = ((timeLeft % 60000) / 1000).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    fun getBalanceFormatted(): String {
        return "$${balance.toInt()}"
    }
    
    fun getMissionTargetFormatted(): String {
        return "$${missionTarget.toInt()}"
    }
}
