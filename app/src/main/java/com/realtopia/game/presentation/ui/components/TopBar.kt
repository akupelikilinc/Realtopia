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

@Composable
fun TopBar(
    balance: Double,
    level: Int,
    timeLeft: Long,
    isPaused: Boolean,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Balance Panel
        InfoPanel(
            icon = Icons.Default.AttachMoney,
            title = "Bakiye",
            value = "$${balance.toInt()}",
            color = MaterialTheme.colorScheme.primary
        )
        
        // Level Panel
        InfoPanel(
            icon = Icons.Default.Star,
            title = "Seviye",
            value = level.toString(),
            color = MaterialTheme.colorScheme.secondary
        )
        
        // Timer Panel
        InfoPanel(
            icon = Icons.Default.AccessTime,
            title = "Süre",
            value = formatTime(timeLeft),
            color = if (timeLeft < 60000) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
        )
        
        // Pause Button
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = if (isPaused) "Devam Et" else "Duraklat",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InfoPanel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = color
            )
        }
    }
}

private fun formatTime(timeLeft: Long): String {
    val minutes = (timeLeft / 60000).toInt()
    val seconds = ((timeLeft % 60000) / 1000).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}
