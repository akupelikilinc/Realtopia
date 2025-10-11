package com.realtopia.game.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "achievements")
@Parcelize
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val type: AchievementType,
    val targetValue: Double,
    val currentProgress: Double = 0.0,
    val isUnlocked: Boolean = false,
    val isHidden: Boolean = false,
    val rewardAmount: Double = 0.0,
    val rewardDescription: String = "",
    val icon: String = "🏆",
    val unlockedDate: Long? = null
) : Parcelable {
    
    enum class AchievementType {
        TOTAL_BALANCE,
        PROPERTIES_OWNED,
        PROPERTIES_SOLD,
        TOTAL_PROFIT,
        LEVEL_REACHED,
        MARKET_EVENTS,
        INVESTMENT_RETURN,
        PORTFOLIO_VALUE,
        CONSECUTIVE_DAYS,
        SPECIAL_EVENTS
    }
    
    fun getProgressPercentage(): Float {
        return if (targetValue > 0) {
            (currentProgress / targetValue).toFloat().coerceAtMost(1.0f)
        } else 0.0f
    }
    
    fun isCompleted(): Boolean {
        return currentProgress >= targetValue
    }
    
    fun getProgressText(): String {
        return when (type) {
            AchievementType.TOTAL_BALANCE -> "$${currentProgress.toInt()} / $${targetValue.toInt()}"
            AchievementType.PROPERTIES_OWNED -> "${currentProgress.toInt()} / ${targetValue.toInt()}"
            AchievementType.PROPERTIES_SOLD -> "${currentProgress.toInt()} / ${targetValue.toInt()}"
            AchievementType.TOTAL_PROFIT -> "$${currentProgress.toInt()} / $${targetValue.toInt()}"
            AchievementType.LEVEL_REACHED -> "Seviye ${currentProgress.toInt()} / ${targetValue.toInt()}"
            AchievementType.MARKET_EVENTS -> "${currentProgress.toInt()} / ${targetValue.toInt()}"
            AchievementType.INVESTMENT_RETURN -> "${currentProgress.toInt()}% / ${targetValue.toInt()}%"
            AchievementType.PORTFOLIO_VALUE -> "$${currentProgress.toInt()} / $${targetValue.toInt()}"
            AchievementType.CONSECUTIVE_DAYS -> "${currentProgress.toInt()} / ${targetValue.toInt()}"
            AchievementType.SPECIAL_EVENTS -> "${currentProgress.toInt()} / ${targetValue.toInt()}"
        }
    }
    
    companion object {
        fun getDefaultAchievements(): List<Achievement> {
            return listOf(
                Achievement(
                    id = "first_property",
                    title = "İlk Mülk",
                    description = "İlk mülkünü satın al",
                    type = AchievementType.PROPERTIES_OWNED,
                    targetValue = 1.0,
                    rewardAmount = 100.0,
                    rewardDescription = "+$100 bonus bakiye",
                    icon = "🏠"
                ),
                Achievement(
                    id = "property_mogul",
                    title = "Mülk Kralı",
                    description = "10 mülk satın al",
                    type = AchievementType.PROPERTIES_OWNED,
                    targetValue = 10.0,
                    rewardAmount = 500.0,
                    rewardDescription = "+$500 bonus bakiye",
                    icon = "👑"
                ),
                Achievement(
                    id = "millionaire",
                    title = "Milyoner",
                    description = "$10,000 bakiye elde et",
                    type = AchievementType.TOTAL_BALANCE,
                    targetValue = 10000.0,
                    rewardAmount = 1000.0,
                    rewardDescription = "+$1000 bonus bakiye",
                    icon = "💰"
                ),
                Achievement(
                    id = "level_master",
                    title = "Seviye Ustası",
                    description = "Seviye 10'a ulaş",
                    type = AchievementType.LEVEL_REACHED,
                    targetValue = 10.0,
                    rewardAmount = 2000.0,
                    rewardDescription = "+$2000 bonus bakiye",
                    icon = "⭐"
                ),
                Achievement(
                    id = "market_watcher",
                    title = "Piyasa İzleyicisi",
                    description = "5 piyasa olayını deneyimle",
                    type = AchievementType.MARKET_EVENTS,
                    targetValue = 5.0,
                    rewardAmount = 300.0,
                    rewardDescription = "+$300 bonus bakiye",
                    icon = "📊"
                ),
                Achievement(
                    id = "profit_master",
                    title = "Kar Ustası",
                    description = "$5,000 kar elde et",
                    type = AchievementType.TOTAL_PROFIT,
                    targetValue = 5000.0,
                    rewardAmount = 800.0,
                    rewardDescription = "+$800 bonus bakiye",
                    icon = "💎"
                ),
                Achievement(
                    id = "portfolio_manager",
                    title = "Portföy Yöneticisi",
                    description = "$50,000 portföy değeri",
                    type = AchievementType.PORTFOLIO_VALUE,
                    targetValue = 50000.0,
                    rewardAmount = 1500.0,
                    rewardDescription = "+$1500 bonus bakiye",
                    icon = "📈"
                ),
                Achievement(
                    id = "risk_taker",
                    title = "Risk Alıcı",
                    description = "%50 yatırım getirisi elde et",
                    type = AchievementType.INVESTMENT_RETURN,
                    targetValue = 50.0,
                    rewardAmount = 1200.0,
                    rewardDescription = "+$1200 bonus bakiye",
                    icon = "🎯"
                )
            )
        }
    }
}
