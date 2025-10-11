package com.realtopia.game.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.data.model.GameState

@Composable
fun BottomBar(
    gameMode: GameState.GameMode,
    ownedCount: Int,
    portfolioValue: Double,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Game Mode
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mod: ${getGameModeText(gameMode)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Mülkler: $ownedCount",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
        
        // Portfolio Value
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Portföy",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "$${portfolioValue.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
        
        // Restart Button
        IconButton(
            onClick = onRestartClick,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Yeniden Başlat",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun getGameModeText(gameMode: GameState.GameMode): String {
    return when (gameMode) {
        GameState.GameMode.CAREER -> "KARİYER"
        GameState.GameMode.SANDBOX -> "SERBEST"
        GameState.GameMode.CHALLENGE -> "MEYDAN OKUMA"
    }
}
